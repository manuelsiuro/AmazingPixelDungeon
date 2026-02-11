# LLM Dialog Variety System Design

## Overview

This document designs a system to make NPC dialogs and game text feel unique across sessions, using a tiny on-device 1B parameter model. Every variety mechanism must fit within short prompt strings — no complex state machines, no multi-turn memory, no fine-tuning. All variety comes from **what we inject into the prompt** and **how we key the cache**.

## Critical Constraint: 1B Model Prompt Budget

A 1B model has ~2048 token context. Our prompts must stay under ~300 tokens to leave room for generation. Every word counts. The variety system works by **selecting different short phrases** to inject, not by writing longer prompts.

---

## 1. NPC Personality Dimensions

### Current State
Each NPC has one flat personality string:
```
"You are melancholy and ethereal. You speak with pauses (ellipses)."
```

### New System: Multi-Axis Personality Injection

Replace the single string with a compact multi-axis template. Each axis is a **single word or short phrase** selected per-NPC, combined into one sentence.

#### Axes

| Axis | Purpose | Token Cost | Example Values |
|------|---------|------------|----------------|
| **Voice** | Core speaking style | 3-5 tokens | "whispers brokenly", "barks gruffly", "speaks precisely", "chatters quickly" |
| **Mood** | Current emotional state (can shift) | 2-3 tokens | "sorrowful", "irritable", "wary", "amused", "desperate" |
| **Address** | How they refer to the hero | 2-4 tokens | "dear child", "fool", "young one", "stranger", "honored guest" |
| **Quirk** | Verbal tic or habit | 3-6 tokens | "trails off with '...'", "asks rhetorical questions", "speaks in third person", "mutters curses" |

#### Per-NPC Personality Tables

**Ghost (Sewers)**
| Axis | Default | Wounded Hero | Deep Floor |
|------|---------|-------------|------------|
| Voice | whispers brokenly | moans painfully | wails |
| Mood | sorrowful | pitying | desperate |
| Address | lost soul | poor wounded thing | doomed one |
| Quirk | trails off with '...' | sighs between words | repeats words |

**Blacksmith (Caves)**
| Axis | Default | To Warrior | To Mage |
|------|---------|-----------|---------|
| Voice | barks gruffly | grunts approvingly | snorts dismissively |
| Mood | impatient | respectful | suspicious |
| Address | you | fellow fighter | spellslinger |
| Quirk | bangs hammer for emphasis | nods with respect | eyes wand warily |

**Wandmaker (Prison)**
| Axis | Default | Early Game | Late Game |
|------|---------|-----------|-----------|
| Voice | speaks slowly, precisely | lectures gently | whispers urgently |
| Mood | scholarly | patient | worried |
| Address | young one | my pupil | brave one |
| Quirk | strokes beard thoughtfully | chuckles wisely | grips staff tightly |

**Imp (Metropolis)**
| Axis | Default | Rich Hero | Poor Hero |
|------|---------|----------|-----------|
| Voice | chatters quickly | purrs smoothly | wheedles desperately |
| Mood | scheming | delighted | eager |
| Address | friend | esteemed customer | pal, buddy |
| Quirk | rubs hands together | counts coins in head | glances at your purse |

**VillageElder (Village, depth 0)**
| Axis | Default | To Warrior | To Huntress |
|------|---------|-----------|-------------|
| Voice | speaks warmly | speaks firmly | speaks softly |
| Mood | welcoming | proud | hopeful |
| Address | young adventurer | brave warrior | keen-eyed hunter |
| Quirk | gestures to the village | clasps your shoulder | points toward the wilds |

#### Prompt Template (Compact)

```
You are $npcName. You $voice. Mood: $mood. Call the hero "$address". $quirk.
Speaking to a $heroClass on floor $depth. $situationalLine
Rewrite in your voice, keep quest instructions. Under 3 sentences. Use _underscores_ for emphasis.
Original: "$originalText"
Rewritten:
```

**Token cost**: ~60-80 tokens for the system portion (vs ~45 currently). Worth the ~20 extra tokens for dramatically more variety.

#### Implementation: `NpcPersonality` Data Class

```kotlin
data class NpcPersonality(
    val voice: String,
    val mood: String,
    val address: String,
    val quirk: String
)
```

