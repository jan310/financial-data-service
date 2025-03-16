package jan.ondra.financialdataservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jan.ondra.financialdataservice.types.dtos.ErrorDto;
import jan.ondra.financialdataservice.util.UserIdExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final UserIdExtractor userIdExtractor;

    public GlobalExceptionHandler(UserIdExtractor userIdExtractor) {
        this.userIdExtractor = userIdExtractor;
    }

    @ExceptionHandler(FinancialDataServiceException.class)
    public ResponseEntity<ErrorDto> handleFinancialDataServiceException(
        FinancialDataServiceException e,
        HttpServletRequest request
    ) {
        if (e.getLogLevel() != null) {
            logger.makeLoggingEventBuilder(e.getLogLevel()).log(
                buildLogMessage(
                    e.getClass().getName(),
                    userIdExtractor.extractFromBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION)),
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage()
                )
            );
        }

        return new ResponseEntity<>(new ErrorDto(e.getClientMessage()), e.getHttpStatusCode());
    }

    private String buildLogMessage(
        String exceptionType,
        String userId,
        String requestMethod,
        String requestPath,
        String log
    ) {
        return String.format(
            "Request failed: %s [userID: '%s' | endpoint: '%s %s' | cause: '%s']",
            exceptionType,
            userId,
            requestMethod,
            requestPath,
            log
        );
    }

}
