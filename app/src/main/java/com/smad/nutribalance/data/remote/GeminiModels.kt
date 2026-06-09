package com.smad.nutribalance.data.remote

data class GeminiRequest(
    val contents: List<Content>
) {
    data class Content(
        val parts: List<Part>,
        val role: String = "user"
    )

    data class Part(
        val text: String
    )
}

data class GeminiResponse(
    val candidates: List<Candidate>?
) {
    data class Candidate(
        val content: Content?
    )

    data class Content(
        val parts: List<Part>?
    )

    data class Part(
        val text: String?
    )

    fun extractText(): String {
        return candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "No response received from AI."
    }
}
