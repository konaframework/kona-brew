# Brew

Brew is Kona's reactive server-component layer. You write components as Kotlin or Java classes with public state
and actions, render them with Crema, and Brew keeps the browser in sync over a small JSON protocol. Its behaviour
and API shape follow Laravel Livewire v4.

```kotlin
class Counter : Component() {
    var count = 0

    fun increment() { count++ }

    override fun template() = """
        <div>
            <button wire:click="increment">+</button>
            <span>{{ count }}</span>
        </div>
    """
}
```

```html
<brew:counter />
```

- Artifact: `org.konaframework:kona-brew` (Kotlin internals, first-class Java API).
- JS runtime: `js/` (Alpine.js + plugins, esbuild). Built bundles are committed under
  `brew/src/main/resources/org/konaframework/brew/` and served from `/brew/brew.min.js`.
- **Docs (this repo):** https://brew.konaframework.org/ — Astro + Starlight site under
  `apps/docs/` (rose brand, Alpine coupling guides, examples). Framework primer also lives on the
  [Kona docs](https://docs.konaframework.org/) site.
- Status: [MILESTONES.md](MILESTONES.md) and the parity ledger
  [docs/governance/livewire-v4-parity.md](docs/governance/livewire-v4-parity.md).

## Documentation site

```bash
cd apps/docs
npm ci
npm run dev          # http://127.0.0.1:4322
PUBLIC_SITE_URL=https://brew.konaframework.org PUBLIC_BASE_PATH=/ npm run build
npm run preview
```

GitHub Pages is enabled for this repository (Settings → Pages → **GitHub Actions**).
Workflow [`.github/workflows/docs.yml`](.github/workflows/docs.yml) builds `apps/docs` on pushes to
`main` (and verifies the build on PRs that touch docs) then deploys to
https://brew.konaframework.org/.

## Requirements

- JDK 21
- Node 22 (JS runtime work, and to build/preview the docs site)
- Kona artifacts in Maven local (until `org.konaframework` is published to Maven Central)

## Build and test

```bash
# 1. Publish Kona to Maven local (once per Kona change)
git clone https://github.com/konaframework/kona ../kona
(cd ../kona && ./gradlew publishToMavenLocal -x test)

# 2. Brew library: tests + 100% line/branch coverage
./gradlew :kona-brew:test :kona-brew:koverVerify

# 3. Samples
./gradlew :brew-sample-kotlin:test :brew-sample-java:test

# 4. JS runtime: tests with 100% coverage, then rebuild the committed bundles
cd js
npm ci
npm run coverage
npm run build          # writes brew.js, brew.min.js(.map), brew.esm.js
npm run check:bundle   # CI: fails when the committed bundles are stale
```

## Run the samples

```bash
./gradlew :brew-sample-kotlin:run --args="--base=$(pwd)/samples/brew-kotlin"   # http://127.0.0.1:4000
PORT=4001 ./gradlew :brew-sample-java:run --args="--base=$(pwd)/samples/brew-java"
```

Each sample renders a counter (single-file component), a todo list (class + Crema view), and a debounced live
search on one page.

## Benchmarks

```bash
./gradlew :kona-brew:bench      # server mount/update hot paths
cd js && npm run bench          # keyed morph
```

Baseline numbers: [docs/performance/baseline.md](docs/performance/baseline.md).

## License

Apache-2.0

## Docs hosting

Primary: [brew.konaframework.org](https://brew.konaframework.org) (Cloudflare Pages project `kona-brew-docs`).

```bash
cd apps/docs
PUBLIC_SITE_URL=https://brew.konaframework.org PUBLIC_BASE_PATH=/ npm run build
```

Deploy: prefer Cloudflare **Connect to Git** for project `kona-brew-docs` (no GitHub secrets). Dashboard settings (npm — not Kona’s pnpm monorepo):

| Setting | Value |
|---------|--------|
| Root directory | `apps/docs` |
| Build command | `npm ci && npm run build` |
| Build output directory | `dist` |
| Env | `PUBLIC_SITE_URL=https://brew.konaframework.org`, `PUBLIC_BASE_PATH=/`, `NODE_VERSION=22` |

Full notes: [docs/governance/docs-site.md](docs/governance/docs-site.md). Optional later: `CLOUDFLARE_API_TOKEN` + `CLOUDFLARE_ACCOUNT_ID` so `.github/workflows/cloudflare-pages.yml` can Wrangler-deploy. Mirror: GitHub Pages at `/kona-brew/` via `.github/workflows/docs.yml`. Central publish order: see [kona publishing.md](https://github.com/konaframework/kona/blob/main/docs/governance/publishing.md).
