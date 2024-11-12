package com.cryptodatacollector;

import com.cryptodatacollector.analysis.CryptoDataAnalyzer;

import com.cryptodatacollector.scheduler.DataCollectionScheduler;
import com.cryptodatacollector.service.CryptoDataService;
import io.github.cdimascio.dotenv.Dotenv;
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
        String elasticsearchHost = System.getenv("ELASTICSEARCH_HOST");
        if (elasticsearchHost == null) {
            elasticsearchHost = "http://localhost:9200";
        }

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(elasticsearchHost))
        );

        CryptoDataService cryptoDataService = new CryptoDataService(client);
        CryptoDataAnalyzer cryptoDataAnalyzer = new CryptoDataAnalyzer(client);

        DataCollectionScheduler scheduler = new DataCollectionScheduler(cryptoDataService, cryptoDataAnalyzer);

        // Сбор данных производится каждые 30 секунд. При необходимости, значение можно изменить в .env файле
        scheduler.startScheduler(Integer.parseInt(Dotenv.load().get("DATA_GATHER_INTERVAL_IN_SECONDS")));
    }
}