A `resolvePersonality(npcName, heroClass, depth, hpPercent, gold)` function selects axes based on simple conditionals — no LLM call needed.

---

## 2. Dynamic Game State Context

### Available State (Cheap to Access)

| State | Access | Variety Use |
|-------|--------|-------------|
| `hero.HP / hero.HT` | `Dungeon.hero?.HP` | HP% thresholds change mood |
| `Dungeon.depth` | Direct | Region tone, NPC urgency |
| `Dungeon.gold` | Direct | Imp/shopkeeper react to wealth |
| `hero.lvl` | `Dungeon.hero?.lvl` | Experienced hero gets respect |
| `hero.className()` | `Dungeon.hero?.className()` | Class-specific address |
| `Statistics.enemiesSlain` | Direct | "You've fought many" flavor |
| `hero.belongings.weapon` | Nullable check | React to weapon type |
| `hero.belongings.armor` | Nullable check | React to armor tier |
| `Dungeon.nightMode` | Direct | Night-specific atmosphere |

### Situational Line Injection

Instead of complex state tracking, compute **one short situational line** (10-15 tokens max) based on game state thresholds. This single line is injected into the prompt.

#### Decision Table

```kotlin
fun situationalLine(heroClass: String, hpPercent: Int, depth: Int,
                    gold: Int, enemiesSlain: Int, heroLevel: Int): String {
    return when {
        hpPercent < 20 -> "The hero is gravely wounded, near death."
        hpPercent < 50 -> "The hero looks battered and tired."
        heroLevel >= 20 -> "The hero radiates power and experience."
        gold > 500 -> "The hero's purse is heavy with gold."
        enemiesSlain > 100 -> "The hero is a seasoned killer."
        depth > 20 -> "Few adventurers survive this deep."
        depth == 1 -> "The hero looks fresh and untested."
        else -> "" // No situational line — save tokens
    }
}
```

**Key design choice**: Only ONE situational line is picked (the most dramatic/relevant). Multiple lines would eat too many tokens. The `when` is ordered by priority — near-death overrides everything.

---

## 3. Session Variation Mechanisms

### Problem
The cache key `key("npc", npcName, questState, heroClass, depth)` means the same NPC says the same thing every run for the same class at the same depth.

### Solution: Variation Seed

Add a **variation seed** (0-3) to the cache key, derived from game state that differs between runs.

```kotlin
fun variationSeed(npcName: String, depth: Int): Int {
    // Combine run-unique data into a simple seed
    val runHash = (Statistics.duration.toInt() * 31 +
                   Statistics.enemiesSlain * 17 +
                   Dungeon.gold * 7 +
                   depth * 3)
    return (runHash and 0x7FFFFFFF) % 4  // 0, 1, 2, or 3
}
```

