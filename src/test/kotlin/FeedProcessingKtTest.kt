

import me.dzikimlecz.libgtfskt.feedProcessor
import me.dzikimlecz.libgtfskt.getFeed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class FeedProcessingKtTest {

    @Test
    fun `feed processor should get upcoming services`() {
    //given
        val file = File(
            this.javaClass.classLoader
                .getResource("feed")?.file
                ?: throw IllegalStateException()
        )
        val feed = getFeed(file)
        val reader = feedProcessor(feed)
    //when
        val services = reader.getUpcomingServicesFor("Jasna Rola")
    //then
        assertEquals(20, services.size)
    }
}