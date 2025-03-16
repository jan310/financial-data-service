package jan.ondra.financialdataservice.exception.exceptions;

import jan.ondra.financialdataservice.exception.FinancialDataServiceException;
import org.springframework.http.HttpStatus;

public class InvalidRequestDataException extends FinancialDataServiceException {

    public InvalidRequestDataException() {
        super(null, null, null, "Invalid request data.", HttpStatus.BAD_REQUEST);
    }

}
