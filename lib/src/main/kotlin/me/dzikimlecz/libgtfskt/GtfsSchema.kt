package me.dzikimlecz.libgtfskt

import java.time.LocalDate
import java.time.LocalTime
import java.util.*

///////////////////////////////////////////////////////////////////////////
// Schema
///////////////////////////////////////////////////////////////////////////

data class GtfsSchema(
    val agencies: List<Agency>,
    val stops: List<Stop>,
    val routes: List<Route>,
    val trips: List<Trip>,
    val stopTimes: List<StopTime>,
    val calendars: List<Calendar>,
    val calendarDates: List<CalendarDates>,
)

///////////////////////////////////////////////////////////////////////////
// Objects
///////////////////////////////////////////////////////////////////////////

data class Agency(
    val id: String = "",
    val name: String,
    val url: String,
    val timezone: TimeZone,
    val lang: String? = null,
    val phone: String? = null,
    val fareUrl: String? = null,
    val email: String? = null,
)

data class Stop(
    val id: String,
    val code: String? = null,
    val name: String = "",
    val desc: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val zoneId: String = "",
    val url: String? = null,
    val locationType: LocationType = LocationType.DEFAULT,
    val parentStation: Stop? = null,
    val timezone: TimeZone? = null,
    val wheelchairBoarding: WheelchairAccessibility =
        WheelchairAccessibility.DEFAULT,
    val levelId: String? = null,
    val platformCode: String? = null,
)

data class Route(
    val id: String,
    val agency: Agency? = null,
    val shortName: String = "",
    val longName: String = "",
    val desc: String? = null,
    val type: RouteType,
    val url: String? = null,
    val color: String? = null,
    val textColor: String? = null,
    val sortOrder: Int? = null,
    val continuousPickup: ContinuousPassengerHandling =
        ContinuousPassengerHandling.DEFAULT,
    val continuousDropOff: ContinuousPassengerHandling =
        ContinuousPassengerHandling.DEFAULT,
)

data class Trip(
    val route: Route,
    val service: Calendar,
    val id: String,
    val headsign: String? = null,
    val shortName: String? = null,
    val direction: Direction? = null,
    val blockId: String? = null,
    val shapeId: String? = null,
    val wheelchairAccessible: WheelchairAccessibility =
        WheelchairAccessibility.DEFAULT,
    val bikesAllowed: BikesAllowed = BikesAllowed.DEFAULT,
)

data class StopTime(
    val trip: Trip,
    val arrivalTime: LocalTime? = null,
    val departureTime: LocalTime? = null,
    val stop: Stop,
    val stopSequence: Int,
    val stopHeadsign: String? = null,
    val pickupType: StopPassengerHandling = StopPassengerHandling.DEFAULT,
    val dropOffType: StopPassengerHandling = StopPassengerHandling.DEFAULT,
    val continuousPickup: ContinuousPassengerHandling =
        ContinuousPassengerHandling.DEFAULT,
    val continuousDropOff: ContinuousPassengerHandling =
        ContinuousPassengerHandling.DEFAULT,
    val shapeDistTravelled: Double? = null,
    val timepoint: Timepoint = Timepoint.DEFAULT,
)

data class Calendar(
    val id: String,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

data class CalendarDates(
    val service: Calendar,
    val date: LocalDate,
    val exceptionType: ExceptionType,
)


///////////////////////////////////////////////////////////////////////////
// ENUMS
///////////////////////////////////////////////////////////////////////////


/**
 * Type of the stop
 */
enum class LocationType {
    /**
     * Stop (or Platform).
     * A location where passengers board or disembark from a transit vehicle.
     * Is called a platform when defined within a parent_station.
     */
    STOP_PLATFORM,

    /**
     * Station.
     * A physical structure or area that contains one or more platform.
     */
    STATION,

    /**
     * Entrance/Exit.
     * A location where passengers can enter or exit a station from the street.
     * If an entrance/exit belongs to multiple stations,
     * it can be linked by pathways to both,
     * but the data provider must pick one of them as parent.
     */
    ENTRANCE_EXIT,

    /**
     * Generic Node.
     * A location within a station, not matching any other location_type,
     * which can be used to link together pathways define in pathways.txt.
     */
    GENERIC_NODE,

    /**
     * Boarding Area.
     * A specific location on a platform,
     * where passengers can board and/or alight vehicles
     */
    BOARDING_AREA,
    ;
    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(0)!!

    }
}

enum class WheelchairAccessibility {
    /**
     * For trip:
     * No accessibility information for the trip.
     * For parentless stops:
     * No accessibility information for the stop.
     * For child stops:
     * Stop will inherit its wheelchair_boarding behavior from the parent
     * station, if specified in the parent.
     * For station entrances/exits:
     * Station entrance will inherit its wheelchair_boarding behavior
     * from the parent station, if specified for the parent.
     */
    UNKNOWN,

