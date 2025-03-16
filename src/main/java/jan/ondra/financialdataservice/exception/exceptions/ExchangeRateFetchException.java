package jan.ondra.financialdataservice.exception.exceptions;

import jan.ondra.financialdataservice.exception.FinancialDataServiceException;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

public class ExchangeRateFetchException extends FinancialDataServiceException {

    public ExchangeRateFetchException(Throwable cause) {
        super(
            cause,
            "Fetching exchange rates failed: " + cause.getMessage(),
            Level.WARN,
            "Fetching exchange rates failed.",
            HttpStatus.BAD_GATEWAY
        );
    }

    public ExchangeRateFetchException(String message) {
        super(
            null,
            "Fetching exchange rates failed: " + message,
            Level.WARN,
            "Fetching exchange rates failed.",
            HttpStatus.BAD_GATEWAY
        );
    }

}
