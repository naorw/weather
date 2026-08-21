package org.radilabs.weather

data class AppVersion(
    val name: String,
    val code: Long,
) {
    fun label(applicationId: String = "org.radilabs.weather"): String {
        return "Weather $name ($code) · $applicationId"
    }
}
