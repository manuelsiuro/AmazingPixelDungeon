#!/usr/bin/env python3
"""Generate AI NPC sprite sheet from thief.png with 10 color variants."""

import os
import sys

try:
    from PIL import Image
    import colorsys
except ImportError:
    print("Error: Pillow is required. Install with: pip install Pillow")
    sys.exit(1)

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
ASSETS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets")

INPUT_FILE = os.path.join(ASSETS_DIR, "thief.png")
OUTPUT_FILE = os.path.join(ASSETS_DIR, "ai_npc.png")

FRAME_WIDTH = 12
FRAME_HEIGHT = 13
FRAMES_PER_ROW = 21
ROW_WIDTH = FRAME_WIDTH * FRAMES_PER_ROW  # 252, but image is 256

# Target hues (0-1 scale) for 10 variants
VARIANTS = [
    ("blue",    0.60, 1.0),
    ("green",   0.33, 0.9),
    ("purple",  0.75, 1.0),
    ("red",     0.00, 1.0),
    ("gold",    0.13, 1.0),
    ("cyan",    0.50, 0.9),
    ("orange",  0.08, 1.0),
    ("pink",    0.90, 0.8),
    ("silver",  0.60, 0.2),
    ("teal",    0.45, 0.7),
]


def recolor_row(source_row, target_hue, sat_mult):
    """Recolor clothing pixels in a row of frames."""
    result = source_row.copy()
    pixels = result.load()
    width, height = result.size

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            if a == 0:
                continue

            h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)

            # Identify clothing pixels: brownish/reddish hues with some saturation
            # Thief browns are roughly hue 0-0.12 (reds/oranges/browns) with s > 0.15
            is_clothing = (s > 0.15 and (h < 0.12 or h > 0.92))

            if is_clothing:
                new_s = min(1.0, s * sat_mult)
                nr, ng, nb = colorsys.hsv_to_rgb(target_hue, new_s, v)
                pixels[x, y] = (int(nr * 255), int(ng * 255), int(nb * 255), a)

    return result


def main():
    if not os.path.exists(INPUT_FILE):
        print(f"Error: {INPUT_FILE} not found")
        sys.exit(1)

    source = Image.open(INPUT_FILE).convert("RGBA")
    print(f"Source: {source.size[0]}x{source.size[1]}")

    # Extract first row (thief frames)
    source_row = source.crop((0, 0, source.size[0], FRAME_HEIGHT))

    # Create output image: same width, 10 rows tall
    out_height = FRAME_HEIGHT * len(VARIANTS)
    output = Image.new("RGBA", (source.size[0], out_height), (0, 0, 0, 0))

    for i, (name, hue, sat) in enumerate(VARIANTS):
        print(f"  Generating variant {i}: {name} (hue={hue:.2f}, sat={sat:.1f})")
        recolored = recolor_row(source_row, hue, sat)
        output.paste(recolored, (0, i * FRAME_HEIGHT))

    output.save(OUTPUT_FILE)
    print(f"Output: {OUTPUT_FILE} ({output.size[0]}x{output.size[1]})")
    print(f"  {len(VARIANTS)} variants, {FRAMES_PER_ROW} frames each")


if __name__ == "__main__":
    main()
