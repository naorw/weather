# OpenWeather credentials

v1 is a static PWA. JavaScript delivered to the browser **cannot hide** a reusable API key.

## Strategy

The key is a Vite environment variable:

`VITE_OPENWEATHER_API_KEY`

Vite inlines `VITE_*` values into the client bundle at **build** time (and into the dev server at **run** time). Anyone who can download the app can extract the key.

This is accepted for a personal/static deployment. A backend proxy would only be justified if the owner wants to keep the key off the client; Phase 1 does not add a server.

## Where the key exists

| Place | Present? |
| --- | --- |
| Git | No. `.env` is ignored. Never commit the real key. |
| `.env.example` | Placeholder only |
| Local `.env` | Yes, developer machine |
| `import.meta.env` / built JS | Yes, visible to the browser user |
| Logs / errors / handoff docs | Must never include the key |

## Local development

```sh
cp .env.example .env
# paste the key into VITE_OPENWEATHER_API_KEY
npm run dev
```

Vite loads `.env` when the **dev process starts**. Press `r` in the Vite terminal or stop and start `npm run dev`. Reloading the phone/browser tab is not enough.

On localhost / LAN Vite, the browser calls `/ow/...` and the dev server attaches `appid`. The key still exists in the client bundle via `VITE_*`.

You do **not** need `npm run build` for `npm run dev`.

`npm run preview` serves a previous build: change `.env`, then `npm run build && npm run preview`.

Settings → Fetch Stockholm weather. Optional: `npm test` runs the live probe when the variable is set.

## Production

Set `VITE_OPENWEATHER_API_KEY` in the build environment, then `npm run build`. The value is baked into `dist/` assets. Treat those assets as containing the key.

## Rotation

1. Generate a new key in the OpenWeather account.
2. Update local `.env` and the production build environment.
3. Rebuild and redeploy the PWA.
4. Disable the old key.

## Limitations

- The key is not a secret once the PWA is installed or the JS is fetched.
- Rate-limit abuse is possible if the key leaks; rotate if needed.
- Do not put the key in `tasks/`, `docs/`, README, or commit messages.
