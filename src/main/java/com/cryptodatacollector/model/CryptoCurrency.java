package com.cryptodatacollector.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс CryptoCurrency представляет модель данных для криптовалюты.
 * Он содержит информацию о идентификаторе, названии, символе, цене, объеме торгов за последние 24 часа,
 * процентном изменении цены за последние 24 часа и временной метке.
 *
 * @author debugByPrintln
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class CryptoCurrency {
    private String id;
    private String name;
    private String symbol;
    private BigDecimal price;
    private BigDecimal volume24h;
    private BigDecimal percentChange24h;
    private LocalDateTime timestamp;
}
