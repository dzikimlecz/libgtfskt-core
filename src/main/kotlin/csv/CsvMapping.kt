package me.dzikimlecz.libgtfskt.csv

import me.dzikimlecz.libgtfskt.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.TimeZone.getTimeZone

fun mapObjects(csvFeed: CsvFeed): GtfsFeed {
    val agencies = mapAgencies(csvFeed.agencies).sortedBy { it.id }
    val stops = mapStops(csvFeed.stops).sortedBy { it.id }
    val calendars = mapCalendars(csvFeed.calendars).sortedBy { it.id }
    val routes = mapRoutes(csvFeed.routes, agencies).sortedBy { it.id }
    val trips = mapTrips(csvFeed.trips, routes, calendars).sortedBy { it.id }
    val stopTimes = mapStopTimes(csvFeed.stopTimes, trips, stops)
    val calendarDates = mapCalendarDates(csvFeed.calendarDates, calendars)
    val feedInfos = mapFeedInfos(csvFeed.feedInfos)

    return GtfsFeed(
        agencies,
        stops,
        routes,
        trips,
        stopTimes,
        calendars,
        calendarDates,
        feedInfos,
    )
}

///////////////////////////////////////////////////////////////////////////
// MAPPERS
///////////////////////////////////////////////////////////////////////////

private fun mapAgencies(agencyCsvs: List<AgencyCsv>): List<Agency> {
    val many = agencyCsvs.size > 1
    return agencyCsvs.map { it.toAgency(many) }
}

private fun mapStops(stopCsvs: List<StopCsv>): List<Stop> {
    val grouped = stopCsvs.groupBy { it.parent_station.isEmpty() }
    val parents = grouped[true]?.map(StopCsv::toStop)
        ?: listOf()


    val children = grouped[false]?.map {
        val parentIndex = parents
            .binarySearch { stop -> stop.id.compareTo(it.parent_station) }
        if (parentIndex == -1) {
            throw NoSuchElementException(
                "Parent station not found: ${it.parent_station}"
            )
        }
        it.toStop(parents[parentIndex])
    } ?: listOf()

    return parents + children
}

private fun mapRoutes(
    routeCsvs: List<RouteCsv>,
    agencies: List<Agency>
): List<Route> {
    val manyAgencies = agencies.size > 1
    return routeCsvs.map {
        if (manyAgencies) {
            require (it.agency_id.isNotEmpty()) {
                "Routes must have a defined agency, if there is more than 1 agency"
            }
        }
        val agencyIndex = agencies
            .binarySearch { agency -> agency.id.compareTo(it.agency_id) }
        val agency = if (agencyIndex == -1) null else agencies[agencyIndex]
        it.toRoute(
            if (manyAgencies) {
                agency ?: throw NoSuchElementException(
                    "Agency not found: ${it.agency_id}"
                )
            } else {
                agency
            }
        )
    }
}

private fun mapCalendars(calendarCsvs: List<CalendarCsv>) =
    calendarCsvs.map(CalendarCsv::toCalendar)

private fun mapCalendarDates(
    calendarDates: List<CalendarDatesCsv>,
    calendars: List<Calendar>,
) = calendarDates.map {
    val calendarIndex = calendars.binarySearch { calendar ->
        calendar.id.compareTo(it.service_id)
    }
    if (calendarIndex == -1) {
        throw NoSuchElementException(
            "Calendar not found: ${it.service_id}"
        )
    }
    it.toCalendarDates(calendars[calendarIndex])
}

private fun mapTrips(
    trips: List<TripCsv>,
    routes: List<Route>,
    calendars: List<Calendar>
) = trips.map {
    val routeIndex = routes.binarySearch {route ->
        route.id.compareTo(it.route_id)
    }
    if (routeIndex == -1) {
        throw NoSuchElementException("Route not found: ${it.route_id}")
    }
    val calendarIndex = calendars.binarySearch { calendar ->
        calendar.id.compareTo(it.service_id)
    }
    if (calendarIndex == -1) {
        throw NoSuchElementException("Calendar not found ${it.service_id}")
    }
    it.toTrip(routes[routeIndex], calendars[calendarIndex])
}

private fun mapStopTimes(
    stopTimes: List<StopTimeCsv>,
    trips: List<Trip>,
    stops: List<Stop>
) = stopTimes.map {
    val tripIndex = trips.binarySearch { trip ->
        trip.id.compareTo(it.trip_id)
    }
    if (tripIndex == -1) {
        throw NoSuchElementException("Trip not found ${it.trip_id}")
    }
    val stopIndex = stops.binarySearch { stop ->
        stop.id.compareTo(it.stop_id)
    }
    if (stopIndex == -1) {
        throw NoSuchElementException("Stop not found ${it.stop_id}")
    }
    it.toStopTime(trips[tripIndex], stops[stopIndex])
}

