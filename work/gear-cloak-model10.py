from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulCloakRendering.java');s=p.read_text().replace('CubeListBuilder.create().texOffs(0,0).addBox(-5,0,0,10,21,1,new CubeDeformation(.1f))','CubeListBuilder.create().texOffs(32,0).addBox(-5,0,0,10,20,1,new CubeDeformation(.1f))').replace('LayerDefinition.create(mesh,32,32)','LayerDefinition.create(mesh,64,32)');p.write_text(s)
p=Path('ASSET_SOURCES.md');s=p.read_text();s+='''
### Cloak panel and final UV atlas
Source: `exec-02383a2f-54ca-41ed-9202-62f5c75be262.png`, built-in imagegen.
Prompt: Use case: stylized-concept. Asset type: production UV texture, a flat rectangular cloak back panel for Minecraft. Portrait aspect ratio exactly 1:2. Fill the ENTIRE rectangle edge to edge, no background or garment silhouette. This is a flat fabric panel, not a cloak illustration. Coarse pixel art on conceptual 10 pixels wide by 20 pixels tall grid enlarged crisply. Dark charcoal purple cloth center, subdued cyan single-pixel stitched edging along left and right and bottom edges, one small antique gold and cyan diamond soul emblem centered near the upper third. Flat even lighting, simple vanilla Minecraft limited palette, no text, no perspective, no mannequin, no transparency, no drop shadow, no fine detail.

`work/gear-cloak-atlas10.ps1` packs the generated cloth and two mechanically resized panel copies into a 64×32 UV atlas at `src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png`. Body uses the left cloth area; front and back of the cape use the 10×20 panels at (33,1) and (44,1). This supersedes the preliminary 32×32 worn texture above.
''';p.write_text(s,encoding='utf-8')
