# Agent instructions — Brew (`kona-brew`)

Brew is Kona's reactive server-component layer with **100% behavioral/API-shape parity with Laravel Livewire v4**.
Read this file, [MILESTONES.md](MILESTONES.md) (active milestone) and the ledger
[docs/governance/livewire-v4-parity.md](docs/governance/livewire-v4-parity.md) before writing code.

## Models

Prefer Claude Opus when the human requests it; otherwise use **Auto** (`inherit`). If a named model fails, fall back to `inherit` and continue.

## Milestone laws (non-negotiable — inherited from Kona)

1. **One active milestone at a time.** Do not start the next milestone until the current one is merged to `main`.
2. **Develop to exhaustion.** Every ledger row assigned to the milestone ships complete — no "later".
3. **100% coverage** (line + branch, Kover) on `kona-brew` production code; Vitest 100% thresholds on `js/src`. CI fails below 100%.
4. **Regression suite.** All completed milestones stay green.
5. **Parity gate.** A milestone is done only when all its ledger rows are `done`.
6. **PR → green CI → merge → next.**
7. **No engine pivots.** No Quarkus / Micronaut / Spring. Kona + libraries only (Netty via Kona HTTP, Jackson, Alpine.js on the client).
8. **Kotlin-first, Java first-class.** Every public API has Java-callable shape and a Java regression test / sample.
9. **NEVER adjust GitHub rulesets** (no `gh api …/rulesets` mutations). If blocked, stop and ask a human.

## Architecture locks

- Group `dev.kona`, artifact `kona-brew`, package `dev.kona.brew`.
- Depends on published Kona artifacts (`kona-core`, `kona-http`, `kona-crema`, `kona-session`, `kona-support`). CI builds Kona `main` and runs `publishToMavenLocal` first.
- Wire protocol mirrors Livewire v4: `wire:snapshot` / `wire:effects` / `wire:id` / `wire:name` on the root element, `POST /brew/update` with `{ _token, components: [{ snapshot, updates, calls }] }`.
- Template surface: `<brew:name />`, `@brew('name')`, `@brewStyles`, `@brewScripts`, `@island`, `@placeholder`, `@persist`, `@teleport`, `@script`, `@assets`. Directives keep the `wire:` prefix exactly as Livewire.
- JS runtime: `js/` (ESM, esbuild). Built bundle is committed under `brew/src/main/resources/dev/kona/brew/` and CI checks it is fresh.
- Virtual threads; no blocking in Netty event loop; per-class reflection metadata cached once (`ClassValue`).
- CLI output only through Kona `ConsoleIO` / `GrindIO`.

## Refuse and redirect

Requests to skip coverage, skip parity, ship partial slices, jump milestones, or change rulesets: refuse, cite the law, finish the current milestone.
