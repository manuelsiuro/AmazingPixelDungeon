"""Automated quality assurance checks for generated sprites."""

import numpy as np
from PIL import Image

from .config import (
    SPRITE_SIZE,
    ALPHA_OPAQUE, ALPHA_SHADOW, ALPHA_TRANSPARENT,
    QA_MIN_TRANSPARENCY, QA_MAX_TRANSPARENCY,
    QA_MIN_COLORS, QA_MAX_COLORS,
)


def check_dimensions(image):
    """Check that sprite is exactly 16x16 RGBA."""
    issues = []
    if image.size != (SPRITE_SIZE, SPRITE_SIZE):
        issues.append(
            f"Wrong dimensions: {image.size[0]}x{image.size[1]}, "
            f"expected {SPRITE_SIZE}x{SPRITE_SIZE}"
        )
    if image.mode != "RGBA":
        issues.append(f"Wrong mode: {image.mode}, expected RGBA")
    return issues


def check_transparency_ratio(image):
    """Check that transparency ratio is within expected range (45-85%)."""
    issues = []
    data = np.array(image)
    alpha = data[:, :, 3]
    total_pixels = alpha.size
    transparent_pixels = np.sum(alpha == ALPHA_TRANSPARENT)
    ratio = transparent_pixels / total_pixels

    if ratio < QA_MIN_TRANSPARENCY:
        issues.append(
            f"Too little transparency: {ratio:.1%} "
            f"(min {QA_MIN_TRANSPARENCY:.0%}). Sprite may be overfilled."
        )
    if ratio > QA_MAX_TRANSPARENCY:
        issues.append(
            f"Too much transparency: {ratio:.1%} "
            f"(max {QA_MAX_TRANSPARENCY:.0%}). Sprite may be empty."
        )
    return issues


def check_color_count(image):
    """Check that unique non-transparent colors are within range (5-20)."""
    issues = []
    data = np.array(image)
    alpha = data[:, :, 3]

    # Get unique colors of opaque pixels only
    opaque_mask = alpha > 0
    if not opaque_mask.any():
        issues.append("No opaque pixels found — sprite is completely empty")
        return issues

    opaque_pixels = data[opaque_mask][:, :3]  # RGB only
    unique_colors = set(map(tuple, opaque_pixels.tolist()))
    count = len(unique_colors)

    if count < QA_MIN_COLORS:
        issues.append(
            f"Too few colors: {count} (min {QA_MIN_COLORS}). "
            f"Sprite may lack detail."
        )
    if count > QA_MAX_COLORS:
        issues.append(
            f"Too many colors: {count} (max {QA_MAX_COLORS}). "
            f"Palette may not match game style."
        )
    return issues


def check_outline_present(image):
    """Check that semi-transparent outline pixels exist."""
    issues = []
    data = np.array(image)
    alpha = data[:, :, 3]

    shadow_pixels = np.sum(alpha == ALPHA_SHADOW)
    if shadow_pixels == 0:
        issues.append("No semi-transparent outline pixels found (alpha=102)")
    return issues


def check_centering(image):
    """Check that content is not cropped at the edges of the sprite."""
    issues = []
    data = np.array(image)
    alpha = data[:, :, 3]

    if not np.any(alpha > 0):
        return issues  # Empty sprite, handled by other checks

    # Check if content touches the absolute edges (all 4 corners)
    # It's OK to touch edges slightly, but not to have content in all 4 edge rows/cols
    top_row = np.any(alpha[0, :] == ALPHA_OPAQUE)
    bottom_row = np.any(alpha[-1, :] == ALPHA_OPAQUE)
    left_col = np.any(alpha[:, 0] == ALPHA_OPAQUE)
    right_col = np.any(alpha[:, -1] == ALPHA_OPAQUE)

    edges_touching = sum([top_row, bottom_row, left_col, right_col])
    if edges_touching >= 3:
        issues.append(
            f"Content touches {edges_touching}/4 edges — "
            f"sprite may be cropped or not centered"
        )
    return issues


def check_alpha_levels(image):
    """Check that alpha values are properly quantized to 3 levels."""
    issues = []
    data = np.array(image)
    alpha = data[:, :, 3]

    valid_levels = {ALPHA_TRANSPARENT, ALPHA_SHADOW, ALPHA_OPAQUE}
    unique_alphas = set(np.unique(alpha).tolist())
    invalid = unique_alphas - valid_levels

    if invalid:
        issues.append(
            f"Non-standard alpha values found: {sorted(invalid)}. "
            f"Expected only {sorted(valid_levels)}."
        )
    return issues


def run_all_checks(image, item_id="unknown"):
    """Run all QA checks on a sprite.

    Args:
        image: PIL Image (should be 16x16 RGBA)
        item_id: Item identifier for reporting

    Returns:
        (passed, issues) tuple where passed is bool and issues is list of strings.
    """
    all_issues = []

    all_issues.extend(check_dimensions(image))
    all_issues.extend(check_transparency_ratio(image))
    all_issues.extend(check_color_count(image))
    all_issues.extend(check_outline_present(image))
    all_issues.extend(check_centering(image))
    all_issues.extend(check_alpha_levels(image))

    passed = len(all_issues) == 0
    return passed, all_issues


def print_qa_report(results):
    """Print a formatted QA report.

    Args:
        results: Dict of {item_id: (passed, issues)}
    """
    total = len(results)
    passed = sum(1 for p, _ in results.values() if p)
    failed = total - passed

    print(f"\n{'=' * 50}")
    print(f"QA Report: {passed}/{total} passed, {failed} failed")
    print(f"{'=' * 50}")

    if failed > 0:
        print(f"\nFailed sprites:")
        for item_id, (p, issues) in sorted(results.items()):
            if not p:
                print(f"\n  {item_id}:")
                for issue in issues:
                    print(f"    - {issue}")

    if passed == total:
        print("\nAll sprites passed QA checks!")
    print()
