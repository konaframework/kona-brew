# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M2 closing — PR merge, then immediately start M3.
- **M1:** done — [kona-brew #1](https://github.com/konaframework/kona-brew/pull/1) + Kona cutover [#79](https://github.com/konaframework/kona/pull/79).
- **M2:** all 35 ledger rows `done`; 100% Kover + 100% Vitest; bundles rebuilt. Awaiting PR → green CI → automerge.

## Next

After M2 merges: branch `milestone/M3-events-js` and exhaust M3 (events, JS API, effects) the same way through M7.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
