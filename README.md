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

- Artifact: `dev.kona:kona-brew` (Kotlin internals, first-class Java API).
- JS runtime: `js/` (Alpine.js + plugins, esbuild). Built bundles are committed under
  `brew/src/main/resources/dev/kona/brew/` and served from `/brew/brew.min.js`.
- Docs: the Kona docs site, section **Brew** (`apps/docs/src/content/docs/docs/brew/` in
  [konaframework/kona](https://github.com/konaframework/kona)).
- Status: [MILESTONES.md](MILESTONES.md) and the parity ledger
  [docs/governance/livewire-v4-parity.md](docs/governance/livewire-v4-parity.md).

## Requirements

- JDK 21
- Node 22 (only to work on the JS runtime)
- Kona artifacts in Maven local (until `dev.kona` is published to Maven Central)

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
