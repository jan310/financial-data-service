package jan.ondra.financialdataservice.client;

import jan.ondra.financialdataservice.exception.exceptions.ExchangeRateFetchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class OpenExchangeRatesClient {

    private final String appId;
    private final RestClient restClient;

    public OpenExchangeRatesClient(
        @Value("${external-api.open-exchange-rates.app-id}") String appId,
        @Value("${external-api.open-exchange-rates.base-url}") String baseUrl,
        RestClient.Builder restClientBuilder
    ) {
        this.appId = appId;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Cacheable("usdExchangeRates")
    public Map<String, Double> getUsdExchangeRates() {
        ExchangeRatesResponse response;

        try {
            response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/latest.json")
                    .queryParam("app_id", appId)
                    .queryParam("base", "USD")
                    .build()
                )
                .retrieve()
                .body(ExchangeRatesResponse.class);
        } catch (RestClientException e) {
            throw new ExchangeRateFetchException(e);
        }

        if (response.rates() == null) {
            throw new ExchangeRateFetchException("Response JSON has unexpected format");
        }

        return response.rates();
    }

}

record ExchangeRatesResponse(Map<String, Double> rates) {}
