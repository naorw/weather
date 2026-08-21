package org.radilabs.weather.weather

class WeatherError(
    val code: Code,
    message: String,
    val httpStatus: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    enum class Code {
        MissingKey,
        Auth,
        RateLimit,
        NotFound,
        Timeout,
        Network,
        Malformed,
        Provider,
        Unknown,
    }

    val title: String
        get() = when (code) {
            Code.MissingKey -> "Credentials"
            Code.Auth -> "Credentials"
            Code.RateLimit -> "Rate limited"
            Code.NotFound -> "Not found"
            Code.Timeout -> "Timeout"
            Code.Network -> "Offline"
            Code.Malformed -> "Malformed"
            Code.Provider -> "Provider"
            Code.Unknown -> "Weather error"
        }
}

fun errorFromHttpStatus(status: Int): WeatherError {
    return when (status) {
        401, 403 -> WeatherError(WeatherError.Code.Auth, "Weather provider rejected the credentials.", status)
        404 -> WeatherError(WeatherError.Code.NotFound, "Weather provider did not find that location.", status)
        429 -> WeatherError(WeatherError.Code.RateLimit, "Weather provider rate limit reached.", status)
        in 500..599 -> WeatherError(WeatherError.Code.Provider, "Weather provider failed.", status)
        else -> WeatherError(WeatherError.Code.Unknown, "Weather provider returned an unexpected status.", status)
    }
}

fun redactSecrets(text: String): String {
    return text.replace(Regex("appid=[^&\\s\"#]*", RegexOption.IGNORE_CASE), "appid=redacted")
}
