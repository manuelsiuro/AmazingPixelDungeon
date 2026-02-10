"""Read, write, and extend the items.png sprite sheet."""

import os

from PIL import Image

from .config import SPRITE_SIZE, SHEET_COLS, ITEMS_PNG, OUTPUT_DIR


def read_sheet(path=ITEMS_PNG):
    """Load the existing items.png sprite sheet.

    Returns:
        PIL Image in RGBA mode.
    """
    sheet = Image.open(path).convert("RGBA")
    return sheet


def get_sprite(sheet, index):
    """Extract a single sprite from the sheet by index.

    Args:
        sheet: PIL Image of the full sprite sheet
        index: Sprite index (0-based, left-to-right, top-to-bottom)

    Returns:
        16x16 PIL Image in RGBA mode.
    """
    col = index % SHEET_COLS
    row = index // SHEET_COLS
    x = col * SPRITE_SIZE
    y = row * SPRITE_SIZE
    return sheet.crop((x, y, x + SPRITE_SIZE, y + SPRITE_SIZE))


def replace_sprite(sheet, sprite, index):
    """Paste a 16x16 sprite into the sheet at the given index.

    Args:
        sheet: PIL Image of the full sprite sheet (modified in place)
        sprite: 16x16 PIL Image to insert
        index: Target sprite index

    Returns:
        The modified sheet (same object, also modified in place).
    """
    col = index % SHEET_COLS
    row = index // SHEET_COLS
    x = col * SPRITE_SIZE
    y = row * SPRITE_SIZE

    # Ensure sheet is large enough
    needed_height = (row + 1) * SPRITE_SIZE
    if sheet.size[1] < needed_height:
        sheet = extend_sheet(sheet, rows_needed=row + 1)

    sheet.paste(sprite, (x, y))
    return sheet


def extend_sheet(sheet, rows_needed):
    """Extend the sheet height to accommodate more rows.

    Args:
        sheet: Current sprite sheet
        rows_needed: Total number of rows needed

    Returns:
        New PIL Image with extended height, original content preserved.
    """
    current_width = sheet.size[0]
    new_height = rows_needed * SPRITE_SIZE
    if sheet.size[1] >= new_height:
        return sheet

    new_sheet = Image.new("RGBA", (current_width, new_height), (0, 0, 0, 0))
    new_sheet.paste(sheet, (0, 0))
    return new_sheet


def write_sheet(sheet, path=ITEMS_PNG):
    """Save the sprite sheet as a PNG file.

    Args:
        sheet: PIL Image to save
        path: Output file path
    """
    os.makedirs(os.path.dirname(path), exist_ok=True)
    sheet.save(path, "PNG")
    print(f"Wrote sprite sheet: {path} ({sheet.size[0]}x{sheet.size[1]})")


def generate_preview(originals, generated, labels=None):
    """Generate a side-by-side comparison grid of original vs generated sprites.

    Args:
        originals: Dict of {index: PIL Image} for original sprites
        generated: Dict of {index: PIL Image} for generated sprites
        labels: Optional dict of {index: str} for label text

    Returns:
        PIL Image of the comparison grid.
    """
    indices = sorted(set(originals.keys()) | set(generated.keys()))
    if not indices:
        return Image.new("RGBA", (1, 1), (0, 0, 0, 0))

    # Layout: each row has original (scaled 4x) + gap + generated (scaled 4x)
    scale = 4
    sprite_display = SPRITE_SIZE * scale  # 64px
    gap = 8
    row_height = sprite_display + 4  # small padding
    col_width = sprite_display * 2 + gap

    # Grid layout: 4 items per row
    items_per_row = 4
    num_rows = (len(indices) + items_per_row - 1) // items_per_row
    grid_width = items_per_row * (col_width + gap)
    grid_height = num_rows * (row_height + gap) + gap

    grid = Image.new("RGBA", (grid_width, grid_height), (32, 32, 32, 255))

    for i, idx in enumerate(indices):
        grid_col = i % items_per_row
        grid_row = i // items_per_row
        base_x = grid_col * (col_width + gap) + gap
        base_y = grid_row * (row_height + gap) + gap

        # Original (left)
        if idx in originals:
            orig = originals[idx].resize(
                (sprite_display, sprite_display), Image.NEAREST
            )
            grid.paste(orig, (base_x, base_y), orig)

        # Generated (right)
        if idx in generated:
            gen = generated[idx].resize(
                (sprite_display, sprite_display), Image.NEAREST
            )
            grid.paste(gen, (base_x + sprite_display + gap, base_y), gen)

    return grid


def save_preview(grid, filename="preview_grid.png"):
    """Save a preview grid image to the output directory.

    Args:
        grid: PIL Image of the preview grid
        filename: Output filename
    """
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    path = os.path.join(OUTPUT_DIR, filename)
    grid.save(path, "PNG")
    print(f"Saved preview: {path}")
    return path


def save_individual_sprite(sprite, item_id, filename=None):
    """Save an individual sprite to the output directory.

    Args:
        sprite: 16x16 PIL Image
        item_id: Item identifier string
        filename: Optional custom filename

    Returns:
        Path to saved file.
    """
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    if filename is None:
        filename = f"{item_id.lower()}.png"
    path = os.path.join(OUTPUT_DIR, filename)
    sprite.save(path, "PNG")
    return path
