from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulTreeScreen.java');s=p.read_text().replace('if(i==0){b.active=equipment;continue;}','if(i==0){b.visible=equipment&&type>0;b.active=b.visible;continue;}').replace('import net.minecraft.world.item.ItemStack;\n','').replace('import net.minecraft.world.item.Items;\n','');p.write_text(s)
