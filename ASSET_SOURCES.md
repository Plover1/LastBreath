# Texture sources — 0.9.0

Generated with the built-in imagegen tool, then mechanically cropped and resized with nearest-neighbor sampling in `work/textures09.ps1`. No external asset service or API key was used. Actual project textures live in `src/main/resources/assets/lastbreath/textures/`.

## Final prompts

### block/ritual_table_top.png

Create one Minecraft vanilla-style ritual table TOP texture tile, flat orthographic square texture filling entire canvas edge to edge, no perspective, no border outside tile, no text. Strict 16x16 pixel art design enlarged with nearest-neighbor pixels: dark oak plank brown base, sparse muted gold lines forming a branching skill tree from one central cyan blue soul gem into THREE branches with tiny nodes. Very simple low resolution, limited 8 color palette, no gradients no lighting no shiny bevels. This is a production game block texture. Whole image is the tile.

### item/red_soul.png and item/red_soul_overlay.png

Minecraft vanilla pixel art RED SOUL item animation sprite sheet. Transparent background. Canvas square split into FOUR equal quadrants, one frame in each, ordered top-left top-right bottom-left bottom-right. Each frame shows the SAME small red jagged flame with dark crimson core and orange highlights, slight flickering shape variation. Each flame occupies only central 50 percent of its cell, identical center alignment, no floating sparks, no borders no text. Coarse 16x16 pixel grid per frame, sharply defined big pixels, limited red palette, NO glow blur. Production sprite sheet, genuine transparency.

### item/purple_soul.png and item/purple_soul_overlay.png

Minecraft vanilla pixel art PURPLE BOSS SOUL item animation sprite sheet. Genuine transparent background. Square canvas divided into FOUR equal quadrants, one animation frame per quadrant top-left top-right bottom-left bottom-right. SAME small royal violet flame with a pale lavender diamond core, crown-like three pointed tips, subtly flickering shape, center aligned. Each flame occupies only central 50 percent of each cell. Coarse 16x16 pixel design per frame, chunky square pixels, no gradients no blur no text no borders. Production game sprite sheet.

### item/mob_soul.png and item/mob_soul_overlay.png

Minecraft vanilla pixel art BLUE PEACEFUL SOUL item animation sprite sheet. Genuine transparent background. Square canvas divided into FOUR equal quadrants, one frame per quadrant in reading order. Same small calm cyan teardrop flame with white blue core, gently flickering shape variations. Center aligned in each quadrant, occupies central 50 percent of cell, no floating sparks. Coarse 16x16 pixel design per frame, chunky square pixels, limited palette, no gradients no blur no text no borders. Production sprite sheet.

## Integration

Souls are 32×128 strips with four 32×32 frames. Jar models layer an animated soul over the existing transparent empty jar. Block tops are 16×16; wood portions reference vanilla dark oak textures with proportionate UV coordinates. Existing totem, sword, reader and empty jar retain their recognizable artwork. Existing altar top and side textures were mechanically reduced to 16×16.

## Equipment 0.10.0 — built-in imagegen, 2026-10-20

Generated with the built-in imagegen tool. Originals retained under `C:/Users/Vlad/.codex/generated_images/01a14e41-fd25-7831-a996-501ab25ed225/`. Mechanical nearest-neighbor scaling to 32×32 performed by `work/gear-textures10.ps1`; original alpha preserved for inventory icons. Project destinations are under `src/main/resources/assets/lastbreath/`.

### textures/item/soul_devourer.png
Source: `exec-a5d6c65f-8183-4c7a-b74d-5aeb66d9a171.png`
Prompt: Use case: stylized-concept. Asset type: Minecraft inventory item sprite, Soul Devourer sword. One isolated sword diagonally from bottom left handle to top right point. True coarse pixel art on a strict 32 by 32 conceptual pixel grid enlarged with crisp square pixels. Dark charcoal netherite blade with a thin cyan soul channel, a small blue soul gemstone in antique gold crossguard, dark brown wrapped handle. Mysterious but restrained vanilla Minecraft appearance. Entire sword fits with 2 pixel padding. No letters, no text, no frame, no glow outside silhouette, no tiny ornament, no antialiasing. Transparent background.

