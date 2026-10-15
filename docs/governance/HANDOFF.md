# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M5 closing — PR → green CI → merge, then immediately start M6.
- **M1–M4:** done — PRs #1–#4 merged.
- **M5:** all ledger rows `done`; 100% Kover + 100% Vitest; bundles rebuilt. Awaiting PR → green CI → merge.

## Next

After M5 merges: branch `milestone/M6-uploads-platform` and exhaust M6→M7 until the livewire-v4-parity ledger is 100%.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
