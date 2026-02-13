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


# ============================================================
# Farmland (index 68) — tilled dirt with visible furrow lines
# ============================================================
FARMLAND_PALETTE = {
    1: (95, 65, 35, 255),       # furrow dark
    2: (130, 90, 50, 255),      # earth base
    3: (110, 75, 40, 255),      # earth shadow
    4: (150, 110, 65, 255),     # earth highlight
    5: (80, 55, 30, 255),       # deep furrow
    6: (140, 100, 58, 255),     # mid earth
    7: (160, 120, 75, 255),     # light top
}

FARMLAND_GRID = [
    [2,2,3,2,2,3,2,2,3,2,2,3,2,2,3,2],
    [4,6,1,4,6,1,4,6,1,4,6,1,4,6,1,4],
    [7,2,5,7,2,5,7,2,5,7,2,5,7,2,5,7],
    [2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2],
    [4,6,5,4,6,5,4,6,5,4,6,5,4,6,5,4],
    [7,2,1,7,2,1,7,2,1,7,2,1,7,2,1,7],
    [2,3,5,2,3,5,2,3,5,2,3,5,2,3,5,2],
    [4,6,1,4,6,1,4,6,1,4,6,1,4,6,1,4],
    [7,2,5,7,2,5,7,2,5,7,2,5,7,2,5,7],
    [2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2],
    [4,6,5,4,6,5,4,6,5,4,6,5,4,6,5,4],
    [7,2,1,7,2,1,7,2,1,7,2,1,7,2,1,7],
    [2,3,5,2,3,5,2,3,5,2,3,5,2,3,5,2],
    [4,6,1,4,6,1,4,6,1,4,6,1,4,6,1,4],
    [7,2,5,7,2,5,7,2,5,7,2,5,7,2,5,7],
    [2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2],
]

# ============================================================
# Hydrated Farmland (index 69) — darker/wetter tilled dirt
# ============================================================
HYDRATED_FARMLAND_PALETTE = {
    1: (60, 45, 25, 255),       # furrow dark (wet)
    2: (85, 60, 35, 255),       # earth base (wet)
    3: (70, 50, 28, 255),       # earth shadow (wet)
    4: (100, 75, 45, 255),      # earth highlight (wet)
    5: (50, 38, 22, 255),       # deep furrow (wet)
    6: (92, 68, 40, 255),       # mid earth (wet)
    7: (110, 85, 55, 255),      # light top (wet)
    8: (75, 65, 55, 255),       # blue-gray moisture tint
}

HYDRATED_FARMLAND_GRID = [
    [2,2,3,2,2,3,2,2,3,2,2,3,2,2,3,2],
    [4,6,1,4,6,1,4,8,1,4,6,1,4,6,1,4],
    [7,2,5,7,2,5,7,2,5,7,2,5,7,2,5,7],
    [2,3,1,2,8,1,2,3,1,2,3,1,2,8,1,2],
    [4,6,5,4,6,5,4,6,5,4,6,5,4,6,5,4],
    [7,2,1,7,2,1,7,2,1,7,8,1,7,2,1,7],
    [2,3,5,2,3,5,2,3,5,2,3,5,2,3,5,2],
    [4,8,1,4,6,1,4,6,1,4,6,1,4,8,1,4],
    [7,2,5,7,2,5,7,8,5,7,2,5,7,2,5,7],
    [2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2],
    [4,6,5,4,8,5,4,6,5,4,6,5,4,6,5,4],
    [7,2,1,7,2,1,7,2,1,7,2,1,7,8,1,7],
    [2,3,5,2,3,5,2,8,5,2,3,5,2,3,5,2],
    [4,6,1,4,6,1,4,6,1,4,8,1,4,6,1,4],
    [7,8,5,7,2,5,7,2,5,7,2,5,7,2,5,7],
    [2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2],
]

# ============================================================
# Enchanting Table (index 66) — dark obsidian/wood table with glowing rune
# ============================================================
ENCHANTING_TABLE_PALETTE = {
    1: (12, 10, 18, 255),       # dark outline
    2: (50, 35, 60, 255),       # dark obsidian base
    3: (35, 25, 42, 255),       # obsidian shadow
    4: (72, 52, 82, 255),       # obsidian highlight
    5: (100, 60, 170, 255),     # rune glow
    6: (150, 100, 220, 255),    # rune bright
    7: (200, 160, 255, 255),    # rune sparkle
    8: (40, 28, 20, 255),       # wood leg dark
}

