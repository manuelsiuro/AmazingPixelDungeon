"""Model download, loading, and inference for sprite generation."""

import sys

import torch
from PIL import Image

from .config import MODELS, DEFAULT_MODEL, GENERATION_SIZE, NEGATIVE_PROMPT


def detect_device(requested=None):
    """Auto-detect the best available device, or use the requested one."""
    if requested:
        return requested
    if torch.backends.mps.is_available():
        return "mps"
    if torch.cuda.is_available():
        return "cuda"
    return "cpu"


def load_pipeline(model_key=DEFAULT_MODEL, device=None):
    """Load a Stable Diffusion pipeline.

    Args:
        model_key: "sd15" or "sdxl"
        device: "mps", "cuda", "cpu", or None for auto-detect

    Returns:
        (pipeline, device_str)
    """
    from diffusers import StableDiffusionPipeline, StableDiffusionXLPipeline

    device = detect_device(device)
    model_cfg = MODELS[model_key]
    model_id = model_cfg["model_id"]
    dtype = torch.float16 if model_cfg["dtype"] == "float16" else torch.float32

    # CPU doesn't support float16
    if device == "cpu":
        dtype = torch.float32

    print(f"Loading model '{model_id}' on {device} ({dtype})...")

    if model_key == "sdxl":
        pipeline = StableDiffusionXLPipeline.from_pretrained(
            model_id,
            torch_dtype=dtype,
            use_safetensors=True,
        )
        # Load LoRA adapter for pixel art
        lora_id = model_cfg.get("lora_id")
        if lora_id:
            print(f"Loading LoRA adapter '{lora_id}'...")
            pipeline.load_lora_weights(lora_id)
    else:
        pipeline = StableDiffusionPipeline.from_pretrained(
            model_id,
            torch_dtype=dtype,
        )

    pipeline = pipeline.to(device)

    # Memory optimizations
    pipeline.enable_attention_slicing()
    if device == "mps":
        # MPS-specific: disable safety checker to avoid MPS fallback issues
        pipeline.safety_checker = None
        pipeline.requires_safety_checker = False

    print("Model loaded successfully.")
    return pipeline, device


def setup_model(model_key=DEFAULT_MODEL):
    """Download the model without running inference. For --setup flag."""
    from diffusers import StableDiffusionPipeline, StableDiffusionXLPipeline

    model_cfg = MODELS[model_key]
    model_id = model_cfg["model_id"]

    print(f"Downloading model '{model_id}'...")

    if model_key == "sdxl":
        StableDiffusionXLPipeline.from_pretrained(
            model_id,
            torch_dtype=torch.float16,
            use_safetensors=True,
        )
        lora_id = model_cfg.get("lora_id")
        if lora_id:
            print(f"Downloading LoRA adapter '{lora_id}'...")
            # Trigger download by loading into a temp pipeline
            from huggingface_hub import hf_hub_download
            try:
                hf_hub_download(repo_id=lora_id, filename="pytorch_lora_weights.safetensors")
            except Exception:
                print(f"  Note: LoRA will be downloaded on first use.")
    else:
        StableDiffusionPipeline.from_pretrained(
            model_id,
            torch_dtype=torch.float16,
        )

    print("Model download complete. Cached at ~/.cache/huggingface/")


def generate_image(pipeline, prompt, device, seed=None, size=GENERATION_SIZE,
                   negative_prompt=NEGATIVE_PROMPT, model_key=DEFAULT_MODEL):
    """Generate a single image from a prompt.

    Args:
        pipeline: Loaded diffusion pipeline
        prompt: Text prompt for generation
        device: Device string
        seed: Random seed for reproducibility (None for random)
        size: Output image size (square)
        negative_prompt: What to avoid in generation
        model_key: Model config key for inference params

    Returns:
        PIL.Image in RGBA mode
    """
    model_cfg = MODELS[model_key]
    steps = model_cfg["num_inference_steps"]
    guidance = model_cfg["guidance_scale"]

    generator = None
    if seed is not None:
        if device == "mps":
            # MPS generator must be created on CPU
            generator = torch.Generator("cpu").manual_seed(seed)
        else:
            generator = torch.Generator(device).manual_seed(seed)

    with torch.no_grad():
        result = pipeline(
            prompt=prompt,
            negative_prompt=negative_prompt,
            num_inference_steps=steps,
            guidance_scale=guidance,
            height=size,
            width=size,
            generator=generator,
        )

    image = result.images[0]
    return image.convert("RGBA")
