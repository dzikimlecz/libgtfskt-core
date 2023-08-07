package me.dzikimlecz.libgtfskt

import me.dzikimlecz.libgtfskt.csv.mapObjects
import me.dzikimlecz.libgtfskt.csv.readAll
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Collectors


data class UpcomingService(
    val line: Route,
    val stop: Stop,
    val direction: String,
    val departure: LocalDateTime,
)

fun getFeed(directory: File): GtfsFeed = mapObjects(readAll(directory))

interface FeedProcessor {
    fun getUpcomingServicesFor(stop: String): List<UpcomingService>

}

fun feedProcessor(feed: GtfsFeed): FeedProcessor = FeedProcessorImpl(feed)

private class FeedProcessorImpl(val feed: GtfsFeed) : FeedProcessor {
    override fun getUpcomingServicesFor(stop: String): List<UpcomingService> {
        val today = LocalDate.now()
        return feed.stopTimes.stream()
            .filter { it.stop.name == stop }
            .filter {
                when (val dayOfWeek = today.dayOfWeek) {
                    DayOfWeek.MONDAY -> it.trip.service.monday
                    DayOfWeek.TUESDAY -> it.trip.service.tuesday
                    DayOfWeek.WEDNESDAY -> it.trip.service.wednesday
                    DayOfWeek.THURSDAY -> it.trip.service.thursday
                    DayOfWeek.FRIDAY -> it.trip.service.friday
                    DayOfWeek.SATURDAY -> it.trip.service.saturday
                    DayOfWeek.SUNDAY -> it.trip.service.sunday
                    else -> throw IllegalStateException(
                        "Unknown day of week value: $dayOfWeek"
                    )
                }
            }
            .map {
                UpcomingService(
                    line = it.trip.route,
                    stop = it.stop,
                    departure = LocalDateTime.of(today,
                        it.departureTime ?: it.arrivalTime!!
                    ),
                    direction = it.trip.headsign ?: it.trip.route.longName
                )
            }.collect(Collectors.toList())
    }
}