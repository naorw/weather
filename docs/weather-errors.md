# Weather errors

UI consumes `WeatherError`, not OkHttp or JSON exceptions.

| Code | Meaning |
| --- | --- |
| `missing_key` | No key in `ApiKeyStore` |
| `auth` | HTTP 401/403 or OpenWeather `cod` 401/403 |
| `rate_limit` | HTTP 429 |
| `not_found` | HTTP 404 |
| `timeout` | Connect/read/call timeout |
| `network` | Other I/O / connectivity failure |
| `malformed` | Unusable JSON or missing required fields |
| `provider` | HTTP 5xx |
| `unknown` | Other HTTP statuses |

Air-pollution failure during `getSnapshot` is swallowed unless it is `auth` or `missing_key`; the snapshot still contains current + forecast.

Do not put the API key or credential-bearing URLs in logs or error messages. `redactSecrets` replaces `appid=…`.
