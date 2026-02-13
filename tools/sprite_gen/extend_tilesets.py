#!/usr/bin/env python3
"""Extend tile PNGs from 256x64 to 256x256 to support terrain indices 64+.

Adds hand-crafted CRAFTING_TABLE (index 64) and FURNACE (index 65) sprites.
Fills remaining new slots with copies of the EMPTY tile (index 1).
"""

import os
from PIL import Image

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.join(SCRIPT_DIR, "..", "..")
ASSETS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets")

TILE_SIZE = 16
COLS = 16  # 256px / 16px = 16 columns
OLD_ROWS = 4  # original 64px / 16px
NEW_ROWS = 16  # target 256px / 16px

TILESET_FILES = [
    "tiles0.png",
    "tiles1.png",
    "tiles2.png",
    "tiles3.png",
    "tiles4.png",
    "tiles_village.png",
]

# -- Transparent alias --
_ = 0

# ============================================================
# Crafting Table (index 64) — wood-framed workbench with stone top
# ============================================================
CRAFTING_TABLE_PALETTE = {
    1: (20, 18, 15, 255),       # dark outline
    2: (120, 80, 35, 255),      # wood base
    3: (85, 55, 25, 255),       # wood shadow
    4: (155, 110, 55, 255),     # wood highlight
    5: (110, 110, 105, 255),    # stone base
    6: (80, 80, 75, 255),       # stone shadow
    7: (140, 140, 135, 255),    # stone highlight
    8: (55, 40, 20, 255),       # leg shadow
}

CRAFTING_TABLE_GRID = [
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
    [_,_,1,7,7,7,7,7,7,7,7,5,5,1,_,_],
    [_,_,1,7,5,5,7,5,5,7,5,5,6,1,_,_],
    [_,_,1,5,5,6,5,5,6,5,5,6,6,1,_,_],
    [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
    [_,_,1,4,4,2,1,_,_,1,4,2,2,1,_,_],
    [_,_,1,4,2,3,1,_,_,1,2,2,3,1,_,_],
    [_,_,1,2,2,3,1,_,_,1,2,3,3,1,_,_],
    [_,_,1,2,3,3,1,_,_,1,3,3,8,1,_,_],
    [_,_,1,3,3,8,1,_,_,1,3,8,8,1,_,_],
    [_,_,1,1,1,1,1,_,_,1,1,1,1,1,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
]

# ============================================================
# Furnace (index 65) — brick furnace with central fire opening
# ============================================================
FURNACE_PALETTE = {
    1: (20, 15, 12, 255),       # dark outline
    2: (140, 70, 40, 255),      # brick base
    3: (100, 50, 28, 255),      # brick shadow
    4: (170, 95, 55, 255),      # brick highlight
    5: (180, 40, 15, 255),      # fire dark
    6: (240, 180, 30, 255),     # flame yellow
    7: (250, 120, 20, 255),     # flame orange
    8: (85, 65, 50, 255),       # mortar line
}

FURNACE_GRID = [
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,6,_,_,_,_,_,_,_,_],
    [_,_,1,1,1,1,1,7,1,1,1,1,1,1,_,_],
    [_,_,1,4,4,2,8,4,8,2,4,4,2,1,_,_],
    [_,_,1,2,2,3,8,2,8,3,2,2,3,1,_,_],
    [_,_,1,8,8,8,8,8,8,8,8,8,8,1,_,_],
    [_,_,1,4,4,2,1,1,1,2,4,4,2,1,_,_],
    [_,_,1,2,2,3,1,6,1,3,2,2,3,1,_,_],
    [_,_,1,4,2,3,1,7,1,3,2,4,3,1,_,_],
    [_,_,1,2,3,3,1,5,1,3,3,2,3,1,_,_],
    [_,_,1,8,8,8,8,8,8,8,8,8,8,1,_,_],
    [_,_,1,4,2,3,8,2,8,3,2,4,3,1,_,_],
    [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
]


def create_sprite(palette, grid):
    """Render a 16x16 pixel-art sprite from a palette-indexed grid."""
    img = Image.new("RGBA", (TILE_SIZE, TILE_SIZE), (0, 0, 0, 0))
    pixels = img.load()
    for y, row in enumerate(grid):
        for x, val in enumerate(row):
            if val != 0 and val in palette:
                pixels[x, y] = palette[val]
    return img


def main():
    crafting_table = create_sprite(CRAFTING_TABLE_PALETTE, CRAFTING_TABLE_GRID)
    furnace = create_sprite(FURNACE_PALETTE, FURNACE_GRID)

    for filename in TILESET_FILES:
        filepath = os.path.join(ASSETS_DIR, filename)
        original = Image.open(filepath).convert("RGBA")
        old_w, old_h = original.size
        print(f"  {filename}: {old_w}x{old_h}")

        # Create new 256x256 image
        new_w = COLS * TILE_SIZE   # 256
        new_h = NEW_ROWS * TILE_SIZE  # 256
        extended = Image.new("RGBA", (new_w, new_h), (0, 0, 0, 0))

        # Paste original content at top
        extended.paste(original, (0, 0))

        # Extract the EMPTY tile (index 1 = col 1, row 0)
        empty_x = 1 * TILE_SIZE  # 16
        empty_y = 0 * TILE_SIZE  # 0
        empty_tile = original.crop((empty_x, empty_y, empty_x + TILE_SIZE, empty_y + TILE_SIZE))

        # Fill new rows (4-15) with EMPTY tile
        for row in range(OLD_ROWS, NEW_ROWS):
            for col in range(COLS):
                px = col * TILE_SIZE
                py = row * TILE_SIZE
                extended.paste(empty_tile, (px, py))

        # Place CRAFTING_TABLE at index 64 (col=0, row=4 -> x=0, y=64)
        ct_col = 64 % COLS  # 0
        ct_row = 64 // COLS  # 4
        extended.paste(crafting_table, (ct_col * TILE_SIZE, ct_row * TILE_SIZE))

        # Place FURNACE at index 65 (col=1, row=4 -> x=16, y=64)
        f_col = 65 % COLS  # 1
        f_row = 65 // COLS  # 4
        extended.paste(furnace, (f_col * TILE_SIZE, f_row * TILE_SIZE))

        extended.save(filepath)
        final_w, final_h = extended.size
        print(f"    -> {final_w}x{final_h} (crafting_table@{64}, furnace@{65})")

    print("Done. All tilesets extended.")


if __name__ == "__main__":
    main()