This gives us 4 possible cache slots per NPC interaction. Since the LLM generates different text each time (it's non-deterministic), we get natural variety without any prompt changes.

#### Updated Cache Key
```kotlin
val seed = variationSeed(npcName, depth)
val cacheKey = LlmResponseCache.key("npc", npcName, questState, heroClass, depth.toString(), seed.toString())
```

**Cache impact**: At most 4x the entries for NPC dialog. With MAX_CACHE_ENTRIES = 1000, this is well within budget.

### Variation Prompt Suffix

To nudge the model toward different outputs even with the same base prompt, append a **tone word** derived from the seed:

```kotlin
val toneVariants = arrayOf("", "Be slightly more terse.", "Add a hint of dark humor.", "Be slightly more poetic.")
val toneSuffix = toneVariants[seed]
```

This costs 0-6 extra tokens and pushes the model toward meaningfully different phrasings.

---

## 4. Dialog Category System

### Categories

Each prompt already has an implicit category (greeting, quest offer, thanks, etc.) via the `questState` parameter. Formalize this into a category system that selects different **prompt framings**:

| Category | When Used | Prompt Framing | Max Length |
|----------|-----------|---------------|------------|
| `greeting` | First meeting | "Greet the hero" | 2 sentences |
| `quest_offer` | Offering quest | "Offer a task" | 3 sentences |
| `quest_remind` | Hero returns without completing | "Remind about the task" | 2 sentences |
| `quest_complete` | Hero finishes quest | "Thank and reward" | 2 sentences |
| `warning` | Danger nearby | "Warn urgently" | 1 sentence |
| `lore` | Examining environment | "Share ancient knowledge" | 2 sentences |
| `react_wounded` | Hero HP < 30% | "React to hero's wounds" | 1 sentence |
| `react_strong` | Hero is high level | "Acknowledge hero's power" | 1 sentence |
| `farewell` | Leaving NPC | "Say goodbye" | 1 sentence |

### Category Selection Logic

```kotlin
fun selectCategory(npcName: String, questState: String, hpPercent: Int, heroLevel: Int): String {
    // Reactive categories override quest state when dramatic enough
    if (hpPercent < 30 && questState != "quest_offer") return "react_wounded"

    // Otherwise use the quest-driven category
    return questState
}
```

### Category-Specific Prompt Adjustments

Rather than a single generic "Rewrite this dialog", each category gets a tailored instruction:

```kotlin
fun categoryInstruction(category: String): String = when (category) {
    "greeting" -> "Greet the hero warmly or warily, based on your personality."
    "quest_offer" -> "Offer a task. Keep quest details intact."
    "quest_remind" -> "Remind the hero about the unfinished task. Sound impatient or worried."
    "quest_complete" -> "Thank the hero. Express relief or satisfaction."
    "warning" -> "Warn the hero urgently about danger."
    "react_wounded" -> "React to the hero's visible injuries."
    "farewell" -> "Say a brief goodbye."
    else -> "Speak in your voice."
}
```

**Token cost**: Replaces the existing "Rewrite this dialog in your voice" with equally-sized but more specific instructions. Net zero token change.

---

## 5. Tone Progression by Region

### Regional Tone Words

Inject a **region tone phrase** (3-5 tokens) that shifts the model's output style as the player descends.

```kotlin
fun regionTone(depth: Int): String = when {
    depth == 0  -> "Tone: hopeful, safe"          // Village
    depth <= 5  -> "Tone: uneasy, damp, echoing"  // Sewers
    depth <= 10 -> "Tone: grim, oppressive, cold"  // Prison
    depth <= 15 -> "Tone: primal, echoing, vast"   // Caves
    depth <= 20 -> "Tone: ancient, crumbling, grand" // Metropolis
    else        -> "Tone: dread, apocalyptic, final"  // Demon Halls
}
```

This replaces embedding the region name alone. Instead of "Sewers" (1 token, no mood guidance), we get "Tone: uneasy, damp, echoing" (6 tokens, strong mood guidance).

### NPC Urgency Escalation

NPCs encountered deeper in the dungeon should reflect increasing danger, even if their base personality doesn't change:

```kotlin
fun urgencyModifier(depth: Int): String = when {
    depth <= 5  -> ""
    depth <= 10 -> "The dungeon grows more dangerous."
    depth <= 15 -> "Darkness presses in. Few survive this deep."
    depth <= 20 -> "Ancient evil stirs. Desperation colors every word."
    else        -> "The end is near. Speak as if these may be your last words."
}
```

This applies to atmosphere text (floor narration, level feelings) but **not** to NPC dialogs (where personality axes handle mood shifts).

---

## 6. Hero Class Reactions

### Class-Specific Address Table

Each NPC should address different hero classes differently. This is handled by the `address` axis of the personality system (Section 1), but here's the full matrix:

| NPC | Warrior | Mage | Rogue | Huntress |
|-----|---------|------|-------|----------|
| Ghost | brave one | wise one | shadow walker | keen one |
| Blacksmith | fellow fighter | spellslinger | sneakthief | sharp-eye |
| Wandmaker | strong one | kindred spirit | clever one | nature's child |
| Imp | muscle | magic-user | fellow shadow | hunter |
| VillageElder | brave warrior | learned scholar | silent blade | keen-eyed hunter |

### Class-Specific Situational Lines

In addition to the address change, certain classes trigger unique situational observations:

```kotlin
fun classFlavorLine(heroClass: String, npcName: String): String = when {
    heroClass == "Warrior" && npcName == "troll blacksmith" ->
        "You respect fellow craftsmen of war."
    heroClass == "Mage" && npcName == "old wandmaker" ->
        "You sense a kindred magical spirit."
    heroClass == "Rogue" && npcName == "ambitious imp" ->
        "You recognize a fellow dealer in shadows."
    heroClass == "Huntress" && npcName == "sad ghost" ->
        "The ghost seems drawn to your connection with nature."
    else -> ""
}
```

**Token cost**: 0 tokens when no special affinity exists; 8-12 tokens when it does. Only triggers for specific NPC+class combinations where it adds meaningful flavor.

---

## 7. NPC Memory Simulation

### Problem
NPCs have no actual memory between encounters. The player talks to the Blacksmith, leaves, returns — the Blacksmith has no idea they've met.

### Solution: Encounter Counter in Cache Key

Track a simple **encounter count** per NPC per run. This is a lightweight integer, not complex state:

```kotlin
// In LlmTextEnhancer or a thin wrapper
private val encounterCounts = mutableMapOf<String, Int>()

fun recordEncounter(npcName: String): Int {
    val count = (encounterCounts[npcName] ?: 0) + 1
    encounterCounts[npcName] = count
    return count
}
```

This counter resets each run (it's in-memory only, not saved). It feeds into the prompt:

```kotlin
fun encounterContext(npcName: String, count: Int): String = when (count) {
    1 -> ""  // First meeting — no memory context needed
    2 -> "You've met this hero before. Acknowledge the return briefly."
    3 -> "This hero keeps returning. Show familiarity."
    else -> "You know this hero well now. Speak as to an old acquaintance."
}
```

**Token cost**: 0 tokens on first meeting; 8-12 tokens on subsequent visits. The return on investment is huge — NPCs feel alive.

### Save/Load Consideration

The encounter counter is **intentionally not persisted**. It resets when the app restarts. This is fine because:
1. Bundle API doesn't support the map structure easily
2. The counter is a "feel" mechanism, not gameplay-critical
3. Within a single play session, it creates the illusion of memory

If persistence is later desired, it can be serialized as a simple comma-separated `"ghost:2,blacksmith:1"` string via `Bundle.put(String)`.

---

## 8. Assembled Prompt Template

Here's how all systems combine into the final prompt for NPC dialog:

```
You are $npcName. You $voice. Mood: $mood. Call the hero "$address". $quirk.
$regionTone
$encounterContext $situationalLine $classFlavorLine
$categoryInstruction Keep _underscored_ words. Under $maxSentences sentences.
Original: "$originalText"
Rewritten:
```

### Token Budget Breakdown

| Component | Tokens | Required? |
|-----------|--------|-----------|
| Identity + personality axes | 25-35 | Always |
| Region tone | 5-7 | Always |
| Encounter context | 0-12 | Conditional |
| Situational line | 0-12 | Conditional |
| Class flavor line | 0-12 | Conditional |
| Category instruction | 8-15 | Always |
| Format instructions | 15-20 | Always |
| Original text | 20-60 | Always |
| **Total** | **73-173** | |

Well within the ~300 token budget, even in the worst case where all conditional lines fire.

---

## 9. Implementation Plan

### New Files

| File | Purpose |
|------|---------|
| `llm/NpcPersonality.kt` | Data class + `resolvePersonality()` function |
| `llm/DialogContext.kt` | `situationalLine()`, `regionTone()`, `encounterContext()`, `classFlavorLine()`, `variationSeed()` |

### Modified Files

| File | Changes |
|------|---------|
| `LlmPromptBuilder.kt` | Refactor `npcDialog()` to accept `NpcPersonality` + `DialogContext`. Update all prompt templates to use region tone. |
| `LlmTextEnhancer.kt` | Add encounter tracking. Pass game state to `DialogContext`. Add variation seed to cache keys. |

### No Changes Needed

- Individual NPC files (Ghost.kt, Blacksmith.kt, etc.) — they already pass `npcName`, `questState`, `heroClass`, `depth` to `LlmTextEnhancer`
- `LlmManager.kt` — no changes to inference pipeline
- `LlmResponseCache.kt` — cache key format already supports variable-length keys
- `LlmConfig.kt` — no new settings needed

### Migration Path

1. Add `NpcPersonality` and `DialogContext` as new files
2. Add new overloaded `npcDialog()` in `LlmPromptBuilder` that accepts the new types
3. Update `LlmTextEnhancer.enhanceNpcDialog()` to compute context and call new overload
4. Old prompt method can remain for backward compatibility until all callers migrate
5. Apply region tone to atmosphere prompts (`floorNarration`, `levelFeeling`, etc.)
6. Add variation seed to cache keys across all text types

---

## 10. Non-NPC Text Variety

The same principles apply to other text types with lighter-weight mechanisms:

### Floor Narration
- Region tone (Section 5) provides depth-appropriate atmosphere
- Variation seed (Section 3) ensures different text per run
- Night mode: append "It is night. Darkness is absolute." when `Dungeon.nightMode`

### Item Descriptions
- No personality system needed (items don't talk)
- Variation seed gives different phrasing per run
- Cursed items: "A dark aura clings to it." appended to prompt

### Combat Narration
- Variation seed for per-run variety
- HP-based intensity: "The blow is devastating." when target HP < 20%
- Region tone colors combat descriptions (sewer combat = wet/slimy, demon halls = infernal)

### Boss Encounters
- Boss personalities already work well (Section 1 of current code)
- Add hero class reaction: bosses should acknowledge class
- Add HP-reactive lines for mid-fight dialog (if applicable)

### Bestiary / Mob Descriptions
- Region tone injection
- Variation seed
- Mob state already passed ("sleeping", "hunting", etc.)

---

## 11. Quality Safeguards

### Fallback Chain
1. Try LLM generation with full variety context
2. On failure, return cached result from any prior seed
3. On cache miss, return original fallback text

### Prompt Discipline for 1B Models
- **Never** use complex instructions ("consider the following factors...")
- **Always** use direct, imperative language ("You whisper. Mood: sad.")
- **Avoid** lists in prompts — use single composite sentences
- **Keep** the "Original" text short — truncate to 100 chars if needed
- **End** every prompt with a clear generation trigger word ("Rewritten:", "Enhanced:", etc.)

### Cache Warming Strategy
- Pre-warm with seed=0 on level load (existing `preWarmCache`)
- Don't pre-warm all 4 seeds — let them fill organically
- Priority: boss dialogs (CRITICAL) > NPC greetings (HIGH) > atmosphere (NORMAL) > descriptions (LOW)

---

## 12. Example: Full Ghost Interaction

**Context**: Warrior, floor 3, HP at 40%, second visit, 15 enemies killed.

**Resolved personality**:
- Voice: "whispers brokenly"
- Mood: "pitying" (hero is wounded)
- Address: "brave one" (warrior class)
- Quirk: "sighs between words"

**Computed context**:
- Region tone: "Tone: uneasy, damp, echoing"
- Encounter: "You've met this hero before. Acknowledge the return briefly."
- Situational: "The hero looks battered and tired."
- Class flavor: "" (no special ghost+warrior affinity)
- Category: "react_wounded" (overrides quest state because HP < 30%)

**Assembled prompt** (~140 tokens):
```
You are sad ghost. You whisper brokenly. Mood: pitying. Call the hero "brave one". You sigh between words.
Tone: uneasy, damp, echoing
You've met this hero before. Acknowledge the return briefly. The hero looks battered and tired.
React to the hero's visible injuries. Keep _underscored_ words. Under 2 sentences.
Original: "I can feel it... the _rose_ my love carried... it's somewhere on this floor..."
Rewritten:
```

**Possible output**:
"Brave one... *sigh*... you return, but so wounded... I beg you... find my love's _rose_... before these sewers claim you too..."

Compare to current flat prompt output which would ignore the wounds, the return visit, and the warrior class entirely.

---

## Summary of Key Design Decisions

1. **Multi-axis personality** (voice, mood, address, quirk) instead of flat strings — selected via simple conditionals, not LLM calls
2. **One situational line** per prompt, priority-ordered — most dramatic state wins
3. **Variation seed** (0-3) in cache keys for cross-session variety at ~0 token cost
4. **Region tone phrases** replace raw region names — better mood guidance for 1B models
5. **Encounter counter** (in-memory, not persisted) for NPC memory simulation
6. **Category-specific instructions** replace generic "rewrite" — guides 1B model toward appropriate responses
7. **Hero class address table** — NPCs refer to classes differently, stored as data not LLM-generated
8. **Total prompt overhead**: ~30 extra tokens vs current system for dramatically more variety
9. **No new settings toggles needed** — variety is automatic when LLM is enabled
10. **Two new files** (`NpcPersonality.kt`, `DialogContext.kt`), minor changes to existing `LlmPromptBuilder` and `LlmTextEnhancer`
