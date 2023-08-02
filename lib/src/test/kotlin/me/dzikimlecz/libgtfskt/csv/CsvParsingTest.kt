package me.dzikimlecz.libgtfskt.csv

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.io.File

class CsvParsingTest {

    @Test
    fun `readAgencies should return a nonempty list`() {
    //given
        val file = File(
            this.javaClass.classLoader
                .getResource("agency.txt")?.file
                ?: throw IllegalStateException()
        )
    //when
        val result = readAgencies(file)
    //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readStops should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("stops.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readStops(file)
        //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readRoutes should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("routes.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readRoutes(file)
        //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readTrips should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("trips.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readTrips(file)
        //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readStopTimes should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("stop_times.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readStopTimes(file)
        //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readCalendars should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("calendar.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readCalendars(file)
        //then
        assertFalse(result.isEmpty())
    }

    @Test
    fun `readCalendarDates should return a nonempty list`() {
        //given
        val file = File(
            this.javaClass.classLoader
                .getResource("calendar_dates.txt")?.file
                ?: throw IllegalStateException()
        )
        //when
        val result = readCalendarDates(file)
        //then
        assertFalse(result.isEmpty())
    }
}