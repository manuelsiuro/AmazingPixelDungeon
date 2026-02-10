#!/usr/bin/env python3
"""CLI entry point for sprite generation.

Usage:
    # One-time setup (downloads model)
    python tools/sprite_gen/generate_sprites.py --setup

    # Preview mode (generates without overwriting items.png)
    python tools/sprite_gen/generate_sprites.py --mode all --preview --seed 42

    # Generate specific category
    python tools/sprite_gen/generate_sprites.py --mode category --category weapon_melee --preview

    # Generate single item
    python tools/sprite_gen/generate_sprites.py --mode single --item SWORD --seed 42

    # Apply to game assets
    python tools/sprite_gen/generate_sprites.py --mode all --seed 42 --apply

    # Select model
    python tools/sprite_gen/generate_sprites.py --model sdxl --preview

    # Run QA checks only on existing output
    python tools/sprite_gen/generate_sprites.py --qa-only
"""

import argparse
import json
import os
import sys
import time

# Ensure the tools directory is on the path for imports
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
TOOLS_DIR = os.path.dirname(SCRIPT_DIR)
if TOOLS_DIR not in sys.path:
    sys.path.insert(0, TOOLS_DIR)

from sprite_gen.config import (
    CATALOG_PATH, OUTPUT_DIR, ITEMS_PNG, DEFAULT_MODEL, MODELS,
    CATEGORY_PROMPTS, NEGATIVE_PROMPT, build_prompt,
)
from sprite_gen.palette_extractor import extract_and_cache
from sprite_gen.post_processor import process_sprite
from sprite_gen.sheet_assembler import (
    read_sheet, get_sprite, replace_sprite, write_sheet,
    generate_preview, save_preview, save_individual_sprite,
)
from sprite_gen.quality_checker import run_all_checks, print_qa_report


def load_catalog():
    """Load the items catalog from JSON."""
    with open(CATALOG_PATH, "r") as f:
        items = json.load(f)
    return {item["id"]: item for item in items}


def filter_catalog(catalog, mode, category=None, item_id=None):
    """Filter catalog items based on mode and options.

    Returns:
        Dict of {item_id: item_dict} for items to process.
    """
    if mode == "single":
        if item_id not in catalog:
            print(f"Error: Item '{item_id}' not found in catalog.")
            print(f"Available items: {', '.join(sorted(catalog.keys()))}")
            sys.exit(1)
        return {item_id: catalog[item_id]}
    elif mode == "category":
        filtered = {
            k: v for k, v in catalog.items()
            if v["category"] == category
        }
        if not filtered:
            categories = sorted(set(v["category"] for v in catalog.values()))
            print(f"Error: No items found in category '{category}'.")
            print(f"Available categories: {', '.join(categories)}")
            sys.exit(1)
        return filtered
    else:  # mode == "all"
        return catalog


def generate_single(pipeline, device, item, model_key, seed, reference_palette):
    """Generate and post-process a single sprite.

    Returns:
        (sprite, qa_passed, qa_issues)
    """
    prompt = build_prompt(
        category=item["category"],
        name=item["name"],
        prompt_hints=item.get("prompt_hints", ""),
        model_key=model_key,
    )

    print(f"  Prompt: {prompt[:80]}...")

    from sprite_gen.model_manager import generate_image
    raw_image = generate_image(
        pipeline, prompt, device,
        seed=seed, model_key=model_key,
    )

    # Save raw image for debugging
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    raw_path = os.path.join(OUTPUT_DIR, f"{item['id'].lower()}_raw.png")
    raw_image.save(raw_path, "PNG")

    # Post-process
    sprite, proc_issues = process_sprite(raw_image, reference_palette)

    # QA check
    qa_passed, qa_issues = run_all_checks(sprite, item["id"])
    all_issues = proc_issues + qa_issues

    return sprite, qa_passed, all_issues


def cmd_setup(args):
    """Handle --setup: download model only."""
    from sprite_gen.model_manager import setup_model
    setup_model(args.model)
    print("Setup complete.")


def cmd_qa_only(args):
    """Handle --qa-only: run QA checks on existing output sprites."""
    catalog = load_catalog()
    results = {}

    for item_id, item in catalog.items():
        sprite_path = os.path.join(OUTPUT_DIR, f"{item_id.lower()}.png")
        if not os.path.exists(sprite_path):
            continue
        from PIL import Image
        sprite = Image.open(sprite_path).convert("RGBA")
        passed, issues = run_all_checks(sprite, item_id)
        results[item_id] = (passed, issues)

    if not results:
        print("No sprites found in output directory. Generate some first.")
        sys.exit(1)

    print_qa_report(results)


