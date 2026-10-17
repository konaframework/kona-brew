# Public documentation site (Brew)

Stack: **Astro + Starlight + `@almasix/starlight-theme`**, **npm** under `apps/docs/` (`package-lock.json`), **Cloudflare Pages** primary at [brew.konaframework.org](https://brew.konaframework.org).

## Brand (locked — matches Kona)

Light primary caramel `#DF8723`, light accent warm brown `#74453F` (soft `#8B5A52`, deep `#5A322E`). Dark primary latte `#C9A57B`; dark accent soft cream. CSS: `apps/docs/src/styles/custom.css` and `landing.css`. Logos keep caramel/latte fills only — accent is theme/CSS, not a mark recolor. `theme-color` stays `#DF8723`.

| Path | Role |
|------|------|
| `apps/docs/` | Public Starlight site |
| `docs/governance/` | Process locks (this file, parity ledgers) |

## Cloudflare Pages (primary)

**First publish does not require GitHub secrets.** Prefer Cloudflare’s Git integration.

Pages project name: **`kona-brew-docs`**.

### Path A — Connect to Git (dashboard)

1. **Workers & Pages → Create → Connect to Git** → `konaframework/kona-brew`.
2. Framework preset: **None** (or leave custom — do not point at the Kona monorepo pnpm settings).
3. Build settings (**npm** — this repo is **not** a pnpm workspace):

   | Setting | Value |
   |---------|--------|
   | Root directory | `apps/docs` |
   | Build command | `npm ci && npm run build` |
   | Build output directory | `dist` |
   | Environment variables | `PUBLIC_SITE_URL=https://brew.konaframework.org`, `PUBLIC_BASE_PATH=/`, `NODE_VERSION=22` |

4. Custom domain: `brew.konaframework.org` → CNAME to the `*.pages.dev` host Cloudflare shows.

Do **not** copy Kona’s dashboard settings (`pnpm …` / root `/`). Brew docs live only under `apps/docs` with `package-lock.json`.

### Path B — optional Wrangler from GitHub Actions

Workflow: [`.github/workflows/cloudflare-pages.yml`](../../.github/workflows/cloudflare-pages.yml). Deploy step runs only when `CLOUDFLARE_API_TOKEN` and `CLOUDFLARE_ACCOUNT_ID` are set.

Local:

```bash
cd apps/docs
npm ci
PUBLIC_SITE_URL=https://brew.konaframework.org PUBLIC_BASE_PATH=/ npm run build
npx wrangler pages deploy dist --project-name=kona-brew-docs
```

Config: `apps/docs/wrangler.jsonc`.
