package me.dzikimlecz.libgtfskt.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.File
import kotlin.reflect.KClass

///////////////////////////////////////////////////////////////////////////
// MAPPER
///////////////////////////////////////////////////////////////////////////

private val csvMapper = CsvMapper().apply {
    enable(CsvParser.Feature.TRIM_SPACES)
    enable(CsvParser.Feature.SKIP_EMPTY_LINES)
    enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE)
    disable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
}

///////////////////////////////////////////////////////////////////////////
// AGENCIES
///////////////////////////////////////////////////////////////////////////

private val agencySchema = CsvSchema.builder()
    .addColumn("agency_id")
    .addColumn("agency_name")
    .addColumn("agency_url")
    .addColumn("agency_timezone")
    .addColumn("agency_lang")
    .addColumn("agency_phone")
    .addColumn("agency_fare_url")
    .addColumn("agency_email")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()


fun readAgencies(inputFile: File): List<AgencyCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// STOPS
///////////////////////////////////////////////////////////////////////////

private val stopSchema = CsvSchema.builder()
    .addColumn("stop_id")
    .addColumn("stop_code")
    .addColumn("stop_name")
    .addColumn("stop_desc")
    .addNumberColumn("stop_lat")
    .addNumberColumn("stop_lon")
    .addColumn("zone_id")
    .addColumn("stop_url")
    .addNumberColumn("location_type")
    .addColumn("parent_station")
    .addColumn("stop_timezone")
    .addNumberColumn("wheelchair_boarding")
    .addColumn("level_id")
    .addColumn("platform_code")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readStops(inputFile: File): List<StopCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// ROUTES
///////////////////////////////////////////////////////////////////////////

private val routeSchema = CsvSchema.builder()
    .addColumn("route_id")
    .addColumn("agency_id")
    .addColumn("route_short_name")
    .addColumn("route_long_name")
    .addColumn("route_desc")
    .addNumberColumn("route_type")
    .addColumn("route_url")
    .addColumn("route_color")
    .addColumn("route_text_color")
    .addNumberColumn("route_sort_order")
    .addNumberColumn("continuous_pickup")
    .addNumberColumn("continuous_drop_off")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readRoutes(inputFile: File): List<RouteCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// TRIPS
///////////////////////////////////////////////////////////////////////////

private val tripSchema = CsvSchema.builder()
    .addColumn("route_id")
    .addColumn("service_id")
    .addColumn("trip_id")
    .addColumn("trip_headsign")
    .addColumn("trip_short_name")
    .addNumberColumn("direction_id")
    .addColumn("block_id")
    .addColumn("shape_id")
    .addNumberColumn("wheelchair_accessible")
    .addNumberColumn("bikes_allowed")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readTrips(inputFile: File): List<TripCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// STOP TIMES
///////////////////////////////////////////////////////////////////////////

private val stopTimeSchema = CsvSchema.builder()
    .addColumn("trip_id")
    .addColumn("arrival_time")
    .addColumn("departure_time")
    .addColumn("stop_id")
    .addNumberColumn("stop_sequence")
    .addColumn("stop_headsign")
    .addNumberColumn("pickup_type")
    .addNumberColumn("drop_off_type")
    .addNumberColumn("continuous_pickup")
    .addNumberColumn("continuous_drop_off")
    .addNumberColumn("shape_dist_travelled")
    .addNumberColumn("timepoint")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readStopTimes(inputFile: File): List<StopTimeCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// CALENDAR
///////////////////////////////////////////////////////////////////////////

private val calendarSchema = CsvSchema.builder()
    .addColumn("service_id")
    .addNumberColumn("monday")
    .addNumberColumn("tuesday")
    .addNumberColumn("wednesday")
    .addNumberColumn("thursday")
    .addNumberColumn("friday")
    .addNumberColumn("saturday")
    .addNumberColumn("sunday")
    .addColumn("start_date")
    .addColumn("end_date")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readCalendars(inputFile: File): List<CalendarCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// CALENDAR DATES
///////////////////////////////////////////////////////////////////////////

private val calendarDatesSchema = CsvSchema.builder()
    .addColumn("service_id")
    .addColumn("date")
    .addNumberColumn("exception_type")
    .setUseHeader(true)
    .setReorderColumns(true)
    .build()

fun readCalendarDates(inputFile: File): List<CalendarDatesCsv> =
    readItems(inputFile)

///////////////////////////////////////////////////////////////////////////
// BACKEND
///////////////////////////////////////////////////////////////////////////

private inline fun<reified T : Any> readItems(
    inputFile: File,
    type: KClass<T> = T::class
) = csvMapper.readerFor(type.java)
    .with(schema(type))
    .readValues<T>(inputFile.inputStream())
    .readAll()

private fun<T : Any> schema(type: KClass<T>): CsvSchema =
    when(type) {
        AgencyCsv::class -> agencySchema
        StopCsv::class -> stopSchema
        RouteCsv::class -> routeSchema
        TripCsv::class -> tripSchema
        StopTimeCsv::class -> stopTimeSchema
        CalendarCsv::class -> calendarSchema
        CalendarDatesCsv::class -> calendarDatesSchema
        else -> throw IllegalArgumentException(
            "There is no schema defined for class ${type.qualifiedName}"
        )
    }




