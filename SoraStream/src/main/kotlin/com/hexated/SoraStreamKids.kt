package com.hexated

import com.lagradost.cloudstream3.*
import android.icu.util.Calendar
import android.icu.text.DateFormat

class SoraStreamKids : SoraStream() {
    override var name = "SoraStream-Kids"
    val mainURL = "$tmdbAPI/discover/movie?api_key=$apiKey"

    override val supportedTypes = setOf(
        TvType.Movie,
    )

    val today = Calendar.getInstance()
    val year = DateFormat.getPatternInstance(DateFormat.YEAR).format(today)
    val todayFormatted = getTodayDateString()
    val minimumdayFormatted = getOldFromTodayDateString()

    fun getOldFromTodayDateString(): String {
        val monthFromNow = Calendar.getInstance()
        monthFromNow.add(Calendar.DATE, -40)
        return "${DateFormat.getPatternInstance(DateFormat.YEAR).format(monthFromNow)}" +
                "-${DateFormat.getPatternInstance("MM").format(monthFromNow)}" +
                "-${DateFormat.getPatternInstance("dd").format(monthFromNow)}"
    }

    fun getTodayDateString(): String {
        val monthFromNow = Calendar.getInstance()
        return "${DateFormat.getPatternInstance(DateFormat.YEAR).format(monthFromNow)}" +
                "-${DateFormat.getPatternInstance("MM").format(monthFromNow)}" +
                "-${DateFormat.getPatternInstance("dd").format(monthFromNow)}"
    }

    override val mainPage = mainPageOf(
        "$mainURL&language=en-US&sort_by=popularity.desc&with_genres=16&with_original_language=en" to "Popular Movies",
        "$mainURL&language=en-US&primary_release_date.gte=$minimumdayFormatted&primary_release_date.lte=$todayFormatted&sort_by=popularity.desc&with_genres=16&with_original_language=en&without_keywords=263548" to "Now Playing",
        "$mainURL&language=en-US&primary_release_date.gte=$todayFormatted&primary_release_date.lte=$year-12-31&sort_by=popularity.desc&with_genres=16&with_original_language=en&without_keywords=263548" to "Upcoming Movies",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_genres=16&with_original_language=en&without_keywords=263548" to "Top Rated Movies",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_companies=3&with_genres=16&with_original_language=en&without_keywords=263548" to "Pixar",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_companies=3&with_genres=16&with_keywords=263548&with_original_language=en" to "Pixar Short Films",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_companies=6125|2&with_genres=16&with_original_language=en&without_companies=3&without_keywords=263548" to "Walt Disney",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_companies=7|521&with_genres=16&with_original_language=en&without_keywords=263548" to "DreamWorks",
        "$mainURL&language=en-US&sort_by=vote_average.desc&vote_count.gte=200&with_companies=2251&with_genres=16&with_original_language=en&without_keywords=263548" to "Sony",
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val type = if (request.data.contains("/movie")) "movie" else "tv"
        val home = app.get("${request.data}&page=$page")
            .parsedSafe<Results>()?.results
            ?.mapNotNull { media ->
                media.toSearchResponse(type)
            } ?: throw ErrorLoadingException("Invalid Json reponse")
        return newHomePageResponse(request.name, home)
    }
}