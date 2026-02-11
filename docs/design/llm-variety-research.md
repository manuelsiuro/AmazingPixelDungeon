# LLM Output Variety Research

Research compiled for Amazing Pixel Dungeon's on-device LLM text enhancement system.

## Current System Constraints

- **Model**: Gemma 3 1B (4-bit quantized, 557 MB)
- **Runtime**: MediaPipe LlmInference v0.10.27 on Android
- **Max output tokens**: 64-96 (DEFAULT_MAX_TOKENS = 64)
- **Context window**: Small (~2K tokens effective)
- **Current API usage**: Only `setModelPath`, `setMaxTopK(64)`, `setMaxTokens(1024)` at model init
- **No per-request parameters**: `generateResponse(prompt)` takes only a prompt string
- **Cache**: SHA-256 keyed, 7-day TTL, 1000 entry max

### Critical Discovery: MediaPipe Session-Level Parameters

The MediaPipe LlmInference API docs list these per-session parameters:
- `topK` (default 40) - controls sampling diversity
- `temperature` (default 0.8) - controls randomness
- `randomSeed` (default 0) - makes randomness reproducible

**Important**: These may be settable at session creation time, not just model init. The current code only uses `LlmInferenceOptions` (model-level). Investigation needed for whether per-generation-request session options exist in v0.10.27.

---

## Technique 1: Dynamic Context Injection (HIGH IMPACT, EASY)

**What**: Vary the prompt itself each time by injecting dynamic game state as context. Different context = different probability distribution = different output.

**Research basis**: "By changing the context in the prompt, you can tap into different 'slices of knowledge' of the learned probability distribution" (Prompt Engineering Guide).

### Implementation for our system

Inject any/all of these into prompts:
- **Time of day** (dawn/morning/noon/afternoon/dusk/night/midnight)
- **Hero stats** (HP%, hunger level, gold count)
- **Recent events** ("just defeated a gnoll", "just found a potion")
- **Dungeon state** (number of explored rooms, items found, mobs killed)
- **Mood/tone directive** (randomly selected from a pool: "melancholic", "ominous", "wry", "urgent", "sardonic")
- **Weather/atmosphere** (random: "mist hangs low", "torches flicker", "cold draft", "distant rumble")

**Token cost**: 5-15 extra tokens per prompt. Very efficient.

**Example transform**:
```
// BEFORE (always same output)
"Write atmospheric narration for a warrior entering Sewers (floor 2)."

// AFTER (varied output each time)
"Write atmospheric narration for a warrior entering Sewers (floor 2).
Mood: sardonic. Torches flicker. The warrior is wounded (35% HP) and hungry."
```

Each unique combination produces a different cache key, naturally defeating the cache-produces-staleness problem.

---

## Technique 2: Style/Voice Rotation (HIGH IMPACT, EASY)

**What**: Maintain pools of stylistic directives and rotate through them per generation.

**Research basis**: GearHead Caramel's "concept rotation" system uses variant concepts that express the same core idea in different ways, with de-prioritization of recently-used variants.

### Implementation

Create pools for each prompt type:

**NPC voice modifiers** (rotate per interaction):
```kotlin
val VOICE_MODIFIERS = listOf(
    "Speak in short, clipped sentences.",
    "Use a poetic, flowing cadence.",
    "Be gruff and use fragments.",
    "Speak formally with archaic words.",
    "Be conspiratorial, whispering.",
    "Be weary and world-worn."
)
```

**Narration style modifiers**:
```kotlin
val NARRATION_STYLES = listOf(
    "Use sensory details (smell, sound, touch).",
    "Focus on what the hero feels emotionally.",
    "Describe through metaphor and simile.",
    "Use terse, punchy prose.",
    "Describe the environment like a naturalist."
)
```

**Selection strategy**: Track a `lastUsedIndex` per pool and round-robin, or use `(depth + someHash) % pool.size` for deterministic-but-varied selection.

**Token cost**: 5-8 extra tokens. Minimal.

---

## Technique 3: String Seed of Thought (SSoT) (MEDIUM IMPACT, EASY)

**What**: Add an instruction to "generate a random string first, then use it to guide your response." This acts as a pseudo-random seed injected into the generation itself.

**Research basis**: SSoT (Misaki et al., 2025) showed LLMs can use self-generated random strings to break out of deterministic response patterns. "Significantly improves diversity... approaching the ideal performance of a pseudo-random number generator."

