# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M6 closing — PR → green CI → merge, then immediately start M7.
- **M1–M5:** done — PRs #1–#5 merged.
- **M6:** all ledger rows `done`; 100% Kover + 100% Vitest; bundles rebuilt. Awaiting PR → green CI → merge.

## Next

After M6 merges: branch `milestone/M7-dx-close` and exhaust M7 until the livewire-v4-parity ledger is 100%.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
