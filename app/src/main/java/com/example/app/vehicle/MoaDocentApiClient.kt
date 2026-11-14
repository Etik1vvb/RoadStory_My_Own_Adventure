package com.example.app.vehicle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class MoaDocentSource(val title: String, val url: String)

data class MoaDocentStory(
    val placeName: String,
    val storyText: String,
    val sourceName: String?,
    val model: String?,
    val status: String,
    val sourceUrls: List<String> = emptyList(),
    val sources: List<MoaDocentSource> = emptyList(),
    val storyScope: String = "basic",
    val selectedTopics: List<String> = emptyList(),
    val retrievalStatus: String = "not_checked",
    val recommendationBasis: String = "insufficient"
)

// A non-ready response must not be cached or spoken as a successful story.
class MoaDocentUnavailableException(
    val serverStatus: String,
    val retryable: Boolean,
    message: String
) : IOException(message)

class MoaDocentApiClient(
    private val baseUrl: String = "http://127.0.0.1:8000"
) {
    suspend fun generateStory(
        placeName: String,
        region: String,
        topic: String = "auto",
        userInterests: String,
        verifiedFacts: List<String>,
        sourceName: String,
        address: String = "",
        recommendationReasons: List<String> = emptyList()
    ): MoaDocentStory = withContext(Dispatchers.IO) {
        require(placeName.isNotBlank()) { "장소명이 필요합니다." }
        currentCoroutineContext().ensureActive()
        val request = JSONObject().apply {
            put("placeName", placeName.trim())
            put("region", region.trim())
            put("address", address.trim())
            put("topic", topic.trim().ifBlank { "auto" })
            put("userInterests", userInterests)
            put("factSnippets", JSONArray(verifiedFacts))
            put("sourceName", sourceName)
            put("recommendationReasons", JSONArray(recommendationReasons))
        }
        val connection = URL("${baseUrl.trimEnd('/')}/api/docent/story")
            .openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 10_000
            // Development allowance for sequential retrieval + narration.
            // This is a read timeout, not an overall request deadline.
            connection.readTimeout = 240_000
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.outputStream.use { output ->
                output.write(request.toString().toByteArray(Charsets.UTF_8))
            }
            val code = connection.responseCode
            val responseText = (if (code in 200..299) connection.inputStream
            else connection.errorStream)?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }.orEmpty()
            currentCoroutineContext().ensureActive()
            if (code !in 200..299) {
                throw IOException("MOA Docent HTTP $code: ${responseText.take(250)}")
            }
            val response = JSONObject(responseText)
            val status = response.optString("status")
            val storyText = response.optString("storyText").trim()
            if (status != "ready") {
                throw MoaDocentUnavailableException(
                    serverStatus = status,
                    retryable = status == "temporarily_unavailable",
                    message = storyText.ifBlank { "장소 설명을 준비하지 못했습니다." }
                )
            }
            if (storyText.isBlank()) throw IOException("도슨트 응답 내용이 비어 있습니다.")
            MoaDocentStory(
                placeName = response.optString("placeName").ifBlank { placeName },
                storyText = storyText,
                sourceName = response.nullableText("sourceName"),
                model = response.nullableText("model"),
                status = status,
                sourceUrls = response.stringList("sourceUrls"),
                sources = buildList {
                    val items = response.optJSONArray("sources") ?: JSONArray()
                    for (i in 0 until items.length()) {
                        val item = items.optJSONObject(i) ?: continue
                        add(MoaDocentSource(item.optString("title"), item.optString("url")))
                    }
                },
                storyScope = response.optString("storyScope", "basic"),
                selectedTopics = response.stringList("selectedTopics"),
                retrievalStatus = response.optString("retrievalStatus", "not_checked"),
                recommendationBasis = response.optString("recommendationBasis", "insufficient")
            )
        } finally {
            connection.disconnect()
        }
    }
}

private fun JSONObject.nullableText(key: String): String? =
    if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

private fun JSONObject.stringList(key: String): List<String> = buildList {
    val items = optJSONArray(key) ?: JSONArray()
    for (i in 0 until items.length()) {
        if (!items.isNull(i)) items.optString(i).takeIf { it.isNotBlank() }?.let { add(it) }
    }
}
