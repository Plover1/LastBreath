# Last Breath 0.12.0 validation

- GameTest: all 10 required tests passed (`work/tests012-final.log`).
- Client resource/menu smoke: successful, no mod model/texture errors (`work/client012b.log`). OSHI host performance-counter warnings are unrelated.
- Clean production build: successful (`work/build012-final.log`); no test classes or fixtures included.
- JSON resources parse; release version and author verified.
- Actual tree screenshot, baked armor geometry and jar/sidewall preview inspected.
- SHA256: 466ab4712402aa503df287edbd0288e83b3438d62819380c35de6c67b269f990

Limits: no manual multiplayer playthrough or balance test. Alt wheel input is implemented and compiles but has not been manually exercised in a multiplayer session. Shrine feature placement and rewards were exercised by GameTest; naturally finding shrines across many seeds was not benchmarked.