private fun mapFeedInfos(feedInfoCsvs: List<FeedInfoCsv>) =
    feedInfoCsvs.map(FeedInfoCsv::toFeedInfo)

///////////////////////////////////////////////////////////////////////////
// CONVERTERS
///////////////////////////////////////////////////////////////////////////

private fun AgencyCsv.toAgency(oneOfMany: Boolean): Agency {
    if (oneOfMany) require(agency_id.isNotEmpty()) {
        "agency_id must not be empty."
    }
    require(agency_name.isNotEmpty()) {
        "agency_name must not be empty."
    }
    require(agency_url.isNotEmpty()) {
        "agency_url must not be empty."
    }

    return Agency(
        agency_id,
        agency_name,
        agency_url,
        getTimeZone(agency_timezone),
        agency_lang.takeIfNotEmpty(),
        agency_phone.takeIfNotEmpty(),
        agency_fare_url.takeIfNotEmpty(),
        agency_email.takeIfNotEmpty(),
    )
}

private fun StopCsv.toStop(parent: Stop? = null): Stop {

    require(stop_id.isNotEmpty()) {
        "stop_id must not be empty"
    }


    val locationType = LocationType.fromInt(location_type)
        ?: unknownValue("location type", location_type)

    val parentStation = if (parent_station.isEmpty()) {
        null
    } else {
        require(parent != null && parent.id == parent_station) {
            "parent_station ($parent_station) doesn't match parent's id " +
                parent?.id
        }
        parent
    }

    val timezone = if (stop_timezone.isNotEmpty()) getTimeZone(stop_timezone)
    else null

    val wheelchairBoarding = WheelchairAccessibility.fromInt(wheelchair_boarding)
        ?: unknownValue("wheelchair boarding", wheelchair_boarding)

    return Stop(
        stop_id,
        stop_code,
        stop_name,
        stop_desc,
        stop_lat,
        stop_lon,
        zone_id,
        stop_url.takeIfNotEmpty(),
        locationType,
        parentStation,
        timezone,
        wheelchairBoarding,
        level_id.takeIfNotEmpty(),
        platform_code.takeIfNotEmpty(),
    )
}

private fun RouteCsv.toRoute(agency: Agency? = null): Route {
    require(route_id.isNotEmpty()) {
        "route_id must not be empty"
    }

    val theAgency = if (agency_id.isEmpty()) {
        null
    } else {
        require(agency != null && agency.id == agency_id) {
            "agency_id ($agency_id) doesn't match agency's id ${agency?.id}"
        }
        agency
    }

    require(
        route_short_name.isNotEmpty() || route_long_name.isNotEmpty()
    ) { "Either short or long name must be provided for route of id $route_id" }

    val routeType = RouteType.fromInt(route_type)
        ?: unknownValue("route type", route_type)

    val sortOrder = route_sort_order.takeIf { it != UNSET }

    val continuousPickup = ContinuousPassengerHandling.fromInt(continuous_pickup)
        ?: unknownValue("continuous pickup", continuous_pickup)

    val continuousDropOff = ContinuousPassengerHandling.fromInt(continuous_drop_off)
        ?: unknownValue("continuous drop off", continuous_drop_off)

    return Route(
        route_id,
        theAgency,
        route_short_name,
        route_long_name,
        route_desc,
        routeType,
        route_url.takeIfNotEmpty(),
        route_color.takeIfNotEmpty(),
        route_text_color.takeIfNotEmpty(),
        sortOrder,
        continuousPickup,
        continuousDropOff,
    )
}

private fun TripCsv.toTrip(route: Route, service: Calendar): Trip {
    require(trip_id.isNotEmpty()) {
        "trip_id must not be empty."
    }

    require(route_id == route.id) {
        "route_id: $route_id does not match route's id: ${route.id}"
    }
    require(service_id == service.id) {
        "service_id: $service_id does not match service's id: ${service.id}"
    }

    val direction = if (direction_id != UNSET) Direction.fromInt(direction_id)
        ?: unknownValue("direction id", direction_id)
        else null

    val wheelchairAccessibile =
        WheelchairAccessibility.fromInt(wheelchair_accessible)
            ?: unknownValue("wheelchair accessible", wheelchair_accessible)

    val bikesAllowed = BikesAllowed.fromInt(bikes_allowed)
        ?: unknownValue("bikes allowed", bikes_allowed)

    return Trip(
        route,
        service,
        trip_id,
        trip_headsign.takeIfNotEmpty(),
        trip_short_name.takeIfNotEmpty(),
        direction,
        block_id.takeIfNotEmpty(),
        shape_id.takeIfNotEmpty(),
        wheelchairAccessibile,
        bikesAllowed,
    )
}

