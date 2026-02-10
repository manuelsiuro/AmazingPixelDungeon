# Sprite Generation Tool

Build-time tool for generating pixel art item sprites using Stable Diffusion, matching the game's existing art style.

## Purpose

Generate **new item sprites** (weapons, armor, potions, food, scrolls, etc.) from natural language descriptions. All sprites are 16x16 RGBA pixel art, inserted into the game's `items.png` sprite sheet.

## Model Selection

### Primary: `PublicPrompts/All-In-One-Pixel-Model` (SD 1.5)
- ~4 GB download (one-time, cached at `~/.cache/huggingface/`)
- ~4-6 GB runtime memory
- Trigger word: `"pixelsprite"`
- ~10-15 sec per sprite at 512x512 generation resolution on Apple Silicon (MPS)

### Optional: SDXL + `nerijs/pixel-art-xl` LoRA
- ~12 GB download, 8-10 GB runtime
- Higher quality output for showcase sprites

## Tool Architecture

```
tools/
  sprite_gen/
    __init__.py              # Package init
    generate_sprites.py      # CLI entry point (argparse)
    config.py                # Model configs, constants, category prompts
    model_manager.py         # Model download/load/inference
    post_processor.py        # 8-step pixel art pipeline
    sheet_assembler.py       # Read/write/extend items.png
    palette_extractor.py     # Extract palette from existing sprites
    quality_checker.py       # Automated QA checks
    items_catalog.json       # Item definitions with prompt hints
  sprite_gen_requirements.txt
```

## Post-Processing Pipeline

Raw 512x512 diffusion output goes through 8 steps to become a game-ready 16x16 sprite:

1. **Background removal** — flood-fill from corners to detect and zero-out background
2. **Center crop** — find bounding box of opaque content, center within canvas
3. **Downscale** — `Image.resize((16, 16), Image.LANCZOS)` for clean averaged colors
4. **Palette reduction** — median-cut quantize to 12-16 colors per sprite
5. **Reference palette mapping** — snap colors to nearest in extracted game palette (Euclidean RGB distance)
6. **Alpha cleanup** — 3-level quantization: 0 (transparent), 102/0x66 (semi-transparent shadow), 255 (opaque)
7. **Outline generation** — 1px dark semi-transparent (`#00000066`) outline via dilation
8. **Final validation** — dimensions, transparency ratio, color count checks

## Developer Workflow

### Prerequisites

- **Python 3.10+** (3.11 or 3.12 recommended)
- **pip** (included with Python)
- 8+ GB RAM (16+ GB recommended for SDXL model)
- macOS (Apple Silicon MPS), Linux/Windows (NVIDIA CUDA), or CPU fallback

Verify your Python version:
```bash
python3 --version  # Should be 3.10+
```

### Environment Setup (One-Time)

Always use a **virtual environment** to isolate tool dependencies from your system Python. This prevents version conflicts with other projects and keeps the system clean.

```bash
# Create a virtual environment in the tools directory
python3 -m venv tools/sprite_gen/.venv

# Activate it
source tools/sprite_gen/.venv/bin/activate    # macOS / Linux
# tools\sprite_gen\.venv\Scripts\activate     # Windows

# Install dependencies
pip install -r tools/sprite_gen_requirements.txt

# Download the Stable Diffusion model (~4 GB, cached at ~/.cache/huggingface/)
python tools/sprite_gen/generate_sprites.py --setup
```