    /**
     * For Trips:
     * Vehicle being used on this particular trip can accommodate
     * at least one rider in a wheelchair.
     * For parentless stops:
     * Some vehicles at this stop can be boarded by a rider in a wheelchair.
     * For child stops:
     * There exists some accessible path from outside the station
     * to the specific stop/platform.
     * For station entrances/exits:
     * There exists no accessible path from outside the station
     * to the specific stop/platform.
     */
    POSSIBLE,

    /**
     * For Trips:
     * No riders in wheelchairs can be accommodated on this trip.
     * For parentless stops:
     * Wheelchair boarding is not possible at this stop.
     * For child stops:
     * There exists no accessible path from outside the station
     * to the specific stop/platform.
     * For station entrances/exits:
     * No accessible path from station entrance to stops/platforms.
     */
    IMPOSSIBLE,
    ;
    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(0)!!

    }
}

enum class RouteType {

    /**
     * Tram, Streetcar, Light rail.
     * Any light rail or street level system within a metropolitan area.
     */
    TRAM,

    /**
     * Subway, Metro.
     * Any underground rail system within a metropolitan area.
     */
    METRO,

    /**
     * Rail.
     * Used for intercity or long-distance travel.
     */
    RAIL,

    /**
     * Bus.
     * Used for short- and long-distance bus routes.
     */
    BUS,

    /**
     * Ferry.
     * Used for short- and long-distance boat service.
     */
    FERRY,

    /**
     * Cable tram.
     * Used for street-level rail cars where the cable runs beneath the vehicle.
     */
    CABLE_TRAM,

    /**
     * Aerial lift, suspended cable car (e.g., gondola lift, aerial tramway).
     * Cable transport where cabins, cars, gondolas or open chairs are
     * suspended by means of one or more cables.
     */
    AERIAL_LIFT,

    /**
     * Funicular.
     * Any rail system designed for steep inclines.
     */
    FUNICULAR,

    /**
     * Trolleybus.
     * Electric buses that draw power from overhead wires using poles.
     */
    TROLLEYBUS,

    /**
     * Monorail.
     * Railway in which the track consists of a single rail or a beam.
     */
    MONOORAIL,
    ;

    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

    }
}

enum class ContinuousPassengerHandling {

    /**
     * Continuous stopping pickup/drop-off.
     */
    POSSIBLE,

    /**
     * No continuous stopping pickup/drop-off.
     */
    IMPOSSIBLE,

    /**
     * Must phone an agency to arrange continuous stopping pickup/drop-off.
     */
    TELEPHONE_ARRANGEMENT,

    /**
     * Must coordinate with a driver to arrange continuous
     * stopping pickup/drop-off.
     */
    DRIVER_ARRANGEMENT,
    ;

    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(1)!!

    }
}

enum class Direction {

    /**
     * Travel in one direction (e.g. outbound travel).
     */
    TO,

    /**
     * Travel in the opposite direction (e.g. inbound travel).
     */
    FROM,
    ;

    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(0)!!
    }
}

enum class BikesAllowed {

    /**
     * No bike information for the trip.
     */
    UNKNOWN,

    /**
     * Vehicle being used on this particular trip can accommodate
     * at least one bicycle.
     */
    ALLOWED,

    /**
     * No bicycles are allowed on this trip.
     */
    NOT_ALLOWED,
    ;

    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(0)!!
    }
}

enum class StopPassengerHandling {

    /**
     * Regular pickup/drop-off.
     */
    POSSIBLE,

    /**
     * No pickup/drop-off.
     */
    IMPOSSIBLE,

    /**
     * Must phone an agency to arrange pickup/drop-off.
     */
    TELEPHONE_ARRANGEMENT,

    /**
     * Must coordinate with a driver to arrange pickup/drop-off.
     */
    DRIVER_ARRANGEMENT,
    ;

    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(0)!!

    }
}

enum class Timepoint {

    /**
     * Times are considered approximate.
     */
    APPROXIMATE,

    /**
     * Times are considered exact.
     */
    EXACT,
    ;
    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal }

        val DEFAULT = fromInt(1)!!
    }

}

enum class ExceptionType {

    /**
     * Service has been added for the specified date.
     */
    SERVICE_ADDED,

    /**
     * Service has been removed for the specified date.
     */
    SERVICE_REMOVED,
    ;
    
    
    companion object {
        fun fromInt(ordinal: Int) =
            values().find { it.ordinal == ordinal + 1 }
    }
}
