package kr.mooner510.konopuro.global.global.error

import kr.mooner510.konopuro.global.global.error.data.ErrorResponse
import kr.mooner510.konopuro.global.global.error.data.GlobalError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class ErrorHandler {
    @ExceptionHandler(GlobalError::class)
    fun globalExceptionHandler(error: GlobalError): ResponseEntity<*> {
        return ResponseEntity.status(error.errorCode.status).body(
            ErrorResponse(
                error.errorCode.ordinal,
                error.errorCode.message
            )
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                -1,
                e.message
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                -2,
                e.bindingResult.allErrors[0].defaultMessage.toString(),
            )
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<*> {
        var message = e.message ?: "Parameter type is mismatch"
        if (message.contains("; ")) {
            message = message.substringAfter("; ")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                -3,
                message
            )
        )
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun httpMediaTypeNotSupportedExceptionException(e: HttpMediaTypeNotSupportedException): ResponseEntity<*> {
        val message = e.message ?: "Media Type Not Supported"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                -4,
                message
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableExceptionException(e: HttpMessageNotReadableException): ResponseEntity<*> {
        val message = e.message ?: "Body or multipart data cannot parse"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                -5,
                message
            )
        )
    }
}