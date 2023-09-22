package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MoneyTransfer {

    @NotNull
    @NotEmpty
    private final String accountFromId;

    @NotNull
    @NotEmpty
    private final String accountToId;

    @NotNull
    @Positive(message = "The amount to transfer must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public MoneyTransfer(@JsonProperty("accountFromId") String accountFromId,
                   @JsonProperty("accountToId") String accountToId,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }
}