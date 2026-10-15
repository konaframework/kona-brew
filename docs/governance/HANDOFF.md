# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M2 — Forms, validation & directive suite (`milestone/M2-forms-directives`).
- **M1:** done — merged [PR #1](https://github.com/konaframework/kona-brew/pull/1); Kona cutover [PR #79](https://github.com/konaframework/kona/pull/79).
- **Ledger:** ~34 M2 rows still `todo` (Form objects, `@Validate`, error bag, wire:loading/dirty/poll/…, `$errors`/`$refs`). DIR-18 (`wire:replace`) already `done`.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify :brew-sample-kotlin:test :brew-sample-java:test
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets. If a ruleset blocks a merge, stop and ask a maintainer.
