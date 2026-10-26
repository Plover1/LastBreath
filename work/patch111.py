from pathlib import Path
p=Path('src/main/java/me/vlad/lastbreath/client/SoulCloakRendering.java');s=p.read_text(encoding='utf-8');draft=Path('art/wanderer-detail/WandererDetailModel.java').read_text(encoding='utf-8');s=s[:s.index('    public static final class CloakModel')]+draft[draft.index('    public static final class CloakModel'):];p.write_text(s,encoding='utf-8')
p=Path('src/main/java/me/vlad/lastbreath/SoulEvents.java');s=p.read_text(encoding='utf-8').replace('.then(Commands.literal("ghost")','.then(SoulAdminCommands.tree())\n                .then(Commands.literal("ghost")');p.write_text(s,encoding='utf-8')
p=Path('gradle.properties');p.write_text(p.read_text(encoding='utf-8').replace('mod_version=0.11.0','mod_version=0.11.1'),encoding='utf-8')