### Implementation

Append to any prompt:
```
"First, silently think of a random 4-letter word. Use its mood to color your response."
```

**Caveats for 1B models**: This technique works best on larger models (7B+). For a 1B model:
- Keep the seed instruction very simple (4-letter word, not a full string)
- Test whether the model actually follows through vs. ignoring it
- May consume 10-15 output tokens on the "thinking" step, reducing available tokens for actual content
- **Recommendation**: Test empirically but have fallback to Technique 1/2

---

## Technique 4: Verbalized Sampling (VS) (LOW PRIORITY for 1B)

**What**: Ask the model to generate multiple candidate responses with probabilities, then sample from them.

**Research basis**: CHATS-lab (2025) showed 1.6-2.1x diversity improvement. "Training-free, model-agnostic."

**Why LOW for us**:
- Requires generating 3-5 full responses per request (3-5x token cost)
- Our 64-token limit barely fits ONE response
- 1B models struggle with meta-cognitive tasks like probability estimation
- Latency multiplication unacceptable for game UX

**Possible mini-version**: Ask for 3 one-sentence variants, parse the best one. But risky with 1B model instruction-following.

---

## Technique 5: Prompt Template Rotation (HIGH IMPACT, EASY)

**What**: Don't use one template per prompt type — maintain 3-5 structural variants.

**Research basis**: "Structured prompt templates improve consistency but significantly reduce diversity. Simple steer prompts lead to greater structural variation" (The Price of Format, 2025).

### Implementation

Instead of one `npcDialog()` template, rotate between:

```kotlin
// Variant A: Direct rewrite
"Rewrite in ${npcName}'s voice: \"$original\""

// Variant B: Character continuation
"${npcName} says to the ${heroClass}: "

// Variant C: Scene description
"The ${npcName} looks at the ${heroClass} and speaks: "

// Variant D: Emotional framing
"${npcName}, feeling $mood, tells the ${heroClass}: "
```

**Key insight from research**: Simpler templates produce MORE varied output than complex ones. Our current prompts are quite verbose (40-60 tokens of instructions). Consider shorter variants.

---

## Technique 6: Few-Shot Example Rotation (MEDIUM IMPACT, MODERATE COST)

**What**: Include 1-2 examples in the prompt, but rotate which examples are shown.

**Research basis**: "The model's predictions varied dramatically based on the sequence of examples. The right permutation led to near state-of-the-art performance" (Few-Shot Prompting Guide).

### Implementation

Maintain 5-8 example responses per prompt type, rotate which 1-2 are included:

```kotlin
val COMBAT_EXAMPLES = listOf(
    "Steel bit deep into corrupted flesh.",
    "The blade sang, and darkness answered.",
    "A precise thrust — the creature staggered.",
    "Sparks flew as weapon met armor.",
    "The strike landed true and terrible."
)
```

**Token cost**: 10-20 extra tokens per example. Use ONE example max for our token budget.

**Caveats**: 1B models are highly susceptible to few-shot imitation. Risk of the model just slightly rephrasing the example rather than generating novel text. Mitigate by choosing examples that are structurally different from what you want.

---

## Technique 7: Cache Key Variation (HIGH IMPACT, ZERO PROMPT COST)

**What**: Make cache keys more granular so the same logical request produces multiple cached variants over time.

### Current problem

Cache key: `key("npc", npcName, questState, heroClass, depth)` — same key every time for the same NPC encounter, so the cached response never varies.

### Solution: Epoch-based cache rotation

```kotlin
// Add an "epoch" or "run ID" to cache keys
val epoch = (System.currentTimeMillis() / VARIETY_PERIOD_MS) % NUM_VARIANTS
val cacheKey = LlmResponseCache.key("npc", npcName, questState, heroClass, depth.toString(), epoch.toString())
```

This generates a new response every `VARIETY_PERIOD_MS` (e.g., every 2 hours or every new game run), building up a library of cached variants. Over time, the cache contains multiple responses for each situation.

### Variant selection at display time

When showing text, randomly pick from ALL available cached variants for that key prefix, not just the latest one. This is the "concept pool" approach from GearHead Caramel applied to our cache.

---

## Technique 8: Atmospheric Adjective/Adverb Injection (MEDIUM IMPACT, MINIMAL COST)

