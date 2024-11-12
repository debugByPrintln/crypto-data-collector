package com.cryptodatacollector.analysis;

import com.cryptodatacollector.model.CryptoCurrency;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Класс CryptoDataAnalyzer предоставляет методы для анализа данных о криптовалютах, хранящихся в Elasticsearch.
 * Он позволяет вычислять среднюю цену криптовалюты за последний час и определять криптовалюту с максимальным процентным изменением цены за последний день.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class CryptoDataAnalyzer {
    private final RestHighLevelClient client;

    /**
     * Конструктор класса CryptoDataAnalyzer.
     *
     * @param client Экземпляр RestHighLevelClient для взаимодействия с Elasticsearch.
     */
    public CryptoDataAnalyzer(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * Возвращает криптовалюту с максимальным процентным изменением цены за последний день.
     *
     * @return Объект CryptoCurrency с максимальным процентным изменением цены или null, если данные отсутствуют.
     * @throws IOException Если произошла ошибка при выполнении запроса к Elasticsearch.
     */
    public CryptoCurrency getMaxPercentChangeCrypto() throws IOException {
        SearchRequest searchRequest = new SearchRequest("crypto_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.sort("percentChange24h", SortOrder.DESC);
        sourceBuilder.size(1);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.getHits().getTotalHits().value > 0) {
            Map<String, Object> sourceAsMap = searchResponse.getHits().getAt(0).getSourceAsMap();
            String id = (String) sourceAsMap.get("id");
            String name = (String) sourceAsMap.get("name");
            String symbol = (String) sourceAsMap.get("symbol");
            BigDecimal price = new BigDecimal(sourceAsMap.get("price").toString());
            BigDecimal volume24h = new BigDecimal(sourceAsMap.get("volume24h").toString());
            BigDecimal percentChange24h = new BigDecimal(sourceAsMap.get("percentChange24h").toString());
            LocalDateTime timestamp = LocalDateTime.parse(sourceAsMap.get("timestamp").toString());

            return new CryptoCurrency(id, name, symbol, price, volume24h, percentChange24h, timestamp);
        }
        else {
            return null;
        }
    }

    /**
     * Вычисляет среднюю цену криптовалюты за последний час.
     *
     * @param symbol Символ криптовалюты, для которой нужно вычислить среднюю цену.
     * @return Средняя цена криптовалюты за последний час.
     * @throws IOException Если произошла ошибка при выполнении запроса к Elasticsearch.
     */
    public double getAveragePriceLastHour(String symbol) throws IOException {
        SearchRequest searchRequest = new SearchRequest("crypto_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("symbol", symbol));
        sourceBuilder.aggregation(AggregationBuilders.avg("avg_price").field("price"));
        sourceBuilder.size(0);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Avg avg = searchResponse.getAggregations().get("avg_price");
        return avg.getValue();
    }
}
