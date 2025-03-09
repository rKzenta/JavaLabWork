package org.nmu.labwork.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nmu.labwork.models.ExchangeRate;
import org.nmu.labwork.repositories.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class ExchangeRateService {
    final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeRate getExchangeRate() {
        Optional<ExchangeRate> lastExchange = exchangeRateRepository.getFirstByOrderByCreatedAtDesc();
        if (lastExchange.isEmpty() || Duration.between(lastExchange.get().getCreatedAt(), OffsetDateTime.now()).toMinutes() > 10) {
            lastExchange.ifPresent(exchangeRateRepository::delete);
            lastExchange = Optional.of(exchangeRateRepository.save(getCurrentExchangeRate()));
        }
        return lastExchange.get();
    }


    private ExchangeRate getCurrentExchangeRate() {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=11"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new Exception(response.body());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            ExchangeRate[] exchangeRates = objectMapper.readValue(response.body(), ExchangeRate[].class);
            return Arrays.stream(exchangeRates).filter(c -> c.getCurrency().equals("USD")).findFirst().orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
