package me.dzikimlecz.libgtfskt

import me.dzikimlecz.libgtfskt.csv.mapObjects
import me.dzikimlecz.libgtfskt.csv.readAll
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    override fun getUpcomingServicesFor(stop: String): List<UpcomingService> =
        feed.stopTimes.stream()
            .filter { it.stop.name.equals(stop.trim(), true) }
            .filter(StopTime::isToday)
            .map(StopTime::upcomingService)
            .filter {
                it.departure.toLocalTime()
                    .isAfter(LocalTime.now().minusMinutes(1))
            }
            .limit(20)
            .collect(Collectors.toList())





}

private fun StopTime.upcomingService() = UpcomingService(
    line = trip.route,
    stop = stop,
    departure = LocalDateTime.of(
        LocalDate.now(),
        departureTime ?: arrivalTime!!
    ),
    direction = trip.headsign ?: trip.route.longName
)

private fun StopTime.isToday() =
    when (val dayOfWeek = LocalDate.now().dayOfWeek) {
        DayOfWeek.MONDAY -> trip.service.monday
        DayOfWeek.TUESDAY -> trip.service.tuesday
        DayOfWeek.WEDNESDAY -> trip.service.wednesday
        DayOfWeek.THURSDAY -> trip.service.thursday
        DayOfWeek.FRIDAY -> trip.service.friday
        DayOfWeek.SATURDAY -> trip.service.saturday
        DayOfWeek.SUNDAY -> trip.service.sunday
        else -> throw IllegalStateException(
            "Unknown day of week value: $dayOfWeek"
        )
    }