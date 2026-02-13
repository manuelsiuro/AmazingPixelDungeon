#!/usr/bin/env python3
"""Create hand-crafted 16x16 pixel-art storage chest sprites.

Adds item sprites to items.png:
  - STORAGE_CHEST (185)
  - DIMENSIONAL_CHEST (186)
  - EYE_OF_ENDER (187)
"""

import os
from PIL import Image

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.join(SCRIPT_DIR, "..", "..")
ITEMS_PNG = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "items.png")
OUTPUT_DIR = os.path.join(SCRIPT_DIR, "output")

TILE_SIZE = 16
ITEM_COLS = 8   # items.png is 128px wide = 8 columns

# -- Transparent alias --
_ = 0

# ============================================================
# ITEM SPRITES
# ============================================================

# ---- STORAGE_CHEST (185) ----
# Wooden treasure chest with iron bands and a lock, closed
STORAGE_CHEST = (
    "storage_chest", 185,
    {1: (25, 15, 8, 255),       # dark outline
     2: (120, 75, 35, 255),     # wood brown
     3: (85, 50, 22, 255),      # wood shadow
     4: (160, 110, 55, 255),    # wood highlight
     5: (100, 100, 105, 255),   # iron band
     6: (70, 70, 75, 255),      # iron shadow
     7: (140, 140, 150, 255),   # iron highlight / lock
     8: (200, 170, 100, 255)},  # gold lock accent
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
        [_,_,1,4,4,5,4,4,4,5,4,4,4,1,_,_],
        [_,_,1,2,2,5,2,2,2,5,2,2,2,1,_,_],
        [_,_,1,5,5,7,5,5,5,7,5,5,5,1,_,_],
        [_,_,1,2,2,5,2,8,2,5,2,2,2,1,_,_],
        [_,_,1,2,2,5,2,7,2,5,2,2,3,1,_,_],
        [_,_,1,2,2,5,2,2,2,5,2,3,3,1,_,_],
        [_,_,1,3,3,6,3,3,3,6,3,3,3,1,_,_],
        [_,_,1,3,3,6,3,3,3,6,3,3,3,1,_,_],
        [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

# ---- DIMENSIONAL_CHEST (186) ----
# Dark obsidian chest with purple magical glow, ender-style
DIMENSIONAL_CHEST = (
    "dimensional_chest", 186,
    {1: (10, 5, 15, 255),       # dark outline
     2: (35, 20, 45, 255),      # obsidian dark
     3: (25, 12, 30, 255),      # obsidian shadow
     4: (55, 35, 65, 255),      # obsidian mid
     5: (120, 60, 180, 255),    # purple glow
     6: (80, 40, 120, 255),     # purple shadow
     7: (180, 120, 240, 255),   # purple bright
     8: (220, 180, 255, 255)},  # sparkle
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,8,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
        [_,_,1,4,4,5,4,4,4,5,4,4,4,1,_,_],
        [_,_,1,2,2,5,2,7,2,5,2,2,2,1,_,_],
        [_,_,1,6,6,7,6,8,6,7,6,6,6,1,_,_],
        [_,_,1,2,2,5,2,7,2,5,2,2,2,1,_,_],
        [_,_,1,2,2,5,2,5,2,5,2,2,3,1,_,_],
        [_,_,1,2,2,6,2,2,2,6,2,3,3,1,_,_],
        [_,_,1,3,3,6,3,3,3,6,3,3,3,1,_,_],
        [_,_,1,3,3,6,3,3,3,6,3,3,3,1,_,_],
        [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

# ---- EYE_OF_ENDER (187) ----
# Floating green magical eye pearl
EYE_OF_ENDER = (
    "eye_of_ender", 187,
    {1: (10, 30, 15, 255),      # dark outline
     2: (30, 120, 60, 255),     # green base
     3: (20, 80, 40, 255),      # green shadow
     4: (50, 170, 90, 255),     # green highlight
     5: (100, 220, 150, 255),   # bright green glow
     6: (15, 15, 25, 255),      # pupil dark
     7: (180, 240, 200, 255),   # eye sparkle
     8: (220, 255, 230, 255)},  # bright sparkle
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,8,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,1,1,1,1,1,_,_,_,_,_,_],
        [_,_,_,_,1,4,4,5,4,4,1,_,_,_,_,_],
        [_,_,_,1,4,5,4,7,4,5,2,1,_,_,_,_],
        [_,_,_,1,2,4,6,6,6,4,2,1,_,_,_,_],
        [_,_,_,1,2,4,6,8,6,4,3,1,_,_,_,_],
        [_,_,_,1,3,2,6,6,6,2,3,1,_,_,_,_],
        [_,_,_,_,1,3,2,3,2,3,1,_,_,_,_,_],
        [_,_,_,_,_,1,1,1,1,1,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,8,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

ITEM_SPRITES = [STORAGE_CHEST, DIMENSIONAL_CHEST, EYE_OF_ENDER]


def create_sprite(palette, grid):
    """Render a 16x16 pixel-art sprite from a palette-indexed grid."""
    img = Image.new("RGBA", (TILE_SIZE, TILE_SIZE), (0, 0, 0, 0))
    pixels = img.load()
    for y, row in enumerate(grid):
        for x, val in enumerate(row):
            if val != 0 and val in palette:
                pixels[x, y] = palette[val]
    return img


def place_item_sprites():
    """Add storage chest item sprites to items.png."""
    items = Image.open(ITEMS_PNG).convert("RGBA")
    old_w, old_h = items.size
    print(f"  Original items.png: {old_w}x{old_h}")

    # Need indices 185-187 -> row 23 (0-indexed). That's 24 rows = 384px.
    needed_h = (max(idx for _, idx, _, _ in ITEM_SPRITES) // ITEM_COLS + 1) * TILE_SIZE
    new_h = max(old_h, needed_h)
    if new_h > old_h:
        new_items = Image.new("RGBA", (old_w, new_h), (0, 0, 0, 0))
        new_items.paste(items, (0, 0))
        print(f"  Extended items.png from {old_h}px to {new_h}px tall")
    else:
        new_items = items

    for name, index, palette, grid in ITEM_SPRITES:
        sprite = create_sprite(palette, grid)
        # Save individual preview
        out_path = os.path.join(OUTPUT_DIR, f"{name}.png")
        sprite.save(out_path)
        print(f"  Created {out_path}")
        # Place in sheet
        col = index % ITEM_COLS
        row = index // ITEM_COLS
        x = col * TILE_SIZE
        y = row * TILE_SIZE
        new_items.paste(sprite, (x, y))
        print(f"  Placed {name} at index {index} (row={row}, col={col}, x={x}, y={y})")

    new_items.save(ITEMS_PNG)
    final_w, final_h = new_items.size
    print(f"  Saved items.png: {final_w}x{final_h}")


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print("=== Storage Chest Item Sprites ===")
    place_item_sprites()
    print()
    print("Done. All storage chest sprites created.")


if __name__ == "__main__":
    main()
