package com.lagradost.cloudstream3.movieproviders

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.getQualityFromName
import com.lagradost.cloudstream3.LoadResponse.*
import org.jsoup.nodes.Element
import org.jsoup.Jsoup

class FilmizleTV : MainAPI() {
    override var mainUrl = "https://filmizletv.me"
    override var name = "FilmizleTV"
    override val hasMainPage = true
    override val hasCensoredCharacters = false
    override val hasQuickSearch = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val items = mutableListOf<HomePageList>()

        try {
            val doc = app.get("$mainUrl/").document
            
            // Movies Section
            doc.select("div.movie-section, section.movies").forEach { section ->
                val title = section.select("h2, h3, .section-title").text().ifEmpty { "Movies" }
                val movies = section.select("div.movie-item, article.movie-card, div.film-item").mapNotNull { movie ->
                    movie.toSearchResult()
                }
                if (movies.isNotEmpty()) {
                    items.add(HomePageList(title, movies))
                }
            }

            // Series Section
            doc.select("div.series-section, section.series").forEach { section ->
                val title = section.select("h2, h3, .section-title").text().ifEmpty { "Series" }
                val series = section.select("div.series-item, article.series-card, div.film-item").mapNotNull { serie ->
                    serie.toSearchResult()
                }
                if (series.isNotEmpty()) {
                    items.add(HomePageList(title, series))
                }
            }

            // Trending Section
            doc.select("div.trending, section.trending").forEach { section ->
                val trending = section.select("div.film-item, article.trending-item").mapNotNull { item ->
                    item.toSearchResult()
                }
                if (trending.isNotEmpty()) {
                    items.add(HomePageList("Trending", trending))
                }
            }

        } catch (e: Exception) {
            logError(e)
        }

