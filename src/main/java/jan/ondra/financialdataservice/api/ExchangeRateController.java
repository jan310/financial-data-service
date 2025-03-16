package jan.ondra.financialdataservice.api;

import jan.ondra.financialdataservice.types.dtos.CurrencyConversionResultDto;
import jan.ondra.financialdataservice.business.ExchangeRateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/convert")
    @ResponseStatus(HttpStatus.OK)
    public CurrencyConversionResultDto convert(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam double amount
    ) {
        return exchangeRateService.convert(from, to, amount);
    }

}
