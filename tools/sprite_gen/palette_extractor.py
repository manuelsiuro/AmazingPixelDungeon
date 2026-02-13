"""Extract reference color palette from existing items.png sprite sheet."""

import json
import os

import numpy as np
from PIL import Image
from sklearn.cluster import KMeans

from .config import (
    ITEMS_PNG, PALETTE_CACHE, PALETTE_COLORS, OUTPUT_DIR,
    SPRITE_SIZE, SHEET_COLS,
)

# Category index ranges for sub-palette extraction
CATEGORY_RANGES = {
    "weapon": [2, 15, 16, 17, 18, 19, 20, 21, 22, 23, 29, 30,
               106, 107, 108, 109, 110],
    "armor": [24, 25, 26, 27, 28, 96, 97, 98, 99],
    "wand": [3, 48, 49, 50, 51, 52, 53, 54, 55, 68, 69, 70, 71],
    "ring": [32, 33, 34, 35, 36, 37, 38, 39, 72, 73, 74, 75],
    "potion": [56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67],
    "scroll": [40, 41, 42, 43, 44, 45, 46, 47, 76, 77, 78, 79],
    "seed": [88, 89, 90, 91, 92, 93, 94, 95],
    "food": [4, 112, 113, 114, 115, 116],
    "material": [156, 157, 158, 159, 160, 161, 162, 163, 164, 165],
    "tool": [168, 169, 171],
    "block": [166, 167],
}


def _extract_opaque_pixels(image):
    """Extract RGB values of all non-transparent pixels from an RGBA image."""
    data = np.array(image)
    # Mask for opaque pixels (alpha > 0)
    mask = data[:, :, 3] > 0
    rgb = data[:, :, :3][mask]
    return rgb


def _extract_sprite(sheet, index):
    """Extract a single 16x16 sprite from the sheet by index."""
    col = index % SHEET_COLS
    row = index // SHEET_COLS
    x = col * SPRITE_SIZE
    y = row * SPRITE_SIZE
    return sheet.crop((x, y, x + SPRITE_SIZE, y + SPRITE_SIZE))


def _cluster_colors(pixels, n_colors):
    """Cluster pixel colors using K-means, return palette as list of RGB tuples."""
    if len(pixels) == 0:
        return []
    if len(pixels) < n_colors:
        n_colors = max(1, len(pixels))
    kmeans = KMeans(n_clusters=n_colors, random_state=42, n_init=10)
    kmeans.fit(pixels)
    centers = kmeans.cluster_centers_.astype(int)
    return [tuple(int(v) for v in c) for c in centers]


def extract_full_palette(items_path=ITEMS_PNG, n_colors=PALETTE_COLORS):
    """Extract the global reference palette from the full sprite sheet.

    Returns:
        List of (R, G, B) tuples representing the reference palette.
    """
    sheet = Image.open(items_path).convert("RGBA")
    pixels = _extract_opaque_pixels(sheet)
    palette = _cluster_colors(pixels, n_colors)
    return palette


def extract_category_palettes(items_path=ITEMS_PNG, n_colors=16):
    """Extract per-category sub-palettes from specific sprite indices.

    Returns:
        Dict mapping category name to list of (R, G, B) tuples.
    """
    sheet = Image.open(items_path).convert("RGBA")
    category_palettes = {}

    for category, indices in CATEGORY_RANGES.items():
        all_pixels = []
        for idx in indices:
            sprite = _extract_sprite(sheet, idx)
            pixels = _extract_opaque_pixels(sprite)
            if len(pixels) > 0:
                all_pixels.append(pixels)
        if all_pixels:
            combined = np.vstack(all_pixels)
            palette = _cluster_colors(combined, n_colors)
            category_palettes[category] = palette

    return category_palettes


def extract_and_cache(items_path=ITEMS_PNG, force=False):
    """Extract palettes and cache to JSON for reuse across runs.

    Returns:
        (global_palette, category_palettes) tuple
    """
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    if not force and os.path.exists(PALETTE_CACHE):
        with open(PALETTE_CACHE, "r") as f:
            cached = json.load(f)
        global_palette = [tuple(c) for c in cached["global"]]
        category_palettes = {
            k: [tuple(c) for c in v] for k, v in cached["categories"].items()
        }
        print(f"Loaded cached palette ({len(global_palette)} global colors)")
        return global_palette, category_palettes

    print("Extracting palette from items.png...")
    global_palette = extract_full_palette(items_path)
    category_palettes = extract_category_palettes(items_path)

    # Cache to JSON
    cache_data = {
        "global": [list(c) for c in global_palette],
        "categories": {
            k: [list(c) for c in v] for k, v in category_palettes.items()
        },
    }
    with open(PALETTE_CACHE, "w") as f:
        json.dump(cache_data, f, indent=2)

    print(f"Extracted palette: {len(global_palette)} global colors, "
          f"{len(category_palettes)} categories")
    return global_palette, category_palettes
