package jan.ondra.financialdataservice.business;

import jan.ondra.financialdataservice.client.OpenExchangeRatesClient;
import jan.ondra.financialdataservice.exception.exceptions.InvalidRequestDataException;
import jan.ondra.financialdataservice.types.dtos.CurrencyConversionResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private OpenExchangeRatesClient openExchangeRatesClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Nested
    class Convert {

        private final Map<String, Double> exchangeRates = Map.of(
            "USD", 1.0,
            "EUR", 0.921455,
            "GBP", 0.771889,
            "JPY", 148.7485,
            "SGD", 1.335
        );

        @Test
        @DisplayName("Should return correct data")
        void shouldReturnCorrectData() {
            when(openExchangeRatesClient.getUsdExchangeRates()).thenReturn(exchangeRates);

            var result = exchangeRateService.convert("EUR", "SGD", 123.45);

            assertThat(result).isEqualTo(new CurrencyConversionResultDto(
                "EUR",
                "SGD",
                123.45,
                178.85,
                1.448795654698276
            ));

            verify(openExchangeRatesClient, times(1)).getUsdExchangeRates();
        }

        @Test
        @DisplayName("Should throw correct exception when currency does not exist")
        void shouldThrowCorrectExceptionWhenCurrencyDoesNotExist() {
            when(openExchangeRatesClient.getUsdExchangeRates()).thenReturn(exchangeRates);

            assertThatThrownBy(() -> exchangeRateService.convert("hello", "SGD", 123.45))
                .isInstanceOf(InvalidRequestDataException.class);
        }

    }

}