ENCHANTING_TABLE_GRID = [
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,7,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
    [_,_,1,4,4,4,5,6,5,4,4,4,2,1,_,_],
    [_,_,1,4,5,6,7,5,7,6,5,2,2,1,_,_],
    [_,_,1,4,6,5,6,7,6,5,6,2,3,1,_,_],
    [_,_,1,1,1,1,1,1,1,1,1,1,1,1,_,_],
    [_,_,1,4,2,3,1,_,_,1,4,2,3,1,_,_],
    [_,_,1,4,2,3,1,_,_,1,2,2,3,1,_,_],
    [_,_,1,2,2,3,1,_,_,1,2,3,3,1,_,_],
    [_,_,1,2,3,8,1,_,_,1,3,3,8,1,_,_],
    [_,_,1,3,8,8,1,_,_,1,3,8,8,1,_,_],
    [_,_,1,1,1,1,1,_,_,1,1,1,1,1,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
]

# ============================================================
# Anvil (index 67) — classic dark iron anvil
# ============================================================
ANVIL_PALETTE = {
    1: (12, 12, 18, 255),       # dark outline
    2: (85, 85, 95, 255),       # iron base
    3: (55, 55, 65, 255),       # iron shadow
    4: (120, 120, 135, 255),    # iron highlight
    5: (160, 160, 178, 255),    # specular
    6: (100, 100, 110, 255),    # mid iron
    7: (40, 38, 45, 255),       # base dark
}

ANVIL_GRID = [
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    [_,_,_,_,1,1,1,1,1,1,1,1,_,_,_,_],
    [_,_,1,1,5,5,4,4,4,4,4,2,1,_,_,_],
    [_,1,5,4,4,2,2,2,2,2,2,2,3,1,_,_],
    [_,_,1,1,4,2,2,2,2,2,2,3,1,1,_,_],
    [_,_,_,1,2,2,6,2,2,6,3,3,1,_,_,_],
    [_,_,_,_,1,6,3,3,3,3,3,1,_,_,_,_],
    [_,_,_,_,_,1,3,3,3,3,1,_,_,_,_,_],
    [_,_,_,1,1,1,7,7,7,7,1,1,1,_,_,_],
    [_,_,1,7,7,7,7,7,7,7,7,7,7,1,_,_],
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
    enchanting_table = create_sprite(ENCHANTING_TABLE_PALETTE, ENCHANTING_TABLE_GRID)
    anvil = create_sprite(ANVIL_PALETTE, ANVIL_GRID)
    farmland = create_sprite(FARMLAND_PALETTE, FARMLAND_GRID)
    hydrated_farmland = create_sprite(HYDRATED_FARMLAND_PALETTE, HYDRATED_FARMLAND_GRID)

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

        # Place ENCHANTING_TABLE at index 66 (col=2, row=4 -> x=32, y=64)
        et_col = 66 % COLS  # 2
        et_row = 66 // COLS  # 4
        extended.paste(enchanting_table, (et_col * TILE_SIZE, et_row * TILE_SIZE))

        # Place ANVIL at index 67 (col=3, row=4 -> x=48, y=64)
        a_col = 67 % COLS  # 3
        a_row = 67 // COLS  # 4
        extended.paste(anvil, (a_col * TILE_SIZE, a_row * TILE_SIZE))

        # Place FARMLAND at index 68 (col=4, row=4 -> x=64, y=64)
        fl_col = 68 % COLS  # 4
        fl_row = 68 // COLS  # 4
        extended.paste(farmland, (fl_col * TILE_SIZE, fl_row * TILE_SIZE))

        # Place HYDRATED_FARMLAND at index 69 (col=5, row=4 -> x=80, y=64)
        hf_col = 69 % COLS  # 5
        hf_row = 69 // COLS  # 4
        extended.paste(hydrated_farmland, (hf_col * TILE_SIZE, hf_row * TILE_SIZE))

        extended.save(filepath)
        final_w, final_h = extended.size
        print(f"    -> {final_w}x{final_h} (crafting_table@64, furnace@65, enchanting_table@66, anvil@67, farmland@68, hydrated_farmland@69)")

    print("Done. All tilesets extended.")


if __name__ == "__main__":
    main()
