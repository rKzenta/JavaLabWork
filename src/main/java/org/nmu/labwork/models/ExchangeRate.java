package org.nmu.labwork.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Table(name = "currencies")
@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    long id;

    @JsonProperty("ccy")
    String currency;

    @JsonProperty("base_ccy")
    String baseCurrency;

    @JsonProperty("buy")
    float buyRate;

    @JsonProperty("sale")
    float saleRate;

    @JsonIgnore
    OffsetDateTime createdAt = OffsetDateTime.now();
}
