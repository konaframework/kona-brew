# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M7 closing — PR → green CI → merge.
- **M1–M6:** done — PRs #1–#6 merged.
- **M7:** all ledger rows `done` (Livewire v4 ledger 100%); 100% Kover + 100% Vitest; Playwright smoke added. Awaiting PR → green CI → merge.

## Next

After M7 merges: program complete. No further Brew parity milestones.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle && npm run playwright
```

## Rules

Never mutate GitHub rulesets.
