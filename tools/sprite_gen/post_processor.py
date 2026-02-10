"""8-step post-processing pipeline for transforming diffusion output to game sprites."""

import numpy as np
from PIL import Image

from .config import (
    SPRITE_SIZE, SPRITE_MAX_COLORS,
    ALPHA_OPAQUE, ALPHA_SHADOW, ALPHA_TRANSPARENT,
    OUTLINE_COLOR,
)


def remove_background(image):
    """Step 1: Flood-fill from corners to remove background.

    Detects the background color by sampling corners, then removes all
    connected pixels of that color.
    """
    data = np.array(image)
    h, w = data.shape[:2]

    # Sample corners to detect background color
    corners = [
        data[0, 0],
        data[0, w - 1],
        data[h - 1, 0],
        data[h - 1, w - 1],
    ]

    # Use the most common corner color as background
    # Convert to tuples for counting
    corner_tuples = [tuple(c) for c in corners]
    bg_color = max(set(corner_tuples), key=corner_tuples.count)
    bg_rgb = np.array(bg_color[:3])

    # Threshold for "close enough to background"
    threshold = 30

    # Create mask of background-like pixels
    rgb = data[:, :, :3].astype(float)
    diff = np.sqrt(np.sum((rgb - bg_rgb.astype(float)) ** 2, axis=2))
    bg_mask = diff < threshold

    # Flood fill from corners using connected components
    from scipy import ndimage
    labeled, num_features = ndimage.label(bg_mask)

    # Find labels touching any edge
    edge_labels = set()
    edge_labels.update(labeled[0, :].tolist())      # top row
    edge_labels.update(labeled[-1, :].tolist())      # bottom row
    edge_labels.update(labeled[:, 0].tolist())       # left col
    edge_labels.update(labeled[:, -1].tolist())      # right col
    edge_labels.discard(0)  # 0 means no label

    # Zero out alpha for edge-connected background regions
    for lbl in edge_labels:
        data[labeled == lbl, 3] = 0

    return Image.fromarray(data)