The `.venv/` directory is local and should not be committed (it's already covered by `.gitignore` patterns for Python venvs).

### Activating the Environment

Every time you open a new terminal to use the tool, activate the venv first:

```bash
source tools/sprite_gen/.venv/bin/activate
```

You'll see `(.venv)` in your prompt when it's active. To deactivate when done:

```bash
deactivate
```

### Upgrading Dependencies

To update packages to their latest compatible versions:

```bash
source tools/sprite_gen/.venv/bin/activate
pip install --upgrade -r tools/sprite_gen_requirements.txt
```

To recreate the environment from scratch (e.g., after a Python version upgrade):

```bash
rm -rf tools/sprite_gen/.venv
python3 -m venv tools/sprite_gen/.venv
source tools/sprite_gen/.venv/bin/activate
pip install -r tools/sprite_gen_requirements.txt
```

### Generate Sprites

```bash
# Preview mode (generates without overwriting items.png)
python tools/sprite_gen/generate_sprites.py --mode all --preview --seed 42

# Generate specific category
python tools/sprite_gen/generate_sprites.py --mode category --category weapon_melee --preview

# Generate single item
python tools/sprite_gen/generate_sprites.py --mode single --item SWORD --seed 42

# Apply to game assets
python tools/sprite_gen/generate_sprites.py --mode all --seed 42 --apply

# Select SDXL model
python tools/sprite_gen/generate_sprites.py --model sdxl --preview

# Run only QA checks
python tools/sprite_gen/generate_sprites.py --qa-only

# One-time model download
python tools/sprite_gen/generate_sprites.py --setup
```

### CLI Flags

| Flag | Description |
|------|-------------|
| `--mode` | `all`, `category`, or `single` |
| `--category` | Category name (e.g., `weapon_melee`, `potion`, `armor`) |
| `--item` | Item ID from catalog (e.g., `SWORD`, `ARMOR_PLATE`) |
| `--model` | `sd15` (default) or `sdxl` |
| `--seed` | Random seed for reproducibility |
| `--device` | `mps`, `cuda`, or `cpu` (auto-detected) |
| `--preview` | Generate preview grid without writing to items.png |
| `--apply` | Write generated sprites to items.png |
| `--batch-size` | Sprites per batch (default: 1) |
| `--setup` | Download model only |
| `--qa-only` | Run QA checks on existing output |

## Adding New Items

### Step-by-Step

1. **Add entry to `items_catalog.json`:**
   ```json
   {
     "id": "OBSIDIAN_DAGGER",
     "index": 128,
     "category": "weapon_melee",
     "name": "obsidian dagger",
     "prompt_hints": "volcanic glass blade, dark, sharp"
   }
   ```

2. **Generate sprite:**
   ```bash
   python tools/sprite_gen/generate_sprites.py --mode single --item OBSIDIAN_DAGGER --preview
   ```

3. **Review preview** at `tools/sprite_gen/output/preview_grid.png`

4. **Apply to sheet:**
   ```bash
   python tools/sprite_gen/generate_sprites.py --mode single --item OBSIDIAN_DAGGER --apply
   ```

5. **Wire into game:**
   ```kotlin
   // ItemSpriteSheet.kt
   const val OBSIDIAN_DAGGER = 128

   // New Item subclass
   class ObsidianDagger : MeleeWeapon() {
       init { image = ItemSpriteSheet.OBSIDIAN_DAGGER }
   }
   ```

### How Sheet Extension Works

- Current `items.png`: 128x256 (8 cols x 16 rows = 128 slots, indices 0-127)
- Adding items beyond index 127 extends the sheet height automatically
- `TextureFilm(texture, 16, 16)` computes rows dynamically — no engine changes needed
- Only `ItemSpriteSheet.kt` needs new `const val` entries

## Prompt Engineering Guide

### Category Templates

Each category has a tuned prompt template with the `pixelsprite` trigger word:

| Category | Template Pattern |
|----------|-----------------|
| `weapon_melee` | `pixelsprite {name}, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `weapon_missile` | `pixelsprite {name}, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `armor` | `pixelsprite {name}, rpg armor icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `potion` | `pixelsprite {color} potion bottle, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `scroll` | `pixelsprite {name}, rolled parchment, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `ring` | `pixelsprite {gem} ring, jewelry, rpg item, shiny, centered, pixel art, 16-bit` |
| `wand` | `pixelsprite {wood} wooden wand, magic, glowing tip, rpg item, centered, pixel art, 16-bit` |
| `food` | `pixelsprite {name}, rpg food item, centered, pixel art, 16-bit` |
| `seed` | `pixelsprite {plant} seed, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |
| `key` | `pixelsprite {material} key, rpg item, centered, pixel art, 16-bit` |
| `misc` | `pixelsprite {name}, rpg item icon, simple flat colors, centered, single object, black background, pixel art, 16-bit` |

### Negative Prompt

Applied to all generations:
```
3d render, realistic, blurry, photograph, text, watermark, signature, smooth, anti-aliased, multiple items, border, frame, background pattern, gradient, shiny, reflective, detailed shading, complex lighting
```

### Auto-Retry on QA Failure

When a generated sprite fails QA checks (transparency ratio, color count, outline, etc.), the tool automatically retries up to 3 times with different seeds (offset +1000 per attempt). This handles occasional bad generations without manual intervention.

### Tips for Good Results

- Keep `prompt_hints` short (2-3 simple physical descriptors): "curved blade, steel, golden hilt" > "curved blade, arabian sword, gleaming steel, golden hilt"
- Use material and color descriptors: "iron", "wooden", "brown", "steel"
- Avoid abstract/mechanic words: "heavy", "two-handed", "entangling" produce noise
- Avoid style modifiers: "fantasy", "metallic", "glowing" cause gradients that downscale poorly
- Seeds ensure reproducibility — record good seeds for items you like

## Quality Checks

Automated QA validates each sprite:

| Check | Criteria |
|-------|----------|
| Dimensions | Exactly 16x16 RGBA |
| Transparency ratio | 45-85% transparent pixels |
| Color count | 5-20 unique non-transparent colors |
| Outline | Semi-transparent outline pixels present |
| Centering | Content not cropped at edges |

## Performance (M4 Pro, MPS)

| Operation | Time |
|-----------|------|
| Single sprite (SD 1.5, 512x512) | ~10-15 sec |
| Full sheet (128 sprites) | ~25-35 min |
| Post-processing per sprite | <100ms |
| Full sheet with QA + retries | ~40 min total |

## Dependencies

See `tools/sprite_gen_requirements.txt`:
- `torch>=2.0.0` — PyTorch for model inference
- `diffusers>=0.25.0` — HuggingFace diffusion pipeline
- `transformers>=4.36.0` — Model tokenizers
- `accelerate>=0.25.0` — Device management
- `safetensors>=0.4.0` — Model weight format
- `Pillow>=10.0.0` — Image processing
- `numpy>=1.24.0` — Array operations
- `scikit-learn>=1.3.0` — K-means for palette extraction
- `scipy>=1.11.0` — Connected-component labeling for background removal
- `tqdm>=4.66.0` — Progress bars

Hardware: 8+ GB RAM recommended. Apple Silicon (MPS), NVIDIA GPU (CUDA), or CPU supported.
