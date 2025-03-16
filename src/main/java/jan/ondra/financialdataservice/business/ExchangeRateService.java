package jan.ondra.financialdataservice.business;

import jan.ondra.financialdataservice.client.OpenExchangeRatesClient;
import jan.ondra.financialdataservice.exception.exceptions.InvalidRequestDataException;
import jan.ondra.financialdataservice.types.dtos.CurrencyConversionResultDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private final OpenExchangeRatesClient openExchangeRatesClient;

    public ExchangeRateService(OpenExchangeRatesClient openExchangeRatesClient) {
        this.openExchangeRatesClient = openExchangeRatesClient;
    }

    public CurrencyConversionResultDto convert(String fromCurrency, String toCurrency, double amount) {
        var usdExchangeRates = openExchangeRatesClient.getUsdExchangeRates();

        if (!usdExchangeRates.containsKey(fromCurrency) || !usdExchangeRates.containsKey(toCurrency)) {
            throw new InvalidRequestDataException();
        }

        var exchangeRate = usdExchangeRates.get(toCurrency) / usdExchangeRates.get(fromCurrency);
        var convertedAmount = new BigDecimal(exchangeRate * amount)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();

        return new CurrencyConversionResultDto(
            fromCurrency,
            toCurrency,
            amount,
            convertedAmount,
            exchangeRate
        );
    }

}