### textures/item/soul_cloak.png
Source: `exec-d644fd4c-0109-4d02-affb-2267dc6d653e.png`
Prompt: Use case: stylized-concept. Asset type: single Minecraft inventory item sprite for a mysterious soul cloak. Front view, empty wearable shoulder cape cloak without person and without hood or hat. Coarse crisp 32x32 conceptual pixel art. Charcoal purple cloth, two muted cyan seams running down its edges, small antique gold clasp with blue gem at neck, broad shoulders tapered flowing hem. Simple readable vanilla Minecraft style, limited colors, no tiny decorative details. Entire isolated item centered with padding. No text, no frame, no antialiasing, no exterior bloom. Transparent background.

### textures/entity/equipment/humanoid/soul_cloak.png
Source: `exec-daf36f11-c61c-4855-a583-72ffdb5ca8a1.png`
Prompt: Use case: stylized-concept. Asset type: flat seamless pixel-art fabric texture tile for a Minecraft dark magical cloak model, NOT an item or garment illustration. Entire square filled edge to edge with dark charcoal muted purple cloth in large crisp pixels on a conceptual 32x32 grid. Low contrast woven mottling, a few sparse very subdued desaturated blue stitches. No central motif, no borders, no objects, no text, no transparency, no lighting gradients. Vanilla Minecraft texture density, flat tileable cloth material.

### Cloak panel and final UV atlas
Source: `exec-02383a2f-54ca-41ed-9202-62f5c75be262.png`, built-in imagegen.
Prompt: Use case: stylized-concept. Asset type: production UV texture, a flat rectangular cloak back panel for Minecraft. Portrait aspect ratio exactly 1:2. Fill the ENTIRE rectangle edge to edge, no background or garment silhouette. This is a flat fabric panel, not a cloak illustration. Coarse pixel art on conceptual 10 pixels wide by 20 pixels tall grid enlarged crisply. Dark charcoal purple cloth center, subdued cyan single-pixel stitched edging along left and right and bottom edges, one small antique gold and cyan diamond soul emblem centered near the upper third. Flat even lighting, simple vanilla Minecraft limited palette, no text, no perspective, no mannequin, no transparency, no drop shadow, no fine detail.

`work/gear-cloak-atlas10.ps1` packs the generated cloth and two mechanically resized panel copies into a 128×64 UV atlas at `src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png`. Body uses the left cloth area; front and back of the cape use the 20×40 panels at (66,2) and (88,2), with a half-scale cape model part. This supersedes the preliminary 32×32 worn texture above.

Final equipment asset paths:
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/item/soul_devourer.png
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/item/soul_cloak.png
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png

## 0.12.0 sword redesign and native models

Both sword sprites originate from the built-in imagegen sheet `exec-6f85b3b3-b8df-44bf-a1c0-c5436ae3a01c.png`, retained as `art/update012/swords-source.png`. Mechanical crop and nearest-neighbor packing: `work/swords012.cjs`. Palette: dark steel, brown leather and brass; cyan soul crack on the Soul Devourer.

Scrolls and memory stones use native JSON geometry with references to Minecraft block textures. No Minecraft raster files are redistributed. Sidewalls for existing item sprites are geometry derived from their alpha masks; animated souls use pixel prisms so frame changes retain matching edge faces. These operations do not repaint the source textures.

## 0.12.1 — approved swords at 128×128

Reused the exact transparent original in `art/update012/swords-source.png`, matching the user’s screenshot of the two approved swords. No regeneration or repainting. Each square half is mechanically resized with nearest-neighbor to 128×128 by `work/swords128.cjs`. `work/sword-models128.cjs` rebuilds solid edge faces. Previews in `art/swords128/*-actual-texture.png` are 4× nearest-neighbor enlargements of the production PNGs, not concept art.

## 0.13.0 — memory and ability artwork

Built-in imagegen generated `art/update013/glyphs-source.png` and `art/update013/logo-source.png`. The 6×5 glyph sheet is mechanically cropped and resized by `work/assets013.cjs`: 28 GUI icons, memory fragment and Soul Reader sprites, and four branch seals used in the block texture atlas by scroll models. `work/new-sides013.cjs` derives closed sidewall geometry from the item alpha masks. `lastbreath.png` is a 256×256 square mod icon. Existing swords, armor and the approved 2D return totem are unchanged. Wood/brass buttons and the wheel background are native GUI rendering code.
