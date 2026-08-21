export class WeatherError extends Error {
  readonly name = "WeatherError";
  readonly requestPath?: string;

  constructor(
    readonly code:
      | "auth"
      | "rate_limit"
      | "not_found"
      | "network"
      | "timeout"
      | "provider"
      | "malformed"
      | "unknown",
    message: string,
    readonly httpStatus?: number,
    options?: { cause?: unknown; requestPath?: string },
  ) {
    super(message, options?.cause !== undefined ? { cause: options.cause } : undefined);
    this.requestPath = options?.requestPath;
  }
}

export function errorFromHttpStatus(status: number, requestPath?: string): WeatherError {
  if (status === 401 || status === 403) {
    return new WeatherError("auth", "Weather provider rejected the credentials.", status, {
      requestPath,
    });
  }
  if (status === 404) {
    return new WeatherError("not_found", "Weather provider did not find that location.", status, {
      requestPath,
    });
  }
  if (status === 429) {
    return new WeatherError("rate_limit", "Weather provider rate limit reached.", status, {
      requestPath,
    });
  }
  if (status >= 500) {
    return new WeatherError("provider", "Weather provider failed.", status, { requestPath });
  }
  return new WeatherError("unknown", "Weather provider returned an unexpected status.", status, {
    requestPath,
  });
}

export function errorFromFetchFailure(error: unknown, requestPath?: string): WeatherError {
  if (error instanceof WeatherError) return error;
  const name = error instanceof Error ? error.name : "";
  if (name === "AbortError") {
    return new WeatherError("timeout", "Weather provider request timed out.", undefined, {
      cause: error,
      requestPath,
    });
  }
  if (error instanceof TypeError || name === "TypeError" || name === "NetworkError") {
    return new WeatherError("network", "Network unavailable for weather request.", undefined, {
      cause: error,
      requestPath,
    });
  }
  return new WeatherError("unknown", "Weather request failed.", undefined, {
    cause: error,
    requestPath,
  });
}

export function redactSecrets(text: string): string {
  return text.replace(/appid=[^&\s"#]*/gi, "appid=redacted");
}

export function describeFetchFailure(error: unknown): string {
  const weather = error instanceof WeatherError ? error : undefined;
  const code = weather?.code ?? "unknown";
  const raw =
    error instanceof Error
      ? `${error.name}: ${error.message}`
      : "non-error throw";
  const cause =
    error instanceof Error && error.cause instanceof Error
      ? `${error.cause.name}: ${error.cause.message}`
      : undefined;
  return [code, weather?.requestPath, redactSecrets(raw), cause ? redactSecrets(cause) : undefined]
    .filter((part): part is string => Boolean(part))
    .join(" · ");
}
