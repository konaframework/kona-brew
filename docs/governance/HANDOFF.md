# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** none — program complete.
- **M1–M7:** done — PRs #1–#7 merged.
- **Ledger:** every row in `docs/governance/livewire-v4-parity.md` is `done` (273/273).

## Next

No further Brew Livewire-v4 parity milestones. Maintenance / docs polish only as needed.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle && npm run playwright
```

## Rules

Never mutate GitHub rulesets.
