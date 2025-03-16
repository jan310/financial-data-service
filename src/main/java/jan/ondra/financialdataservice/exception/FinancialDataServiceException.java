package jan.ondra.financialdataservice.exception;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

public class FinancialDataServiceException extends RuntimeException {

    private final Level logLevel;
    private final String clientMessage;
    private final HttpStatus httpStatusCode;

    public FinancialDataServiceException(
        Throwable cause,
        String message,
        Level logLevel,
        String clientMessage,
        HttpStatus httpStatusCode
    ) {
        super(message, cause);
        this.logLevel = logLevel;
        this.clientMessage = clientMessage;
        this.httpStatusCode = httpStatusCode;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

}