**What**: Randomly inject evocative words into prompts to steer generation direction.

### Implementation

```kotlin
val ATMOSPHERES = listOf(
    "eerie", "foreboding", "ancient", "crumbling", "festering",
    "silent", "echoing", "dim", "musty", "damp", "frigid",
    "oppressive", "haunted", "forgotten", "cursed"
)

// Inject 2-3 random atmosphere words
val atmo = ATMOSPHERES.shuffled().take(2).joinToString(", ")
"The $atmo dungeon floor stretches before you..."
```

**Token cost**: 2-5 tokens. Trivial.

---

## Technique 9: Anti-Repetition via Recent Output Tracking (MEDIUM IMPACT, CODE-ONLY)

**What**: Track recently generated phrases and inject a "do not use these words" constraint.

**Research basis**: Presence/frequency penalty is the standard LLM approach. Since we can't set these parameters, we encode the constraint in the prompt.

### Implementation

```kotlin
// Track last 5 generation outputs
val recentOutputs: Queue<String> = LinkedList()

// Extract key phrases from recent outputs
val avoidWords = recentOutputs
    .flatMap { extractKeyPhrases(it) }
    .distinct()
    .take(5)
    .joinToString(", ")

// Add to prompt
"Avoid these words/phrases: $avoidWords"
```

**Token cost**: 10-20 tokens. Acceptable.

**Risk**: 1B models may not reliably follow "avoid X" instructions. More effective: use the avoidance list to SELECT which template/style variant to use rather than encoding it in the prompt.

---

## Technique 10: MediaPipe randomSeed Cycling (POTENTIALLY HIGH IMPACT)

**What**: If the MediaPipe API supports per-session or per-request `randomSeed`, cycle it each call.

### What we know

The MediaPipe docs list `randomSeed` (default 0) as a configuration parameter. The current code doesn't set it. If we can pass different seeds per call, this is the most direct way to get varied output from the same prompt.

### Investigation needed

1. Can `randomSeed` be set per-session (via `LlmInference.createSession()`)?
2. Or only at model-init time (`LlmInferenceOptions`)?
3. Does the default seed=0 mean "random" or "deterministic at 0"?

If per-session: cycle `randomSeed = System.nanoTime().toInt()` before each generation.
If model-level only: reinitializing the model per request is too expensive.

---

## Priority Ranking for Implementation

| Priority | Technique | Impact | Token Cost | Complexity |
|----------|-----------|--------|------------|------------|
| 1 | Dynamic Context Injection | High | 5-15 | Low |
| 2 | Style/Voice Rotation | High | 5-8 | Low |
| 3 | Prompt Template Rotation | High | 0 (shorter) | Low |
| 4 | Cache Key Variation | High | 0 | Low |
| 5 | Atmospheric Word Injection | Medium | 2-5 | Low |
| 6 | MediaPipe randomSeed | Potentially High | 0 | Medium |
| 7 | Few-Shot Example Rotation | Medium | 10-20 | Medium |
| 8 | Anti-Repetition Tracking | Medium | 10-20 | Medium |
| 9 | String Seed of Thought | Medium | 10-15 | Low |
| 10 | Verbalized Sampling | Low (for 1B) | 3-5x | High |

## Recommended Implementation Strategy

### Phase 1: Zero/Low-Cost Wins
- Cache key variation with epoch rotation
- Prompt template rotation (3-5 variants per type)
- Style/voice rotation pools

### Phase 2: Dynamic Context
- Inject game state (HP, hunger, recent events, time-of-day)
- Atmospheric word injection
- Mood directive rotation

### Phase 3: Advanced
- Investigate MediaPipe randomSeed API
- Few-shot example rotation for key prompt types
- Recent output tracking for anti-repetition

## Key Principles for 1B Models

1. **Shorter prompts = more diverse output**. Our current prompts are verbose. Cut instructions.
2. **Context variation > instruction complexity**. Change WHAT you tell the model, not HOW you tell it to be different.
3. **Multiple simple templates > one complex template**. Rotate through simple prompts.
4. **Don't rely on meta-cognition**. 1B models can't reliably "think of a random word" or "avoid these phrases." Use code-level randomization instead.
5. **Cache is your friend, not your enemy**. Build up variant pools in the cache rather than always generating fresh.
