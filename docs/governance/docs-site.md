# Public documentation site (Brew)

Stack: **Astro + Starlight + `@almasix/starlight-theme`**, **npm** under `apps/docs/` (`package-lock.json`), **Cloudflare Workers Assets** primary at [brew.konaframework.org](https://brew.konaframework.org).

## Brand (locked — matches Kona)

Light primary caramel `#DF8723`, light accent warm brown `#74453F` (soft `#8B5A52`, deep `#5A322E`). Dark primary latte `#C9A57B`; dark accent soft cream. CSS: `apps/docs/src/styles/custom.css` and `landing.css`. Logos keep caramel/latte fills only — accent is theme/CSS, not a mark recolor. `theme-color` stays `#DF8723`.

| Path | Role |
|------|------|
| `apps/docs/` | Public Starlight site |
| `docs/governance/` | Process locks (this file, parity ledgers) |

## Cloudflare Workers Builds (primary)

**First publish does not require GitHub secrets.** Cloudflare’s Workers Builds Git integration clones, builds, and deploys from the repo. Workers Builds supplies its own API token — do **not** add a `CLOUDFLARE_API_TOKEN` build variable.

Worker / Workers Builds project name: **`kona-brew-docs`** (the `name` in both `wrangler.jsonc` files must match the dashboard project).

Brew docs are a **static Astro site on Workers Assets** (not classic Pages). Config uses `"assets": { "directory": "…", "not_found_handling": "404-page" }` plus `"previews": {}`, and deploys with **`npx wrangler deploy`**. This repo does **not** use `pages_build_output_dir`.

### Do not use — classic Pages deploy

| | Use this | Never use this |
|---|----------|----------------|
| Deploy | `npx wrangler deploy` | `npx wrangler pages deploy …` |
| Flags | (none required; name comes from `wrangler.jsonc`) | `--project-name=kona-brew-docs` with `pages deploy` |
| Config key | `"assets": { "directory": "…" }` | `pages_build_output_dir` |

**Why:** Workers Builds runs `npx wrangler deploy`. With a Pages-style config (`pages_build_output_dir`) Wrangler warns *It seems that you have run `wrangler deploy` on a Pages project* and fails with *Missing entry-point to Worker script or to assets directory*. The `assets.directory` key fixes that.

### Path A — Workers Builds Git integration (dashboard)

1. **Workers & Pages →** project **`kona-brew-docs`** → **Settings → Builds** (or Create → Connect to Git → `konaframework/kona-brew`).
2. Framework preset: **None** (do not point at the Kona monorepo pnpm settings).
3. Build settings (**npm** — lockfile is `apps/docs/package-lock.json`; this repo is **not** a pnpm workspace). Either root directory works:

   | Setting | Root `apps/docs` (recommended) | Root `/` |
   |---------|--------------------------------|----------|
   | Root directory | `apps/docs` | `/` |
   | Build command | `npm ci && npm run build` | `npm ci --prefix apps/docs && npm run build --prefix apps/docs` |
   | Deploy command | `npx wrangler deploy` | `npx wrangler deploy` |
   | Non-production (preview) command | `npx wrangler preview` | `npx wrangler preview` |
   | Wrangler config used | `apps/docs/wrangler.jsonc` → `./dist` | `wrangler.jsonc` → `./apps/docs/dist` |
   | Build variables | `PUBLIC_SITE_URL=https://brew.konaframework.org`, `PUBLIC_BASE_PATH=/`, `NODE_VERSION=22` | same |

   `npx wrangler preview` only works because both configs include `"previews": {}`.

4. Custom domain: `brew.konaframework.org` on the Worker (Workers & Pages → `kona-brew-docs` → Settings → Domains & Routes).
5. **Save** Build settings, then **Retry build** (or push to `main`).

Do **not** copy Kona’s build command (`pnpm …`). Brew docs live only under `apps/docs` with `package-lock.json`.

### Path B — optional Wrangler from GitHub Actions

Workflow: [`.github/workflows/cloudflare-pages.yml`](../../.github/workflows/cloudflare-pages.yml) — on `main` docs changes it always builds (smoke) and runs `wrangler deploy` from `apps/docs` only when `CLOUDFLARE_API_TOKEN` (Account → Workers Scripts → Edit) and `CLOUDFLARE_ACCOUNT_ID` are set as GitHub secrets.

Local:

```bash
cd apps/docs
npm ci
PUBLIC_SITE_URL=https://brew.konaframework.org PUBLIC_BASE_PATH=/ npm run build
npm run cf:deploy          # wrangler deploy (needs `wrangler login` or token env)
npx wrangler deploy --dry-run   # verify config finds ./dist without deploying
```

Config: repo-root [`wrangler.jsonc`](../../wrangler.jsonc) (`assets.directory: ./apps/docs/dist`) and [`apps/docs/wrangler.jsonc`](../../apps/docs/wrangler.jsonc) (`assets.directory: ./dist`) — same `name`, keep them in sync.
