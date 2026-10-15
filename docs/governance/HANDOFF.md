# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M3 closing — PR merge, then immediately start M4.
- **M1:** done — [kona-brew #1](https://github.com/konaframework/kona-brew/pull/1) + Kona cutover [#79](https://github.com/konaframework/kona/pull/79).
- **M2:** done — [kona-brew #2](https://github.com/konaframework/kona-brew/pull/2).
- **M3:** all 32 ledger rows `done`; 100% Kover + 100% Vitest; bundles rebuilt. Awaiting PR → green CI → automerge.

## Next

After M3 merges: branch `milestone/M4-nesting-islands` and exhaust M4→M7 the same way until the livewire-v4-parity ledger is 100%.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
