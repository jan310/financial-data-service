package jan.ondra.financialdataservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateCacheManager {

    private final CacheManager cacheManager;

    public ExchangeRateCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 1 * * * *") // runs each hour after the first minute of the hour has passed
    public void evictCache() {
        cacheManager.getCache("usdExchangeRates").clear();
    }

}
