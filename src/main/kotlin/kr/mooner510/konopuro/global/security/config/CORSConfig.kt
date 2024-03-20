package kr.mooner510.konopuro.global.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CORSConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost",
//                "https://api.dsm-dongpo.com",
                "https://dsm-dongpo.com",
                "https://www.dsm-dongpo.com",
//                "dongpo-frontend.vercel.app",
//                "*"
            )
            .allowedMethods("*")
//            .allowCredentials(true)
    }
}