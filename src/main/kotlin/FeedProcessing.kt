package me.dzikimlecz.libgtfskt

import java.time.LocalDateTime


data class UpcomingService(
    val line: String,
    val stop: String,
    val direction: String,
    val departure: LocalDateTime,
)