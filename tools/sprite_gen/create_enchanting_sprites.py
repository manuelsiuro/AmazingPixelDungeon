#!/usr/bin/env python3
"""Create hand-crafted 16x16 pixel-art enchanting sprites.

Adds item sprites to items.png:
  - ARCANE_DUST (182)
  - BLANK_TOME (183)
  - ENCHANTED_BOOK (184)

Adds terrain sprites to all 6 tilesets:
  - ENCHANTING_TABLE (66)
  - ANVIL (67)
"""

import os
from PIL import Image

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.join(SCRIPT_DIR, "..", "..")
ITEMS_PNG = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "items.png")
ASSETS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets")
OUTPUT_DIR = os.path.join(SCRIPT_DIR, "output")

TILE_SIZE = 16
ITEM_COLS = 8   # items.png is 128px wide = 8 columns
TILE_COLS = 16  # tilesets are 256px wide = 16 columns

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
# ITEM SPRITES
# ============================================================

# ---- ARCANE_DUST (182) ----
# Sparkling pile of purple-violet magical dust with floating sparkle particles
ARCANE_DUST = (
    "arcane_dust", 182,
    {1: (30, 15, 50, 255),      # dark purple outline
     2: (110, 50, 150, 255),    # purple base
     3: (75, 35, 105, 255),     # purple shadow
     4: (150, 90, 200, 255),    # purple highlight
     5: (220, 180, 255, 255),   # sparkle/glow white
     6: (90, 60, 180, 255),     # blue-purple accent
     7: (180, 140, 230, 255)},  # light purple
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,5,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,5,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,5,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,5,_,_,_,_,_],
        [_,_,_,_,5,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,5,_,7,_,_,_,_,_,_,_],
        [_,_,_,_,_,1,1,1,1,1,_,_,_,_,_,_],
        [_,_,_,_,1,7,5,7,4,7,1,_,_,_,_,_],
        [_,_,_,1,7,4,7,5,7,4,2,1,_,_,_,_],
        [_,_,_,1,4,6,4,7,4,6,3,1,_,_,_,_],
        [_,_,_,_,1,2,6,2,6,3,1,_,_,_,_,_],
        [_,_,_,_,_,1,1,1,1,1,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

# ---- BLANK_TOME (183) ----
# Plain closed leather-bound book with page edges and metal clasp
BLANK_TOME = (
    "blank_tome", 183,
    {1: (30, 20, 10, 255),      # dark outline
     2: (140, 95, 50, 255),     # leather brown
     3: (100, 65, 30, 255),     # leather shadow
     4: (175, 125, 70, 255),    # leather highlight
     5: (225, 215, 195, 255),   # page edge cream
     6: (195, 185, 165, 255),   # page shadow
     7: (155, 155, 160, 255)},  # metal clasp
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,1,1,1,1,1,1,1,1,1,_,_,_,_],
        [_,_,_,1,4,4,4,4,4,4,2,1,_,_,_,_],
        [_,_,_,1,4,4,4,4,4,2,2,1,_,_,_,_],
        [_,_,_,1,4,2,2,7,7,2,3,1,_,_,_,_],
        [_,_,_,1,5,6,5,6,5,6,5,1,_,_,_,_],
        [_,_,_,1,5,5,5,5,5,5,6,1,_,_,_,_],
        [_,_,_,1,2,2,2,2,2,2,3,1,_,_,_,_],
        [_,_,_,1,2,2,2,2,2,3,3,1,_,_,_,_],
        [_,_,_,1,3,3,3,3,3,3,3,1,_,_,_,_],
        [_,_,_,1,1,1,1,1,1,1,1,1,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

# ---- ENCHANTED_BOOK (184) ----
# Magical book with glowing purple rune on cover, radiating energy
ENCHANTED_BOOK = (
    "enchanted_book", 184,
    {1: (20, 12, 35, 255),      # dark outline
     2: (90, 50, 120, 255),     # enchanted leather
     3: (60, 35, 80, 255),      # leather shadow
     4: (130, 80, 160, 255),    # leather highlight
     5: (210, 200, 215, 255),   # page edge
     6: (180, 170, 190, 255),   # page shadow
     7: (160, 120, 220, 255),   # glow mid
     8: (220, 190, 255, 255)},  # glow bright
    [
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,8,_,_,_,_,_,_,_,_],
        [_,_,_,1,1,1,1,1,1,1,1,1,_,_,_,_],
        [_,_,_,1,4,4,4,7,4,4,2,1,_,_,_,_],
        [_,_,_,1,4,4,7,8,7,2,2,1,_,_,_,_],
        [_,_,_,1,4,7,8,7,8,7,3,1,_,_,_,_],
        [_,_,_,1,5,6,7,8,7,6,5,1,_,_,_,_],
        [_,_,_,1,5,5,5,7,5,5,6,1,_,_,_,_],
        [_,_,_,1,2,2,2,2,2,2,3,1,_,_,_,_],
        [_,_,_,1,2,2,2,2,2,3,3,1,_,_,_,_],
        [_,_,_,1,3,3,3,3,3,3,3,1,_,_,_,_],
        [_,_,_,1,1,1,1,1,1,1,1,1,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
        [_,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_],
    ]
)

ITEM_SPRITES = [ARCANE_DUST, BLANK_TOME, ENCHANTED_BOOK]

# ============================================================
# TERRAIN SPRITES
# ============================================================

# ---- ENCHANTING TABLE (terrain index 66) ----
# Dark obsidian/wood table with glowing purple rune circle on top, two legs
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

# ---- ANVIL (terrain index 67) ----
# Classic dark iron anvil — horn, face, waist, and heavy base
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


def place_item_sprites():
    """Add enchanting item sprites to items.png."""
    items = Image.open(ITEMS_PNG).convert("RGBA")
    old_w, old_h = items.size
    print(f"  Original items.png: {old_w}x{old_h}")

    # Need indices 182-184 -> row 22-23 (0-indexed). That's 24 rows = 384px.
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


def place_terrain_sprites():
    """Add enchanting table and anvil terrain sprites to all tilesets."""
    enchanting_table = create_sprite(ENCHANTING_TABLE_PALETTE, ENCHANTING_TABLE_GRID)
    anvil = create_sprite(ANVIL_PALETTE, ANVIL_GRID)

    # Save individual previews
    enchanting_table.save(os.path.join(OUTPUT_DIR, "enchanting_table_terrain.png"))
    anvil.save(os.path.join(OUTPUT_DIR, "anvil_terrain.png"))
    print("  Created terrain previews")

    for filename in TILESET_FILES:
        filepath = os.path.join(ASSETS_DIR, filename)
        tileset = Image.open(filepath).convert("RGBA")
        w, h = tileset.size
        print(f"  {filename}: {w}x{h}")

        # Enchanting table at index 66 (col=2, row=4 -> x=32, y=64)
        et_col = 66 % TILE_COLS  # 2
        et_row = 66 // TILE_COLS  # 4
        tileset.paste(enchanting_table, (et_col * TILE_SIZE, et_row * TILE_SIZE))

        # Anvil at index 67 (col=3, row=4 -> x=48, y=64)
        a_col = 67 % TILE_COLS  # 3
        a_row = 67 // TILE_COLS  # 4
        tileset.paste(anvil, (a_col * TILE_SIZE, a_row * TILE_SIZE))

        tileset.save(filepath)
        print(f"    -> enchanting_table@{66}, anvil@{67}")

    print("  All tilesets updated.")


def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print("=== Enchanting Item Sprites ===")
    place_item_sprites()
    print()
    print("=== Enchanting Terrain Sprites ===")
    place_terrain_sprites()
    print()
    print("Done. All enchanting sprites created.")


if __name__ == "__main__":
    main()
