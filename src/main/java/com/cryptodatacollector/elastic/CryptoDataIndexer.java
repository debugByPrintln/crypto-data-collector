package com.cryptodatacollector.elastic;

import com.cryptodatacollector.model.CryptoCurrency;
import com.cryptodatacollector.util.LocalDateTimeAdapter;
import com.google.gson.*;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс CryptoDataIndexer отвечает за индексацию данных о криптовалютах в Elasticsearch.
 * Он преобразует данные из формата JSON в объекты CryptoCurrency и индексирует их в Elasticsearch.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class CryptoDataIndexer {
    private final ElasticsearchClient elasticsearchClient;
    private final String indexName = "crypto_data";
    private final Gson gson;

    /**
     * Конструктор класса CryptoDataIndexer.
     *
     * @param client Экземпляр RestHighLevelClient для взаимодействия с Elasticsearch.
     */
    public CryptoDataIndexer(RestHighLevelClient client) {
        this.elasticsearchClient = new ElasticsearchClient(client);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Индексирует данные о криптовалютах в Elasticsearch.
     *
     * @param cryptoData JsonArray с данными о криптовалютах.
     * @throws IOException Если произошла ошибка при индексации данных.
     */
    public void indexCryptoData(JsonArray cryptoData) throws IOException {
        elasticsearchClient.createIndexIfNotExists(indexName);

        for (JsonElement element : cryptoData) {
            JsonObject cryptoCurrencyJson = element.getAsJsonObject();
            CryptoCurrency cryptoCurrency = convertJsonToCryptoCurrency(cryptoCurrencyJson);
            String jsonString = gson.toJson(cryptoCurrency);
            elasticsearchClient.indexDocument(indexName, cryptoCurrency.getId(), jsonString);
        }
    }

    /**
     * Преобразует JSON объект в объект CryptoCurrency.
     *
     * @param json JsonObject с данными о криптовалюте.
     * @return Объект CryptoCurrency.
     */
    private CryptoCurrency convertJsonToCryptoCurrency(JsonObject json) {
        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        String symbol = json.get("symbol").getAsString();
        BigDecimal price = json.getAsJsonObject("quote").getAsJsonObject("USD").get("price").getAsBigDecimal();
        BigDecimal volume24h = json.getAsJsonObject("quote").getAsJsonObject("USD").get("volume_24h").getAsBigDecimal();
        BigDecimal percentChange24h = json.getAsJsonObject("quote").getAsJsonObject("USD").get("percent_change_24h").getAsBigDecimal();
        LocalDateTime timestamp = LocalDateTime.now();

        return new CryptoCurrency(id, name, symbol, price, volume24h, percentChange24h, timestamp);
    }
}
