package me.dzikimlecz.libgtfskt


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

        val DEFAULT = fromInt(0)

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

        val DEFAULT = fromInt(0)

    }
}

enum class ContinuousPassengkerHandling {

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

        val DEFAULT = fromInt(1)

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

        val DEFAULT = fromInt(0)
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

        val DEFAULT = fromInt(0)
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

        val DEFAULT = fromInt(0)

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

        val DEFAULT = fromInt(1)
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
