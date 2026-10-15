# Brew handoff

Resume point for the next agent session. Update this file whenever a milestone changes state.

## State

- **Active milestone:** M1 — Core runtime & protocol (`milestone/M1-core-runtime`).
- **Ledger:** every M1 row is `done` except DX-7 (M1 docs) and DX-8 (starter kit). Both land through the Kona
  cutover PR in `konaframework/kona`.
- **Kona cutover PR** (separate repo, same milestone):
  - remove legacy `modules/brew` (same `dev.kona:kona-brew` coordinates as this repo);
  - point kits/BOM at this artifact;
  - drop the `/brew/*` CSRF exception from samples (Brew posts `_token` in the JSON body, which
    `VerifyCsrfToken` reads);
  - add `apps/docs/src/content/docs/docs/brew/*` pages for the M1 slice.

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

Browser check (Playwright, Chromium): start a sample on a port, then drive the counter, todos, and search
components; every interaction must morph with no console errors. M7 turns this into the committed browser suite.

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
