package com.cryptodatacollector.service;

import com.cryptodatacollector.api.CoinMarketCapApiClient;
import com.cryptodatacollector.elastic.CryptoDataIndexer;
import com.google.gson.JsonArray;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Класс CryptoDataService отвечает за сбор и индексацию данных о криптовалютах.
 * Он использует CoinMarketCapApiClient для получения данных и CryptoDataIndexer для их индексации в Elasticsearch.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class CryptoDataService {
    private final CoinMarketCapApiClient apiClient;
    private final CryptoDataIndexer indexer;

    /**
     * Конструктор класса CryptoDataService.
     *
     * @param apiKey API ключ для доступа к CoinMarketCap API.
     * @param client Экземпляр RestHighLevelClient для взаимодействия с Elasticsearch.
     */
    public CryptoDataService(String apiKey, RestHighLevelClient client) {
        this.apiClient = new CoinMarketCapApiClient(apiKey);
        this.indexer = new CryptoDataIndexer(client);
    }

    /**
     * Собирает и индексирует данные о криптовалютах.
     *
     * @throws IOException        Если произошла ошибка при выполнении HTTP запроса или индексации данных.
     * @throws URISyntaxException Если произошла ошибка при построении URI.
     */
    public void collectAndIndexData() throws IOException, URISyntaxException {
        try {
            JsonArray cryptoData = apiClient.fetchCryptoData();
            indexer.indexCryptoData(cryptoData);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}