        return HomePageResponse(items.ifEmpty {
            listOf(HomePageList("Movies", emptyList()))
        })
    }

    private fun Element.toSearchResult(): SearchResponse? {
        return try {
            val title = this.select("h2, h3, .title, .name, a").text().trim()
            val href = this.select("a").attr("href").trim()
            val posterUrl = this.select("img").attr("src").let { src ->
                if (src.startsWith("http")) src else "$mainUrl$src"
            }
            val type = when {
                this.select(".series-badge, .tv-badge").isNotEmpty() -> TvType.TvSeries
                this.select(".movie-badge, .film-badge").isNotEmpty() -> TvType.Movie
                else -> TvType.Movie
            }

            if (title.isNotEmpty() && href.isNotEmpty()) {
                MovieSearchResponse(
                    name = title,
                    url = href,
                    apiName = this@FilmizleTV.name,
                    type = type,
                    posterUrl = posterUrl.ifEmpty { null }
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun search(query: String): List<SearchResponse> {
        val results = mutableListOf<SearchResponse>()
        
        try {
            val searchUrl = "$mainUrl/?s=$query"
            val doc = app.get(searchUrl).document
            
            doc.select("div.search-result, article.search-item, div.film-item").forEach { item ->
                item.toSearchResult()?.let { results.add(it) }
            }
        } catch (e: Exception) {
            logError(e)
        }

        return results
    }

    override suspend fun load(url: String): LoadResponse? {
        return try {
            val doc = app.get(url).document
            
            val title = doc.select("h1, .page-title, .film-title").text().trim()
            val posterUrl = doc.select("div.poster img, .movie-poster img").attr("src").let { src ->
                if (src.startsWith("http")) src else "$mainUrl$src"
            }
            val description = doc.select("div.description, .plot, p.synopsis").text().trim()
            val rating = doc.select("span.rating, .imdb-rating").text().toDoubleOrNull()
            val year = doc.select("span.year, .release-year").text().filter { it.isDigit() }.takeLast(4)
            
            val type = if (url.contains("/series/") || url.contains("/tv/")) {
                TvType.TvSeries
            } else {
                TvType.Movie
            }

            val episodes = if (type == TvType.TvSeries) {
                doc.select("div.season, .season-container").mapIndexed { seasonIndex, season ->
                    val seasonNum = season.select("span.season-number, .season-title")
                        .text().filter { it.isDigit() }.toIntOrNull() ?: (seasonIndex + 1)
                    
                    season.select("div.episode, .episode-item, li.episode").mapIndexed { epIndex, episode ->
                        val epNum = episode.select("span.episode-number, .ep-num")
                            .text().filter { it.isDigit() }.toIntOrNull() ?: (epIndex + 1)
                        val epTitle = episode.select("span.episode-title, .ep-title, a").text().trim()
                        val epUrl = episode.select("a").attr("href").trim()
                        
                        Episode(
                            name = epTitle,
                            season = seasonNum,
                            episode = epNum,
                            data = epUrl.ifEmpty { url }
                        )
                    }
                }.flatten()
            } else {
                emptyList()
            }

            when (type) {
                TvType.TvSeries -> {
                    TvSeriesLoadResponse(
                        name = title,
                        url = url,
                        apiName = name,
                        type = type,
                        episodes = episodes,
                        posterUrl = posterUrl.ifEmpty { null },
                        backgroundPosterUrl = posterUrl.ifEmpty { null },
                        plot = description.ifEmpty { null },
                        rating = rating?.toInt()
                    )
                }
                else -> {
                    MovieLoadResponse(
                        name = title,
                        url = url,
                        apiName = name,
                        type = type,
                        dataUrl = url,
                        posterUrl = posterUrl.ifEmpty { null },
                        backgroundPosterUrl = posterUrl.ifEmpty { null },
                        plot = description.ifEmpty { null },
                        rating = rating?.toInt()
                    )
                }
            }
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return try {
            val doc = app.get(data).document
            
            // Extract video links from various possible sources
            doc.select("iframe, source[type*='video']").forEach { element ->
                val src = when {
                    element.tagName() == "iframe" -> element.attr("src")
                    else -> element.attr("src")
                }
                
                if (src.isNotEmpty()) {
                    when {
                        src.contains("m3u8") -> {
                            M3u8Helper.generateM3u8(
                                name,
                                src,
                                mainUrl,
                                headers = mapOf("Referer" to mainUrl)
                            ).forEach { link ->
                                callback(
                                    ExtractorLink(
                                        source = name,
                                        name = name,
                                        url = link.url,
                                        referer = mainUrl,
                                        quality = link.quality ?: Qualities.Unknown.value,
                                        isM3u8 = true
                                    )
                                )
                            }
                        }
                        src.contains("mp4") || src.contains("video") -> {
                            callback(
                                ExtractorLink(
                                    source = name,
                                    name = name,
                                    url = src,
                                    referer = mainUrl,
                                    quality = Qualities.HD.value
                                )
                            )
                        }
                        else -> {
                            // Try to extract from iframe src
                            try {
                                val iframeDoc = app.get(src, referer = mainUrl).document
                                iframeDoc.select("video source, source[type*='video']").forEach { source ->
                                    val videoUrl = source.attr("src")
                                    if (videoUrl.isNotEmpty()) {
                                        callback(
                                            ExtractorLink(
                                                source = name,
                                                name = name,
                                                url = videoUrl,
                                                referer = mainUrl,
                                                quality = Qualities.HD.value
                                            )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                logError(e)
                            }
                        }
                    }
                }
            }

            // Extract subtitle tracks
            doc.select("track[kind='captions'], track[kind='subtitles']").forEach { track ->
                val subtitle = track.attr("src")
                val label = track.attr("label") ?: "Unknown"
                
                if (subtitle.isNotEmpty()) {
                    subtitleCallback(
                        SubtitleFile(
                            label = label,
                            url = if (subtitle.startsWith("http")) subtitle else "$mainUrl$subtitle",
                            name = name
                        )
                    )
                }
            }

            true
        } catch (e: Exception) {
            logError(e)
            false
        }
    }

    companion object {
        private const val TAG = "FilmizleTV"
    }
}
