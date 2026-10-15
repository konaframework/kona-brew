# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M2 — Forms, validation & directive suite (`milestone/M2-forms-directives`).
- **M1:** done — [kona-brew #1](https://github.com/konaframework/kona-brew/pull/1) + Kona cutover [#79](https://github.com/konaframework/kona/pull/79).
- **M2 progress (server):** Form, `@Validate`/`@Rule`, `ErrorBag`, `SupportValidation`, `memo.errors` / effects, `Brew.test` `assertHasErrors` — **done** (100% Kover).
- **M2 remaining:** VAL-4/7; ACT-7/9; FRM-6/7; DIR-4/5/8–11/14/15/17/19/22/23/25; ATR-16; JS-3b/11; wire:loading/dirty/confirm/poll/offline/cloak/show/text/bind/ref/transition; docs (DX-7b).

## Next

Implement the M2 JS directive suite under `js/src/directives/`, rebuild committed bundles, Vitest 100%, then docs in Kona `apps/docs/.../brew/`.

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

## Rules

Never mutate GitHub rulesets.
