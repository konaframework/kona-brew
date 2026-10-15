# Brew milestones

Single source of truth for milestone status. Rules: [AGENTS.md](AGENTS.md). Ledger: [docs/governance/livewire-v4-parity.md](docs/governance/livewire-v4-parity.md).

**Active milestone:** M5 — Pages, navigation, URL & pagination (closing)

The program is complete when every ledger row is `done` (M1–M7). Each milestone closes **all** ledger rows tagged with it.

## Definition of Done (every milestone)

- [ ] Every ledger row tagged with the milestone is `done`.
- [ ] 100% line + branch coverage on `kona-brew` (Kover) and 100% on `js/src` (Vitest); CI enforced.
- [ ] Full regression suite green (all completed milestones).
- [ ] Kotlin sample + Java sample/regression exercise the new public API.
- [ ] Public docs for the slice written/rewritten on the Kona docs site (`apps/docs/src/content/docs/docs/brew/`).
- [ ] PR merged to `main` after green CI; this file updated (milestone **done**, next **active**).

---

## M1 — Core runtime & protocol

**Branch:** `milestone/M1-core-runtime` · **Status:** done (merged)

- [x] Ledger rows: all M1 rows `done` (incl. DX-7 docs + DX-8 starter kit cutover).
- [x] 100% Kover (`kona-brew`) and 100% Vitest (`js/src`, 123 tests); CI enforced.
- [x] Kotlin + Java samples with smoke and round-trip tests; both pass a Chromium run.
- [x] Performance baseline recorded ([docs/performance/baseline.md](docs/performance/baseline.md)).
- [x] Kona cutover PR merged (legacy `modules/brew` removed, starter kit + docs) — kona #79.
- [x] This repo's M1 PR merged — #1.

Protocol (snapshot, checksum, update endpoint, header + CSRF guards, payload limits), hydration/synthesizers (scalars, collections,
enums, `java.time`, big numbers, UUID, DTO/data classes, `Wireable`), component model (class + inline-template "single file"),
registry/discovery/namespaces, `<brew:…>` / `@brew` / `@brewStyles` / `@brewScripts` / `@js`, auto-injected assets, properties
(`fill`, `reset`, `pull`, `only`, `all`), actions (params, DI, returns, magic `$set`/`$toggle`/`$refresh`), lifecycle hooks (incl.
interface-mixin hooks), `@Locked`, `@Computed`, `@Renderless` / `skipRender`, JS runtime core on Alpine (`$wire`, `wire:click`,
`wire:submit`, `wire:model` + all modifiers, any-event `wire:*` + modifiers, `wire:init`, `wire:ignore`, `wire:key`, keyed morph with
block markers, request bus, hooks, custom directives, error modals), testing API core, `./grind make:brew`, Kona monorepo cutover
(remove legacy `modules/brew`, starter kit → `kona-brew`).

## M2 — Forms, validation & directive suite

**Branch:** `milestone/M2-forms-directives` · **Status:** done (merged #2)

- [x] Ledger rows: all M2 rows `done` (35/35).
- [x] 100% Kover (`kona-brew`) and 100% Vitest (`js/src`).
- [x] `Form` / `@Validate` / `@Rule` / `Rules.unique().ignore()` / error bag / Testable assertions.
- [x] JS: `wire:loading`, `data-loading`, `wire:dirty`, `wire:confirm`, `wire:offline`, `wire:poll`, `wire:cloak`,
      `wire:show`, `wire:text`, `wire:bind`, `wire:transition` + `@Transition`, `wire:ref` + `$refs` / `$errors`,
      `x-modelable`, form disable-during-submit.
- [x] Bundles rebuilt into `brew/src/main/resources/dev/kona/brew/`.

`Form` objects, `@Validate`/`rules()`/messages/attributes, `validate`/`validateOnly`, error bag API, `$errors` (Crema + JS), real-time
validation, form hooks; `wire:loading` (+ targets/delays), `data-loading`, `wire:dirty`, `wire:confirm`, `wire:offline`, `wire:poll`,
`wire:cloak`, `wire:show`, `wire:text`, `wire:bind`, `wire:transition` + `@Transition`, `wire:replace`, `wire:ref` + `$refs`,
`x-modelable` custom controls, disabling forms during requests, validation testing assertions.

## M3 — Events, JavaScript API & effects

**Branch:** `milestone/M3-events-js` · **Status:** done (merged #3)

`dispatch` (+ `to`/`self`), `@On` (dynamic names), JS `Brew.on/dispatch*`, `$wire.$on/$dispatch*`, Alpine events, Echo/broadcasting
events, `@script`/`@assets`/`@entangle`/`@this`, `js()` + `@Js`, `@Json`, `@Async`/`.async`, interceptors, redirects + flash,
downloads, `wire:stream` + `stream()`, `.preserve-scroll`, event/js/redirect/download testing assertions.

## M4 — Nesting, slots, lazy/defer, islands, teleport

**Branch:** `milestone/M4-nesting-islands` · **Status:** done (merged #4)

- [x] Ledger rows: all M4 rows `done` (33/33).
- [x] 100% Kover (`kona-brew`) and 100% Vitest (`js/src`).
- [x] Nesting + `memo.children`, keys, `@Reactive`/`@Modelable`, slots, attribute forwarding, `$parent`, dynamic & recursive,
      `@Isolate`, child `@event` listeners, `@Lazy`/`@Defer` + placeholders + bundling, `@island`, `@teleport`,
      `Brew.withoutLazyLoading()`.
- [x] JS: teleport, islands (`.island` / `$island`), lazy/defer, `$parent`.
- [x] Kotlin + Java samples (`Dashboard` / `Panel` / `LazyPanel`).

Children + `memo.children`, keys, `@Reactive`, `@Modelable`, slots (default/named), attribute forwarding, `$parent`, dynamic &
recursive components, child event listeners, `@Isolate`, `@Lazy`/`@Defer` + placeholders + bundling, `@island` (all modes),
`@teleport`, `withoutLazyLoading`.

## M5 — Pages, navigation, URL & pagination

**Branch:** `milestone/M5-pages-navigate` · **Status:** done (PR pending)

- [x] Ledger rows: all M5 rows `done`.
- [x] 100% Kover + 100% Vitest.
- [x] Full-page routes, layouts/titles/slots, `@Url`/`queryString`, `@Session`, `WithPagination`, `wire:navigate`/`wire:current`/`@persist`.


Full-page routing, layouts/`@Layout`/`@Title`/slots, route params & Extract binding, `wire:navigate` (prefetch, persist, scroll,
progress bar, view transitions, script rules, hooks), `wire:current`, `@Url` + `queryString()`, `@Session`, `WithPagination`
(all variants/themes/hooks), full-page lazy, back-button cache, page testing helpers.

## M6 — Uploads, sorting, styles & platform

**Branch:** `milestone/M6-uploads-platform` · **Status:** planned

`WithFileUploads` (temp storage, signed URLs, previews, multiple, progress/cancel, S3, cleanup, testing), `wire:sort`,
`wire:intersect`, multi-file components, scoped/global styles, CSS modules, component JS modules, CSP build, custom synthesizers,
Extract model synths, persistent/global middleware, `@Authorize`, locales, release tokens, large payloads, checksum debugging,
package development hooks.

## M7 — DX, browser suite & program close

**Branch:** `milestone/M7-dx-close` · **Status:** planned

`make:brew` all flags, `brew:convert`, `brew:publish`/`stubs`/`layout`, config publishing, Kona Idea/LSP notes, Playwright browser
suite, performance report, complete docs + samples audit, ledger 100%.
