from pathlib import Path
p=Path('gradle.properties');s=p.read_text().replace('harvest three kinds of souls and develop supernatural abilities at a ritual table.','harvest three kinds of souls, develop supernatural abilities and empower a Soul Devourer and Twilight Cloak at a ritual table.');p.write_text(s)
p=Path('ASSET_SOURCES.md');s=p.read_text();s+='''
Final equipment asset paths:
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/item/soul_devourer.png
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/item/soul_cloak.png
- C:/Users/Vlad/IdeaProjects/LastBreath/src/main/resources/assets/lastbreath/textures/entity/equipment/humanoid/soul_cloak.png
''';p.write_text(s,encoding='utf-8')
