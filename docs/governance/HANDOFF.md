# Agent handoff — Brew (`kona-brew`)

Paste this file (or its path) into a new chat so another agent can continue without the prior transcript.

## Purpose

**Brew** is Kona’s reactive server-component layer with **100% behavioral/API-shape parity with Laravel Livewire v4**. Artifact `dev.kona:kona-brew`, package `dev.kona.brew`. Kotlin-first, Java first-class. Org: [konaframework/kona-brew](https://github.com/konaframework/kona-brew).

Standalone docs (Astro + Starlight): https://konaframework.github.io/kona-brew/  
Kona monorepo keeps only a **brief link-out** page (kona PR #94); do not re-expand Brew guides inside Kona.

## Read first

1. [AGENTS.md](../../AGENTS.md) — laws, architecture locks.
2. [MILESTONES.md](../../MILESTONES.md) — program status (M1–M7).
3. [livewire-v4-parity.md](livewire-v4-parity.md) — full ledger (273/273 `done`).
4. Parent framework: sibling `/home/smaosa/kona` → [docs/governance/HANDOFF.md](https://github.com/konaframework/kona/blob/main/docs/governance/HANDOFF.md).
5. **Never** mutate GitHub rulesets.

## Where we are (2026-10-16)

| Item | Value |
|------|--------|
| Default branch | `main` at transparent espresso mark (#15) |
| Active milestone | **none** — Livewire v4 program complete |
| Ledger | Every row in `livewire-v4-parity.md` is `done` (273/273) |
| Open PRs | none (as of handoff) |
| Models | Prefer **Auto** (`inherit`) unless human names a model |
| Merge habit | PR → green CI → squash automerge |

### Recently merged

| Work | PR |
|------|-----|
| Restore transparent espresso cup mark | [#15](https://github.com/konaframework/kona-brew/pull/15) |
| Redo Brew mark from concept art | [#14](https://github.com/konaframework/kona-brew/pull/14) |
| Espresso / copper / cream brand mark | [#13](https://github.com/konaframework/kona-brew/pull/13) |
| Outfit + Monokai landing brand pass | [#12](https://github.com/konaframework/kona-brew/pull/12) |
| Standalone Brew docs site + GitHub Pages | [#11](https://github.com/konaframework/kona-brew/pull/11) |
| CI: Kona checkout on `main` after Cadence rename | [#10](https://github.com/konaframework/kona-brew/pull/10) |
| Depend on `kona-cadence` (not extract) | [#9](https://github.com/konaframework/kona-brew/pull/9) |
| Mark M7 done / ledger complete | [#8](https://github.com/konaframework/kona-brew/pull/8) |
| M7 DX / Playwright / 100% ledger | [#7](https://github.com/konaframework/kona-brew/pull/7) |
| M1–M6 feature milestones | [#1](https://github.com/konaframework/kona-brew/pull/1)–[#6](https://github.com/konaframework/kona-brew/pull/6) |

### Done vs in-flight (honest)

**Done on `main`:** M1–M7 parity, standalone docs + Pages, Cadence dependency cutover, espresso branding iterations through transparent cup mark (#15). Brand assets under `apps/docs/public/branding/` and `apps/docs/src/assets/`.

**In-flight / local only:**

- Further **banner/logo refinement** may still be running as a parallel agent, or uncommitted copies may sit in sibling Kona (`apps/docs/public/branding/brew-*.svg`). Check `git status` here and `gh pr list` before starting a new branding pass.
- Working tree on `main` should be clean after this handoff PR; stale local branches (`docs/brew-*`, `milestone/*`) can be pruned when convenient.

## How to continue

```bash
# Publish Kona first (sibling checkout)
(cd ../kona && ./gradlew publishToMavenLocal -x test)

# Library tests + 100% Kover
./gradlew :kona-brew:test :kona-brew:koverVerify

# JS runtime
cd js && npm ci && npm run coverage && npm run build && npm run check:bundle && npm run playwright

# Docs site
cd apps/docs && npm ci && npm run dev
# build for Pages: PUBLIC_SITE_URL=https://konaframework.github.io PUBLIC_BASE_PATH=/kona-brew/ npm run build
```

Key paths: `brew/` (Kotlin), `js/` (Alpine runtime), `apps/docs/` (site), `samples/`, `docs/governance/`.

Sibling: Inertia is `/home/smaosa/kona-inertia` (unrelated to Brew).

## Open gaps / next priorities

1. **Maintenance only** for Livewire v4 parity — no new milestone without a human mandate.
2. Docs/branding polish if the espresso mark still needs light/dark balance or banner composition (verify against #15 first).
3. Keep CI green against Kona `main` (Cadence rename already landed).
4. If Kona’s brief Brew page should show marks, sync SVGs via a Kona docs PR — do not re-home exhaustive guides there.

## Do not

- Reopen closed ledger rows without mandate.
- Skip coverage (Kover / Vitest 100%).
- Mutate GitHub rulesets.
- Expand full Brew docs back into the Kona monorepo site.