def center_crop(image):
    """Step 2: Find bounding box of opaque content and center within canvas."""
    data = np.array(image)
    alpha = data[:, :, 3]

    # Find bounding box of non-transparent pixels
    rows = np.any(alpha > 0, axis=1)
    cols = np.any(alpha > 0, axis=0)

    if not rows.any() or not cols.any():
        return image  # All transparent, return as-is

    rmin, rmax = np.where(rows)[0][[0, -1]]
    cmin, cmax = np.where(cols)[0][[0, -1]]

    # Crop to content
    content = data[rmin:rmax + 1, cmin:cmax + 1]
    ch, cw = content.shape[:2]

    # Create new canvas and center the content
    h, w = data.shape[:2]
    result = np.zeros_like(data)

    # Calculate centering offsets
    y_off = max(0, (h - ch) // 2)
    x_off = max(0, (w - cw) // 2)

    # Clip content if larger than canvas
    paste_h = min(ch, h - y_off)
    paste_w = min(cw, w - x_off)
    result[y_off:y_off + paste_h, x_off:x_off + paste_w] = content[:paste_h, :paste_w]

    return Image.fromarray(result)


def downscale(image, target_size=SPRITE_SIZE):
    """Step 3: Downscale to target size using nearest-neighbor interpolation."""
    return image.resize((target_size, target_size), Image.NEAREST)


def reduce_palette(image, max_colors=SPRITE_MAX_COLORS):
    """Step 4: Reduce to limited color palette via median-cut quantization."""
    data = np.array(image)
    alpha = data[:, :, 3]

    # Only quantize opaque pixels
    opaque_mask = alpha > 0
    if not opaque_mask.any():
        return image

    # Use PIL's built-in quantize with median cut
    # Convert to RGB for quantization, then re-apply alpha
    rgb_image = image.convert("RGB")
    quantized = rgb_image.quantize(colors=max_colors, method=Image.Quantize.MEDIANCUT)
    quantized_rgb = quantized.convert("RGB")

    # Merge back with original alpha
    result = np.array(quantized_rgb)
    result_rgba = np.dstack([result, alpha])

    return Image.fromarray(result_rgba)


def map_to_reference_palette(image, reference_palette):
    """Step 5: Snap each color to the nearest in the reference palette.

    Uses Euclidean distance in RGB space to find closest match.
    """
    if not reference_palette:
        return image

    data = np.array(image)
    alpha = data[:, :, 3]
    rgb = data[:, :, :3].astype(float)

    ref = np.array(reference_palette, dtype=float)  # (N, 3)

    # For each opaque pixel, find nearest palette color
    opaque_mask = alpha > 0
    if not opaque_mask.any():
        return image

    opaque_pixels = rgb[opaque_mask]  # (M, 3)

    # Compute distances: (M, 1, 3) - (1, N, 3) -> (M, N)
    diffs = opaque_pixels[:, np.newaxis, :] - ref[np.newaxis, :, :]
    distances = np.sqrt(np.sum(diffs ** 2, axis=2))
    nearest_idx = np.argmin(distances, axis=1)

    # Replace colors
    new_rgb = ref[nearest_idx].astype(np.uint8)
    result = data.copy()
    result[:, :, :3][opaque_mask] = new_rgb

    return Image.fromarray(result)


def cleanup_alpha(image):
    """Step 6: Quantize alpha to 3 levels matching existing sprite style."""
    data = np.array(image)
    alpha = data[:, :, 3].astype(float)

    # 3-level quantization:
    # 0-50 -> TRANSPARENT (0)
    # 51-178 -> SHADOW (102)
    # 179-255 -> OPAQUE (255)
    result_alpha = np.zeros_like(alpha, dtype=np.uint8)
    result_alpha[alpha > 178] = ALPHA_OPAQUE
    result_alpha[(alpha > 50) & (alpha <= 178)] = ALPHA_SHADOW
    # Everything else stays 0 (TRANSPARENT)

    data[:, :, 3] = result_alpha
    return Image.fromarray(data)


def add_outline(image):
    """Step 7: Add 1px dark semi-transparent outline around opaque pixels."""
    data = np.array(image)
    h, w = data.shape[:2]
    alpha = data[:, :, 3]

    # Create mask of opaque pixels (alpha == 255)
    opaque = alpha == ALPHA_OPAQUE

    # Dilate the opaque mask by 1 pixel (4-connected neighbors)
    dilated = np.zeros_like(opaque)
    dilated[1:, :] |= opaque[:-1, :]    # shift down
    dilated[:-1, :] |= opaque[1:, :]    # shift up
    dilated[:, 1:] |= opaque[:, :-1]    # shift right
    dilated[:, :-1] |= opaque[:, 1:]    # shift left

    # Outline pixels: in dilated but not in opaque, and currently transparent
    outline_mask = dilated & ~opaque & (alpha == ALPHA_TRANSPARENT)

    # Apply outline color
    data[outline_mask, 0] = OUTLINE_COLOR[0]
    data[outline_mask, 1] = OUTLINE_COLOR[1]
    data[outline_mask, 2] = OUTLINE_COLOR[2]
    data[outline_mask, 3] = OUTLINE_COLOR[3]

    return Image.fromarray(data)


def validate_sprite(image):
    """Step 8: Final validation checks.

    Returns:
        (is_valid, list of issue strings)
    """
    issues = []
    data = np.array(image)

    # Check dimensions
    if image.size != (SPRITE_SIZE, SPRITE_SIZE):
        issues.append(f"Wrong size: {image.size}, expected ({SPRITE_SIZE}, {SPRITE_SIZE})")

    # Check mode
    if image.mode != "RGBA":
        issues.append(f"Wrong mode: {image.mode}, expected RGBA")

    return len(issues) == 0, issues


def process_sprite(raw_image, reference_palette=None):
    """Run the full 8-step post-processing pipeline.

    Args:
        raw_image: PIL Image from diffusion model (e.g., 128x128)
        reference_palette: List of (R, G, B) tuples for palette mapping

    Returns:
        (processed_sprite, issues) - 16x16 RGBA PIL Image and list of issue strings
    """
    image = raw_image.convert("RGBA")

    # Step 1: Remove background
    image = remove_background(image)

    # Step 2: Center content
    image = center_crop(image)

    # Step 3: Downscale to 16x16
    image = downscale(image)

    # Step 4: Reduce palette
    image = reduce_palette(image)

    # Step 5: Map to reference palette
    if reference_palette:
        image = map_to_reference_palette(image, reference_palette)

    # Step 6: Clean up alpha
    image = cleanup_alpha(image)

    # Step 7: Add outline
    image = add_outline(image)

    # Step 8: Validate
    is_valid, issues = validate_sprite(image)

    return image, issues