private fun StopTimeCsv.toStopTime(trip: Trip, stop: Stop): StopTime {
    require(trip_id == trip.id) {
        "trip_id $trip_id does not match trip's id ${trip.id}"
    }
    require(stop_id == stop.id) {
        "stop_id does not match stop's id ${stop.id}"
    }

    require (stop_sequence != UNSET) {
        "stop_sequence must be set"
    }

    val arrivalTime = parseHour(arrival_time)

    val departureTime = parseHour(departure_time)

    require(arrivalTime != null || departureTime != null) {
        "At least one from departure time and arrival time must be set."
    }

    val pickupType = StopPassengerHandling.fromInt(pickup_type)
        ?: unknownValue("pickup type", pickup_type)

    val dropOffType = StopPassengerHandling.fromInt(drop_off_type)
        ?: unknownValue("drop off type", drop_off_type)

    val continuousPickup = ContinuousPassengerHandling.fromInt(continuous_pickup)
        ?: unknownValue("continuous pickup", continuous_pickup)

    val continuousDropOff = ContinuousPassengerHandling.fromInt(continuous_drop_off)
        ?: unknownValue("continuous drop off", continuous_drop_off)

    val shapeDistanceTravelled = if (shape_dist_travelled == UNSET.toDouble()) null
    else shape_dist_travelled

    val theTimepoint = Timepoint.fromInt(timepoint) ?: unknownValue(
        "timepoint", timepoint
    )

    return StopTime(
        trip,
        arrivalTime,
        departureTime,
        stop,
        stop_sequence,
        stop_headsign.takeIfNotEmpty(),
        pickupType,
        dropOffType,
        continuousPickup,
        continuousDropOff,
        shapeDistanceTravelled,
        theTimepoint
    )
}

private fun CalendarCsv.toCalendar(): Calendar {
    require(service_id.isNotEmpty()) {
        "service_id must not be empty."
    }

    requireSet(monday, "monday")
    requireSet(tuesday, "tuesday")
    requireSet(wednesday, "wednesday")
    requireSet(thursday, "thursday")
    requireSet(friday, "friday")
    requireSet(saturday, "saturday")
    requireSet(sunday, "sunday")

    return Calendar(
        service_id,
        monday == 1,
        tuesday == 1,
        wednesday == 1,
        thursday == 1,
        friday == 1,
        saturday == 1,
        sunday == 1,
        parseDate(start_date),
        parseDate(end_date)
    )
}

private fun CalendarDatesCsv.toCalendarDates(service: Calendar): CalendarDates {
    require(service_id == service.id) {
        "service_id $service_id does not match service's id ${service.id}"
    }
    val exceptionType = ExceptionType.fromInt(exception_type)
        ?: unknownValue("exception type", exception_type)

    return CalendarDates(
        service,
        parseDate(date),
        exceptionType
    )
}

private fun FeedInfoCsv.toFeedInfo(): FeedInfo {
    require(feed_publisher_name.isNotEmpty()) {
        "Feed publisher info can't be empty."
    }
    require(feed_publisher_url.isNotEmpty()) {
        "Feed publisher url can't be empty."
    }
    require(feed_lang.isNotEmpty()) {
        "Feed language can't be empty."
    }

    val startDate =
        if (feed_start_date.isNotEmpty()) parseDate(feed_start_date) else null

    val endDate =
        if (feed_end_date.isNotEmpty()) parseDate(feed_end_date) else null

    return FeedInfo(
        feed_publisher_name,
        feed_publisher_url,
        feed_lang,
        default_lang.takeIfNotEmpty(),
        startDate,
        endDate,
        feed_version.takeIfNotEmpty(),
        feed_contact_email.takeIfNotEmpty(),
        feed_contact_url.takeIfNotEmpty(),
    )
}

///////////////////////////////////////////////////////////////////////////
// UTIL
///////////////////////////////////////////////////////////////////////////

private fun String.takeIfNotEmpty() = takeIf { isNotEmpty() }

private fun unknownValue(name: String, value: Any): Nothing =
    throw IllegalStateException("Unknown $name value: $value")

private fun parseHour(time: String): LocalTime? {
    if (time == "") return null
    var hour = time.substring(0..1).toIntOrNull()
    val timeProper = if (hour != null && hour >= 24) {
        do {
            hour -= 24
        } while(hour >= 24)
        buildString {
            if (hour < 10) {
                append('0')
            }
            append(hour)
            append(time.substring(2))
        }
    }
    else {
        time
    }
    return try {
        LocalTime.parse(timeProper)
    } catch (e: DateTimeParseException) { null }
}

private fun parseDate(date: String) =
    try {
        LocalDate.of(
            date.substring(0 , 4).toInt(),
            date.substring(4 , 6).toInt(),
            date.substring(6, 8).toInt()
        )
    } catch (e: DateTimeParseException) {
        throw DateTimeParseException(
            "date is not in required YYYYMMDD format",
            date,
            e.errorIndex
        )
    }


private fun requireSet(value: Int, name: String) {
    require(value != UNSET) {
        "$name must be set."
    }
}









