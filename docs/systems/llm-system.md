# LLM System

The LLM (Large Language Model) system provides on-device AI text generation to enhance game content. It runs a quantized Gemma model locally via [MediaPipe LLM Inference](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android) — no internet connection is required after the initial model download.

## Overview

The system enhances four types of game text:

| Feature | Description | Default |
|---------|-------------|---------|
| **NPC Dialog** | Rewrites NPC quest dialog in each character's voice | On |
| **Floor Narration** | Generates atmospheric text when entering a new floor | On |
| **Item Descriptions** | Adds flavor text to weapon/armor/item descriptions | On |
| **Combat Narration** | Rewrites combat log messages in dark fantasy style | Off |

All features are optional and individually toggleable. When disabled (or when the model isn't loaded), the game uses its original hardcoded text with zero performance impact.

## Architecture

```
Game Code (NPCs, Items, GLog, WndStory, InterlevelScene)
    │
    ▼
LlmTextEnhancer  ◄── Facade: the only class game code calls
    │
    ├── LlmResponseCache  ── Two-tier cache (memory + disk)
    ├── LlmPromptBuilder   ── Constructs prompts per content type
    └── LlmManager         ── MediaPipe LlmInference wrapper
         │
         └── LlmDownloadManager  ── Model download from HuggingFace
```

### Key Design Principles

1. **Never blocks the game thread.** Every `LlmTextEnhancer` method returns immediately — either the fallback text or a cached result. Async generation runs on a background executor.
2. **Cache-first pattern.** On the first call, the original fallback text is returned and an async generation is submitted. The generated result is cached so the *next* time the same text is requested, the enhanced version is returned instantly.
3. **Graceful degradation.** If the model isn't downloaded, isn't loaded, the device lacks RAM, or any error occurs, the game silently falls back to its original text.

## Package Structure

All LLM code lives in `com.watabou.pixeldungeon.llm/`:

| File | Role |
|------|------|
| `LlmTextEnhancer.kt` | **Facade** — all game code calls this. Checks settings, queries cache, submits async generation. |
| `LlmManager.kt` | Wraps MediaPipe `LlmInference`. Manages model lifecycle (load/unload/generate). |
| `LlmDownloadManager.kt` | Downloads model files from HuggingFace with progress tracking and auth token support. |
| `LlmResponseCache.kt` | Two-tier cache (in-memory `ConcurrentHashMap` + disk files). SHA-256 keyed, 7-day TTL, 500 entry limit. |
| `LlmPromptBuilder.kt` | Constructs prompts for each content type with NPC personality definitions. |
| `LlmConfig.kt` | Constants: token limits, timeouts, cache settings, available model definitions. |
| `LlmModelInfo.kt` | Data class describing a downloadable model (id, URL, file size, etc.). |

## Integration Points

The LLM system is called from these locations in the game code:

| Call Site | Method Called | When |
|-----------|-------------|------|
| `Ghost.kt` | `enhanceNpcDialog("sad ghost", ...)` | Player interacts with the Sad Ghost NPC |
| `Blacksmith.kt` | `enhanceNpcDialog("troll blacksmith", ...)` | Player interacts with the Troll Blacksmith |
| `Wandmaker.kt` | `enhanceNpcDialog("old wandmaker", ...)` | Player interacts with the Old Wandmaker |
| `Imp.kt` | `enhanceNpcDialog("ambitious imp", ...)` | Player interacts with the Ambitious Imp |
| `WndStory.kt` | `generateFloorNarration(...)` | Region intro story window is shown |
| `Item.kt` | `enhanceItemInfo(...)` | Player views an item's description |
| `GLog.kt` | `enhanceCombatMessage(...)` | A combat message is logged |
| `InterlevelScene.kt` | `preWarmCache(...)` | Loading screen between floors (pre-generates narration) |

## Data Flow

### Typical Call (e.g., NPC Dialog)

```
1. Ghost.interact() → LlmTextEnhancer.enhanceNpcDialog("sad ghost", "rose_initial", ...)
2. Check: LLM enabled? NPC dialog enabled? Model ready? → if no, return fallbackText
3. Build cache key from (type, npcName, questState, heroClass, depth)
4. Check cache → if hit, return cached text
5. Cache miss:
   a. Build prompt via LlmPromptBuilder.npcDialog(...)
   b. Submit to LlmManager.generateText() on background executor
   c. Return fallbackText immediately (non-blocking)
6. Background: MediaPipe generates response → sanitize → store in cache
7. Next time the same dialog is requested → cache hit → enhanced text returned
```

### Pre-warming

`InterlevelScene` calls `preWarmCache()` during floor transitions. This pre-generates floor narration in the background while the loading screen is shown, so the text is ready by the time `WndStory` displays it.

## Model Management

### Supported Models

Currently one model is configured in `LlmConfig.AVAILABLE_MODELS`:

| Model | Size | Format |
|-------|------|--------|
| Gemma 3 1B IT (int4) | 557 MB | MediaPipe `.task` |

### Model Lifecycle

```
NOT_DOWNLOADED → [download] → DOWNLOADED → [loadModel] → LOADING → READY
                                                                      │
                                                              [unloadModel]
                                                                      │
                                                                      ▼
                                                                  DOWNLOADED
```

States are defined in `LlmManager.ModelState`:
- **NOT_DOWNLOADED** — No model file on disk
- **DOWNLOADED** — Model file exists but not loaded into memory
- **LOADING** — Model is being loaded (on background thread)
- **READY** — Model is loaded and inference is available
- **ERROR** — Loading failed (e.g., insufficient RAM)

### Download Flow

1. User opens **Settings > AI Settings > Manage Models**
2. If no HuggingFace token is saved, `WndHfToken` prompts for one (required for Gemma license)
3. `LlmDownloadManager.download()` streams the file with progress tracking
4. `WndLlmModels` polls `LlmDownloadManager.progress()` in `update()` to animate the progress bar
5. On completion, the `.tmp` file is renamed to the final filename

### Memory Requirements

`LlmManager.hasEnoughMemory()` checks for at least **3 GB total device RAM** before attempting to load a model.

## Settings & Preferences

All LLM preferences are stored via `PixelDungeon` / `Preferences`:

| Preference | Key | Default | Description |
|------------|-----|---------|-------------|
| `llmEnabled` | `llm_enabled` | `false` | Master toggle |
| `llmNpcDialog` | `llm_npc_dialog` | `true` | NPC dialog enhancement |
| `llmNarration` | `llm_narration` | `true` | Floor narration |
| `llmItemDesc` | `llm_item_desc` | `true` | Item descriptions |
| `llmCombatNarration` | `llm_combat_narration` | `false` | Combat log narration |
| `llmSelectedModel` | `llm_selected_model` | `""` | Selected model ID |
| `llmHfToken` | `llm_hf_token` | `""` | HuggingFace auth token |

## UI Windows

| Window | Purpose |
|--------|---------|
| `WndLlmSettings` | Master toggle + per-feature toggles + link to model manager |
| `WndLlmModels` | Model download/delete, progress bar, storage info, cache management |
| `WndHfToken` | Native Android dialog to enter HuggingFace token |

## Prompt Engineering

`LlmPromptBuilder` defines prompts for each content type. NPC prompts include personality definitions:

| NPC | Personality |
|-----|-------------|
| Sad Ghost | Melancholy, ethereal, speaks with ellipses, sorrowful |
| Troll Blacksmith | Gruff, broken common tongue, impatient but fair |
| Old Wandmaker | Wise, slightly confused, formal, polite, scholarly |
| Ambitious Imp | Sly, deal-making, speaks quickly, loves bargains |

All prompts instruct the model to keep responses short (1-3 sentences) and maintain a dark fantasy tone. The `sanitize()` function enforces hard length limits per content type:
- NPC dialog: 500 characters
- Item descriptions: 400 characters
- Combat messages: 200 characters

## Caching

`LlmResponseCache` implements a two-tier cache:

1. **Memory tier** — `ConcurrentHashMap` for fast in-process lookups
2. **Disk tier** — Individual files in `{filesDir}/llm_cache/`, keyed by SHA-256 hash

Configuration (from `LlmConfig`):
- **TTL:** 7 days
- **Max entries:** 500 (in-memory; LRU eviction by timestamp)
- **Key format:** SHA-256 of `type:param1:param2:...`, truncated to 32 hex chars

## Debugging

All LLM activity is logged with tag `LLM` at debug level. Filter logcat:

```bash
adb logcat -s LLM
```

Each public method logs:
- **On entry** — method name and key parameters
- **SKIP** — feature disabled or model not ready
- **CACHE HIT** — result served from cache
- **CACHE MISS** — async generation submitted
- **GENERATED** — async result with length and preview (first 80 chars)
- **GENERATE FAILED** — null result from inference
- **ERROR** — exception in catch block (logged at error level)

## Build Requirements

- **minSdk 24** — Required by MediaPipe tasks-genai
- **Jetifier disabled** — `android.enableJetifier=false` in `gradle.properties` (Jetifier chokes on class file major version 65)
- **Dependency:** `com.google.mediapipe:tasks-genai` in `app/build.gradle`
