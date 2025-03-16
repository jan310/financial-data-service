package jan.ondra.financialdataservice.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jan.ondra.financialdataservice.exception.exceptions.ExchangeRateFetchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class OpenExchangeRatesClientTest {

    private final OpenExchangeRatesClient openExchangeRatesClient;

    public OpenExchangeRatesClientTest(WireMockRuntimeInfo wireMockRuntimeInfo) {
        openExchangeRatesClient = new OpenExchangeRatesClient(
            "test-app-id",
            wireMockRuntimeInfo.getHttpBaseUrl(),
            RestClient.builder()
        );
    }

    @Nested
    class GetUsdExchangeRates {

        @Test
        @DisplayName("Should return correct data")
        void shouldReturnCorrectData() throws IOException {
            stubFor(get(urlPathEqualTo("/api/latest.json"))
                .withQueryParam("app_id", equalTo("test-app-id"))
                .withQueryParam("base", equalTo("USD"))
                .willReturn(okJson(Files.readString(Path.of("src/test/resources/testdata/exchange_rates.json"))))
            );

            var result = openExchangeRatesClient.getUsdExchangeRates();

            assertThat(result).isEqualTo(Map.of(
                "USD", 1.0,
                "EUR", 0.921455,
                "GBP", 0.771889,
                "JPY", 148.7485,
                "SGD", 1.335
            ));

            verify(getRequestedFor(urlPathEqualTo("/api/latest.json"))
                .withQueryParam("app_id", equalTo("test-app-id"))
                .withQueryParam("base", equalTo("USD")));

        }

        @ParameterizedTest(name = "{0}")
        @CsvSource(
            textBlock = """
                Unexpected JSON response;{ "EUR": 0.85, "GBP": 0.75 };200
                Non-JSON response;asdf;200
                4xx Client Error;;400
                5xx Server Error;;500
                """,
            delimiter = ';'
        )
        @DisplayName("Should throw correct exception for Various API failures")
        void shouldThrowCorrectExceptionForVariousApiFailures(String testName, String responseBody, int statusCode) {
            stubFor(get(urlPathEqualTo("/api/latest.json"))
                .withQueryParam("app_id", equalTo("test-app-id"))
                .withQueryParam("base", equalTo("USD"))
                .willReturn(responseDefinition().withBody(responseBody).withStatus(statusCode))
            );

            assertThatThrownBy(openExchangeRatesClient::getUsdExchangeRates)
                .isInstanceOf(ExchangeRateFetchException.class);

            verify(getRequestedFor(urlPathEqualTo("/api/latest.json"))
                .withQueryParam("app_id", equalTo("test-app-id"))
                .withQueryParam("base", equalTo("USD")));
        }

    }

}
