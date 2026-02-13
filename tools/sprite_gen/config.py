"""Configuration constants and prompt templates for sprite generation."""

import os

# --- Paths ---
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
TOOLS_DIR = os.path.dirname(SCRIPT_DIR)
PROJECT_ROOT = os.path.dirname(TOOLS_DIR)
ASSETS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets")
ITEMS_PNG = os.path.join(ASSETS_DIR, "items.png")
OUTPUT_DIR = os.path.join(SCRIPT_DIR, "output")
CATALOG_PATH = os.path.join(SCRIPT_DIR, "items_catalog.json")
PALETTE_CACHE = os.path.join(OUTPUT_DIR, "palette_cache.json")

# --- Sprite Sheet Constants ---
SPRITE_SIZE = 16
SHEET_COLS = 8
SHEET_ROWS = 23  # 23 rows in items.png (184 slots) — extended for crafting sprites
GENERATION_SIZE = 512  # SD 1.5 native resolution; downscaled to 16x16 in post-processing

# --- Model Configurations ---
MODELS = {
    "sd15": {
        "model_id": "PublicPrompts/All-In-One-Pixel-Model",
        "trigger_word": "pixelsprite",
        "dtype": "float16",
        "num_inference_steps": 30,
        "guidance_scale": 7.5,
    },
    "sdxl": {
        "model_id": "stabilityai/stable-diffusion-xl-base-1.0",
        "lora_id": "nerijs/pixel-art-xl",
        "trigger_word": "pixel art",
        "dtype": "float16",
        "num_inference_steps": 30,
        "guidance_scale": 7.5,
    },
}

DEFAULT_MODEL = "sd15"

# --- Prompt Templates ---
# {name} and {prompt_hints} are filled from items_catalog.json
CATEGORY_PROMPTS = {
    "weapon_melee": (
        "{trigger} {name}, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "weapon_missile": (
        "{trigger} {name}, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "armor": (
        "{trigger} {name}, rpg armor icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "wand": (
        "{trigger} {name} wooden wand, magic, glowing tip, "
        "rpg item, centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "ring": (
        "{trigger} {name} ring, jewelry, rpg item, shiny, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "potion": (
        "{trigger} {name} potion bottle, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "scroll": (
        "{trigger} {name}, rolled parchment, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "seed": (
        "{trigger} {name} seed, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "food": (
        "{trigger} {name}, rpg food item, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "key": (
        "{trigger} {name} key, rpg item, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "bag": (
        "{trigger} {name}, rpg container, leather, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "quest": (
        "{trigger} {name}, rpg quest item, fantasy, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "heap": (
        "{trigger} {name}, rpg dungeon object, "
        "centered, pixel art, 16-bit, {prompt_hints}"
    ),
    "misc": (
        "{trigger} {name}, rpg item icon, simple flat colors, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "material": (
        "{trigger} {name}, rpg crafting material, raw resource, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "tool": (
        "{trigger} {name}, rpg utility item, crafted tool, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
    "block": (
        "{trigger} {name}, rpg building block, placeable terrain, "
        "centered, single object, black background, pixel art, 16-bit, {prompt_hints}"
    ),
}

NEGATIVE_PROMPT = (
    "3d render, realistic, blurry, photograph, text, watermark, signature, "
    "smooth, anti-aliased, multiple items, border, frame, background pattern, "
    "gradient, shiny, reflective, detailed shading, complex lighting"
)

# --- Post-Processing ---
# Palette extraction
PALETTE_COLORS = 64  # Max colors in reference palette
SPRITE_MAX_COLORS = 16  # Max colors per sprite after quantization
SPRITE_MIN_COLORS = 5  # Min colors per sprite (QA threshold)

# Alpha levels used in existing sprites
ALPHA_OPAQUE = 255
ALPHA_SHADOW = 102  # 0x66
ALPHA_TRANSPARENT = 0

# Outline color
OUTLINE_COLOR = (0, 0, 0, ALPHA_SHADOW)  # #00000066

# QA thresholds
QA_MIN_TRANSPARENCY = 0.45  # Min fraction of transparent pixels
QA_MAX_TRANSPARENCY = 0.85  # Max fraction of transparent pixels
QA_MIN_COLORS = 5
QA_MAX_COLORS = 20


def build_prompt(category, name, prompt_hints="", model_key=DEFAULT_MODEL):
    """Build a complete generation prompt from category template and item info."""
    template = CATEGORY_PROMPTS.get(category, CATEGORY_PROMPTS["misc"])
    trigger = MODELS[model_key]["trigger_word"]
    prompt = template.format(
        trigger=trigger,
        name=name,
        prompt_hints=prompt_hints,
    )
    # Clean up trailing commas/spaces from empty prompt_hints
    prompt = prompt.rstrip(", ").rstrip()
    return prompt