def cmd_generate(args):
    """Handle generation modes: all, category, single."""
    catalog = load_catalog()
    items_to_generate = filter_catalog(
        catalog, args.mode,
        category=args.category,
        item_id=args.item,
    )

    print(f"\nGenerating {len(items_to_generate)} sprite(s) "
          f"with model '{args.model}' on {args.device or 'auto'}...")
    print(f"Seed: {args.seed}")

    # Extract reference palette
    if os.path.exists(ITEMS_PNG):
        global_palette, category_palettes = extract_and_cache()
    else:
        print("Warning: items.png not found, skipping palette reference")
        global_palette = None
        category_palettes = {}

    # Load model
    from sprite_gen.model_manager import load_pipeline
    pipeline, device = load_pipeline(args.model, args.device)

    # Load existing sheet for comparison
    sheet = None
    if os.path.exists(ITEMS_PNG):
        sheet = read_sheet()

    generated_sprites = {}
    original_sprites = {}
    qa_results = {}
    start_time = time.time()

    for i, (item_id, item) in enumerate(items_to_generate.items()):
        idx = item["index"]
        print(f"\n[{i + 1}/{len(items_to_generate)}] {item_id} (index {idx})")

        # Choose palette: category-specific if available, else global
        cat_key = item["category"].split("_")[0]  # "weapon_melee" -> "weapon"
        palette = category_palettes.get(cat_key, global_palette)

        # Use item-specific seed for reproducibility
        item_seed = None
        if args.seed is not None:
            item_seed = args.seed + idx

        sprite, qa_passed, issues = generate_single(
            pipeline, device, item, args.model, item_seed, palette
        )

        generated_sprites[idx] = sprite
        qa_results[item_id] = (qa_passed, issues)

        # Save individual sprite
        save_individual_sprite(sprite, item_id)

        # Get original for comparison
        if sheet is not None and idx < (sheet.size[1] // 16 * 8):
            original_sprites[idx] = get_sprite(sheet, idx)

        status = "PASS" if qa_passed else "WARN"
        print(f"  QA: {status}")
        if issues:
            for issue in issues:
                print(f"    - {issue}")

    elapsed = time.time() - start_time
    print(f"\nGenerated {len(generated_sprites)} sprites in {elapsed:.1f}s")
    print(f"({elapsed / max(len(generated_sprites), 1):.1f}s per sprite)")

    # Print QA summary
    print_qa_report(qa_results)

    # Generate preview grid
    if generated_sprites:
        preview = generate_preview(original_sprites, generated_sprites)
        save_preview(preview)

    # Apply to items.png if requested
    if args.apply:
        if sheet is None:
            print("Error: Cannot apply — items.png not found")
            sys.exit(1)

        print(f"\nApplying {len(generated_sprites)} sprites to {ITEMS_PNG}...")
        for idx, sprite in generated_sprites.items():
            sheet = replace_sprite(sheet, sprite, idx)
        write_sheet(sheet)
        print("Done! Run './gradlew assembleDebug' to build with new sprites.")
    elif not args.preview:
        print("\nUse --preview to see results or --apply to write to items.png")


def main():
    parser = argparse.ArgumentParser(
        description="Generate pixel art item sprites for Amazing Pixel Dungeon",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s --setup                          Download model (one-time)
  %(prog)s --mode single --item SWORD       Generate one sprite
  %(prog)s --mode category --category potion Generate all potions
  %(prog)s --mode all --preview --seed 42   Preview all sprites
  %(prog)s --mode all --apply --seed 42     Generate and apply all
  %(prog)s --qa-only                        Run QA on existing output
        """,
    )

    parser.add_argument(
        "--mode", choices=["all", "category", "single"],
        default="single",
        help="Generation mode (default: single)",
    )
    parser.add_argument(
        "--category",
        help="Category to generate (for --mode category)",
    )
    parser.add_argument(
        "--item",
        help="Item ID to generate (for --mode single)",
    )
    parser.add_argument(
        "--model", choices=list(MODELS.keys()),
        default=DEFAULT_MODEL,
        help=f"Model to use (default: {DEFAULT_MODEL})",
    )
    parser.add_argument(
        "--seed", type=int, default=None,
        help="Random seed for reproducibility",
    )
    parser.add_argument(
        "--device", choices=["mps", "cuda", "cpu"],
        default=None,
        help="Compute device (default: auto-detect)",
    )
    parser.add_argument(
        "--preview", action="store_true",
        help="Generate preview grid without writing to items.png",
    )
    parser.add_argument(
        "--apply", action="store_true",
        help="Write generated sprites to items.png",
    )
    parser.add_argument(
        "--setup", action="store_true",
        help="Download model only (no generation)",
    )
    parser.add_argument(
        "--qa-only", action="store_true",
        help="Run QA checks on existing output sprites",
    )
    parser.add_argument(
        "--batch-size", type=int, default=1,
        help="Sprites per batch (default: 1)",
    )

    args = parser.parse_args()

    # Validate mode-specific arguments
    if args.mode == "single" and not args.item and not args.setup and not args.qa_only:
        parser.error("--item is required for --mode single")
    if args.mode == "category" and not args.category:
        parser.error("--category is required for --mode category")
    if args.apply and args.preview:
        parser.error("--apply and --preview are mutually exclusive")

    # Dispatch
    if args.setup:
        cmd_setup(args)
    elif args.qa_only:
        cmd_qa_only(args)
    else:
        cmd_generate(args)


if __name__ == "__main__":
    main()
