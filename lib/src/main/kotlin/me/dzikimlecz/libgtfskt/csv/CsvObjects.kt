package me.dzikimlecz.libgtfskt.csv

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgencyCsv(
    var agency_id: String = "",
    var agency_name: String,
    var agency_url: String,
    var agency_timezone: String,
    var agency_lang: String = "",
    var agency_phone: String = "",
    var agency_fare_url: String = "",
    var agency_email: String = "",
) {
    constructor() : this(
        agency_name = "",
        agency_url = "",
        agency_timezone = "",
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StopCsv(
    var stop_id: String,
    var stop_code: String = "",
    var stop_name: String = "",
    var stop_desc: String = "",
    var stop_lat: Double = 0.0,
    var stop_lon: Double = 0.0,
    var zone_id: String = "",
    var stop_url: String = "",
    var location_type: Int = 0,
    var parent_station: String = "",
    var stop_timezone: String = "",
    var wheelchair_boarding: Int = 0,
    var level_id: String = "",
    var platform_code: String = "",
) {

    constructor() : this(
        stop_id = "",
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class RouteCsv(
    var route_id: String,
    var agency_id: String = "",
    var route_short_name: String = "",
    var route_long_name: String = "",
    var route_desc: String = "",
    var route_type: Int,
    var route_url: String = "",
    var route_color: String = "",
    var route_text_color: String = "",
    var route_sort_order: Int = 0,
    var continuous_pickup: Int = 1,
    var continuous_drop_off: Int = 1,
) {
    constructor() : this(
        route_id = "",
        route_type = 0,
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class TripCsv(
    var route_id: String,
    var service_id: String,
    var trip_id: String,
    var trip_headsign: String = "",
    var trip_short_name: String = "",
    var direction_id: Int = 0,
    var block_id: String = "",
    var shape_id: String = "",
    var wheelchair_accessible: Int = 0,
    var bikes_allowed: Int = 0,
) {
    constructor() : this(
        route_id = "",
        service_id = "",
        trip_id = "",
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class StopTimeCsv(
    var trip_id: String,
    var arrival_time: String = "",
    var departure_time: String = "",
    var stop_id: String,
    var stop_sequence: Int,
    var stop_headsign: String = "",
    var pickup_type: Int = 0,
    var drop_off_type: Int = 0,
    var continuous_pickup: Int = 1,
    var continuous_drop_off: Int = 1,
    var shape_dist_travelled: Double = 0.0,
    var timepoint: Int = 1,
) {
    constructor() : this(
        trip_id = "",
        stop_id = "",
        stop_sequence = 0,
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CalendarCsv(
    var service_id: String,
    var monday: Int,
    var tuesday: Int,
    var wednesday: Int,
    var thursday: Int,
    var friday: Int,
    var saturday: Int,
    var sunday: Int,
    var start_date: String,
    var end_date: String,
) {
    constructor() : this(
        "",
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        "",
        "",
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CalendarDatesCsv(
    var service_id: String,
    var date: String,
    var exception_type: Int,
) {
    constructor() : this(
        "",
        "",
        0,
    )
}

