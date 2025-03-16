package jan.ondra.financialdataservice.api;

import jan.ondra.financialdataservice.business.ExchangeRateService;
import jan.ondra.financialdataservice.exception.exceptions.ExchangeRateFetchException;
import jan.ondra.financialdataservice.exception.exceptions.InvalidRequestDataException;
import jan.ondra.financialdataservice.types.dtos.CurrencyConversionResultDto;
import jan.ondra.financialdataservice.util.UserIdExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static jan.ondra.financialdataservice.util.RestDocsUtil.documentResult;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets/exchange-rate")
class ExchangeRateControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateService exchangeRateService;
    @MockitoBean
    private UserIdExtractor userIdExtractor;

    @Nested
    class Convert {

        @Test
        @DisplayName("Should return status code 200 and correct response body")
        void shouldReturnCorrectStatusCodeAndResponseBody() throws Exception {
            when(exchangeRateService.convert("EUR", "SGD", 123.45)).thenReturn(
                new CurrencyConversionResultDto(
                    "EUR",
                    "SGD",
                    123.45,
                    178.85,
                    1.448795654698276
                )
            );

            mockMvc
                .perform(
                    get("/api/currencies/convert")
                        .header(AUTHORIZATION, "fake token")
                        .queryParam("from", "EUR")
                        .queryParam("to", "SGD")
                        .queryParam("amount", "123.45")
                )
                .andExpectAll(
                    status().isOk(),
                    content().json(
                        """
                            {
                                "fromCurrency": "EUR",
                                "toCurrency": "SGD",
                                "amount": 123.45,
                                "convertedAmount": 178.85,
                                "exchangeRate": 1.448795654698276
                            }
                            """
                    )
                )
                .andDo(documentResult("convert_success"));
        }

        @Test
        @DisplayName("Should return status code 400 and error message when unknown currency is provided")
        void shouldReturnStatusCode400AndErrorMessageWhenUnknownCurrencyIsProvided() throws Exception {
            when(exchangeRateService.convert("abc", "SGD", 123.45))
                .thenThrow(new InvalidRequestDataException());

            mockMvc
                .perform(
                    get("/api/currencies/convert")
                        .header(AUTHORIZATION, "fake token")
                        .queryParam("from", "abc")
                        .queryParam("to", "SGD")
                        .queryParam("amount", "123.45")
                )
                .andExpectAll(
                    status().isBadRequest(),
                    content().json(
                        """
                            {
                                "errorMessage": "Invalid request data."
                            }
                            """
                    )
                )
                .andDo(documentResult("convert_error-invalid_request_data"));
        }

        @Test
        @DisplayName("Should return status code 502 and error message when calling the external API throws an error")
        void shouldReturnStatusCode502AndErrorMessageWhenCallingTheExternalApiThrowsAnError() throws Exception {
            when(exchangeRateService.convert("EUR", "SGD", 123.45))
                .thenThrow(new ExchangeRateFetchException("Response JSON has unexpected format"));

            mockMvc
                .perform(
                    get("/api/currencies/convert")
                        .header(AUTHORIZATION, "fake token")
                        .queryParam("from", "EUR")
                        .queryParam("to", "SGD")
                        .queryParam("amount", "123.45")
                )
                .andExpectAll(
                    status().isBadGateway(),
                    content().json(
                        """
                            {
                                "errorMessage": "Fetching exchange rates failed."
                            }
                            """
                    )
                )
                .andDo(documentResult("convert_error-fetching_exchange_rates_failed"));
        }

    }

}