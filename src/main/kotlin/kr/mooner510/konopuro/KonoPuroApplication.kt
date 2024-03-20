package kr.mooner510.konopuro

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class KonoPuroApplication {
    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = Configuration()
        config.hostname = "localhost"
        config.port = 11092
        return SocketIOServer(config)
    }
}

fun main(args: Array<String>) {
    runApplication<KonoPuroApplication>(*args)
}
