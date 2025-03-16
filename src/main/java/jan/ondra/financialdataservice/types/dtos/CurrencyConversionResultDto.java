package jan.ondra.financialdataservice.types.dtos;

public record CurrencyConversionResultDto(
    String fromCurrency,
    String toCurrency,
    double amount,
    double convertedAmount,
    double exchangeRate
) { }
