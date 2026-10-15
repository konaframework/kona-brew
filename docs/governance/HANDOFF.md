# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M4 closing — PR → green CI → automerge, then immediately start M5.
- **M1:** done — [kona-brew #1](https://github.com/konaframework/kona-brew/pull/1) + Kona cutover [#79](https://github.com/konaframework/kona/pull/79).
- **M2:** done — [kona-brew #2](https://github.com/konaframework/kona-brew/pull/2).
- **M3:** done — [kona-brew #3](https://github.com/konaframework/kona-brew/pull/3).
- **M4:** all 33 ledger rows `done`; 100% Kover + 100% Vitest; bundles rebuilt. Awaiting PR → green CI → automerge.

## Next

After M4 merges: branch `milestone/M5-pages-navigate` and exhaust M5→M7 the same way until the livewire-v4-parity ledger is 100%.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
