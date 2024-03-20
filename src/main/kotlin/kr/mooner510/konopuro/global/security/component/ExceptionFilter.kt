package kr.mooner510.konopuro.global.security.component

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.mooner510.konopuro.global.global.error.data.ErrorResponse
import kr.mooner510.konopuro.global.global.error.data.GlobalError
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: GlobalError) {
            response.status = e.errorCode.status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            objectMapper.writeValue(
                response.writer, ErrorResponse(
                    e.errorCode.ordinal,
                    e.errorCode.message,
                )
            )
        } catch (e: Exception) {
            response.status = 500
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            e.printStackTrace()
            objectMapper.writeValue(
                response.writer, ErrorResponse(
                    -999,
                    e.message ?: "Unknown Error",
                )
            )
            e.printStackTrace()
        }
    }
}