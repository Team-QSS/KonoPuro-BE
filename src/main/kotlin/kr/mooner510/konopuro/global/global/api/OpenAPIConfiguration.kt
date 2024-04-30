package kr.mooner510.konopuro.global.global.api

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "KonoPuro-API-Documentation",
        version = "Unknown"
    )
)
@Configuration
class OpenAPIConfiguration {
    @Bean
    fun globalAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("_Global")
            .displayName("모든 API")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun authAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Auth")
            .displayName("인증 API")
            .pathsToMatch("/api/auth/**")
            .build()
    }

    @Bean
    fun cardAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Card")
            .displayName("카드 API")
            .pathsToMatch("/api/card/**")
            .build()
    }

    @Bean
    fun gameAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Game")
            .displayName("게임 로직 API")
            .pathsToMatch("/api/game/**")
            .build()
    }

    @Bean
    fun gatchaAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Gatcha")
            .displayName("가챠 API")
            .pathsToMatch("/api/gatcha/**")
            .build()
    }

    @Bean
    fun inventoryAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Inventory")
            .displayName("인벤 API")
            .pathsToMatch("/api/inventory/**")
            .build()
    }
}