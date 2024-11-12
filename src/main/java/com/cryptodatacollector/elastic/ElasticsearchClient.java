package com.cryptodatacollector.elastic;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;

/**
 * Класс ElasticsearchClient предоставляет методы для взаимодействия с Elasticsearch.
 * Он позволяет создавать индексы, индексировать документы и выполнять поисковые запросы.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class ElasticsearchClient {
    private final RestHighLevelClient client;

    /**
     * Конструктор класса ElasticsearchClient.
     *
     * @param client Экземпляр RestHighLevelClient для взаимодействия с Elasticsearch.
     */
    public ElasticsearchClient(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * Создает индекс в Elasticsearch, если он еще не существует.
     *
     * @param indexName Имя индекса, который нужно создать.
     * @throws IOException Если произошла ошибка при создании индекса.
     */
    public void createIndexIfNotExists(String indexName) throws IOException {
        if (!client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)) {
            client.indices().create(new CreateIndexRequest(indexName), RequestOptions.DEFAULT);
        }
    }

    /**
     * Индексирует документ в Elasticsearch.
     *
     * @param indexName  Имя индекса, в который нужно добавить документ.
     * @param id         Уникальный идентификатор документа.
     * @param jsonString JSON строка с данными документа.
     * @throws IOException Если произошла ошибка при индексации документа.
     */
    public void indexDocument(String indexName, String id, String jsonString) throws IOException {
        IndexRequest request = new IndexRequest(indexName).id(id).source(jsonString, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        System.out.println("-->     Indexed document with ID: " + response.getId());
    }

    /**
     * Выполняет поиск всех документов в указанном индексе.
     *
     * @param indexName Имя индекса, в котором нужно выполнить поиск.
     * @return JsonArray с найденными документами.
     * @throws IOException Если произошла ошибка при выполнении поискового запроса.
     */
    public JsonArray searchAllDocuments(String indexName) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        JsonArray hitsArray = new JsonArray();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            JsonElement jsonElement = JsonParser.parseString(hit.getSourceAsString());
            hitsArray.add(jsonElement);
        }
        return hitsArray;
    }
}