# Last Breath 0.13.0 validation

- `runGameTestServer -PlastbreathTests --offline`: all 10 required integration tests passed (`work/tests013-final.log`). Includes real death/respawn flow, persistence/relog/restart, altar/channel transactions, soul harvesting, gear and memory trials. Expanded assertions cover named-player ghost/alive commands, Survival death policy and its toggles, all new living active effects, healing cost/cooldown, passive modifier removal and 28-bit talent codec round trip.
- `runClient -PlastbreathTests -PlastbreathClientSmoke --offline`: successful (`work/client013-final.log`). Resource/model loading completed without mod errors. Actual equipment, original talent tree and living page screenshots visually inspected. Final metadata uses `iconFile` supported by this loader.
- All JSON parsed; English/Ukrainian/Russian each contain the same 307 keys with matching format placeholders. The 28 custom icons, scroll seals, reader/fragment textures and square mod icon are packaged.
- Clean production build excludes test fixture classes. JAR SHA-256: `a8b2417c0618c413728c6236933d327724383c6edc0702b37946567a4fe0c2dc`.

Limits: no session with multiple real network clients or manual Alt-wheel input test in this pass. Long-session multiplayer balance, other-mod compatibility and world-generation density remain playtesting tasks. Short already-applied potion effects expire naturally when a talent is disabled; no unrelated potion effects are forcibly removed. GUI settings registration and resource startup checked; live remote server config editing was not exercised.

Production: C:\Users\Vlad\Documents\Codex\2026-10-18\z\outputs\lastbreath-0.13.0.jar
