package me.dzikimlecz.libgtfskt

import java.time.LocalDateTime


data class UpcomingService(
    val line: Route,
    val stop: Stop,
    val direction: String,
    val departure: LocalDateTime,
)