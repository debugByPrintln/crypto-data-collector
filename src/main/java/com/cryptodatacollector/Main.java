package com.cryptodatacollector;

import com.cryptodatacollector.analysis.CryptoDataAnalyzer;

import com.cryptodatacollector.scheduler.DataCollectionScheduler;
import com.cryptodatacollector.service.CryptoDataService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.quartz.SchedulerException;

/**
 * Главный класс приложения, который инициализирует сервисы для сбора, анализа и индексации данных о криптовалютах.
 * Он также запускает планировщик задач для периодического выполнения этих операций.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class Main {
    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки (не используются).
     * @throws SchedulerException Если произошла ошибка при запуске планировщика задач.
     */
    public static void main(String[] args) throws SchedulerException {
        String apiKey = "471b8e74-26e5-4a1c-89c3-1640a4716009";

        String elasticsearchHost = System.getenv("ELASTICSEARCH_HOST");
        if (elasticsearchHost == null) {
            elasticsearchHost = "http://localhost:9200";
        }

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(elasticsearchHost))
        );

        CryptoDataService cryptoDataService = new CryptoDataService(apiKey, client);
        CryptoDataAnalyzer cryptoDataAnalyzer = new CryptoDataAnalyzer(client);

        DataCollectionScheduler scheduler = new DataCollectionScheduler(cryptoDataService, cryptoDataAnalyzer);

        // Сбор данных производится каждые 30 секунд. При необходимости, значение можно изменить
        scheduler.startScheduler(30);
    }
}