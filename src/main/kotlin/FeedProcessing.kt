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
    fun getUpcomingServicesForName(name: String): List<UpcomingService>
    fun getUpcomingServicesForCode(code: String): List<UpcomingService>
    fun getUpcomingServicesFor(stopTemplate: Stop): List<UpcomingService>

    val feedValid: Boolean?
}

fun feedProcessor(feed: GtfsFeed): FeedProcessor = FeedProcessorImpl(feed)

private class FeedProcessorImpl(val feed: GtfsFeed) : FeedProcessor {
    override fun getUpcomingServicesForName(name: String) =
        getUpcomingServices { it.stop.name.equals(name, true) }


    override fun getUpcomingServicesForCode(code: String) =
        getUpcomingServices {
            it.stop.code?.equals(code, true) ?: false
        }

    override fun getUpcomingServicesFor(stopTemplate: Stop)
            : List<UpcomingService> = getUpcomingServices {
        (it.stop.name == stopTemplate.name.ifEmpty { it.stop.name }
                && it.stop.code == (stopTemplate.code ?: it.stop.code))
    }

    private fun getUpcomingServices(filter: (StopTime) -> Boolean)
            : MutableList<UpcomingService> {
        val today = LocalDate.now().dayOfWeek
        return feed.stopTimes
            .stream()
            .filter (filter)
            .filter { it.isOn(today) }
            .filter {
                val minuteAgo = LocalTime.now().minusMinutes(1)
                it.departureTime?.isAfter(minuteAgo)
                    ?: it.arrivalTime!!.isAfter(minuteAgo)
            }
            .sorted { stopTime1, stopTime2 ->
                (stopTime1.departureTime ?: stopTime1.arrivalTime!!).compareTo(
                    (stopTime2.departureTime ?: stopTime2.arrivalTime!!)
                )
            }
            .limit(20)
            .map(StopTime::upcomingService)
            .collect(Collectors.toList())
    }

    private fun LocalDate.isBetween(start: LocalDate, end: LocalDate) =
        equals(start) || (isAfter(start) && isBefore(end))


    override val feedValid: Boolean? by lazy {
        val feedInfo = feed.feedInfos.firstOrNull() ?: return@lazy null
        val now = LocalDate.now()
        val startDate = feedInfo.feedStartDate
        val endDate = feedInfo.feedEndDate
        if (startDate == null || endDate == null) null
        else now.isBetween(startDate, endDate)
    }

    
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

private fun StopTime.isOn(day: DayOfWeek) =
    when (day) {
        DayOfWeek.MONDAY -> trip.service.monday
        DayOfWeek.TUESDAY -> trip.service.tuesday
        DayOfWeek.WEDNESDAY -> trip.service.wednesday
        DayOfWeek.THURSDAY -> trip.service.thursday
        DayOfWeek.FRIDAY -> trip.service.friday
        DayOfWeek.SATURDAY -> trip.service.saturday
        DayOfWeek.SUNDAY -> trip.service.sunday
        else -> throw IllegalStateException(
            "Unknown day of week value: $day"
        )
    }

