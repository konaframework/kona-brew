# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M1 — Core runtime & protocol (`milestone/M1-core-runtime`) — DoD pending merges.
- **Ledger:** every M1 row is `done`, including DX-7 (docs) and DX-8 (starter kit).
- **Kona cutover** (`brew/cutover-kona-brew` in `konaframework/kona`):
  - legacy `modules/brew` removed; kits/docs/CI consume external `dev.kona:kona-brew`;
  - M1 Brew docs under `apps/docs/src/content/docs/docs/brew/*`.
- **This repo PR:** https://github.com/konaframework/kona-brew/pull/1 — merge when CI green, then cutover.

## Layout

| Path | What |
|------|------|
| `brew/` | `:kona-brew` library (`dev.kona.brew`) |
| `js/` | JS runtime sources, Vitest suite, esbuild script |
| `brew/src/main/resources/dev/kona/brew/` | committed bundles (`brew.js`, `brew.min.js`, `.map`, `brew.esm.js`) |
| `samples/brew-kotlin`, `samples/brew-java` | runnable apps + smoke tests |
| `docs/governance/livewire-v4-parity.md` | parity ledger |
| `docs/performance/baseline.md` | benchmark baseline |

## Commands

```bash
(cd ../kona && ./gradlew publishToMavenLocal -x test)
./gradlew :kona-brew:test :kona-brew:koverVerify :brew-sample-kotlin:test :brew-sample-java:test
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle
```

After M1 merges: open `milestone/M2-forms-directives` and exhaust the M2 ledger rows.

## Runtime notes worth knowing

- The JS runtime follows Livewire v4's architecture: actions → per-component messages (identical calls squash) →
  a 5 ms message bus → one request per bundle of components, with interceptors at action, message, and request
  level.
- Deliberate deviations: isolated components always get their own request; redirects settle pending action
  promises so a prevented redirect never blocks the component's queue.
- `id` is a `$wire` alias for `$id`; do not name component properties `id`.
- Use `Alpine.evaluateRaw` (not `Alpine.evaluate`) for action expressions so errors are catchable.

## Rules

Never mutate GitHub rulesets. If a ruleset blocks a merge, stop and ask a maintainer.
