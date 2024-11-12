package e2e;

import com.cryptodatacollector.elastic.ElasticsearchClient;
import com.cryptodatacollector.service.CryptoDataService;
import com.google.gson.JsonArray;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс CryptoDataCollectorE2ETest содержит end-to-end тесты для проверки функциональности сбора и индексации данных о криптовалютах.
 * Он использует Testcontainers для запуска экземпляра Elasticsearch в контейнере и проверяет, что данные успешно собираются и индексируются.
 *
 * @author debugByPrintln
 * @version 1.0
 */
@Testcontainers
public class CryptoDataCollectorE2ETest {

    /**
     * Контейнер Elasticsearch, запускаемый с помощью Testcontainers.
     */
    @Container
    private static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.0")
    );

    private RestHighLevelClient client;
    private CryptoDataService cryptoDataService;

    /**
     * Настройка тестового окружения перед каждым тестом.
     * Инициализирует клиент Elasticsearch и сервис для сбора данных.
     */
    @BeforeEach
    public void setUp() {
        client = new RestHighLevelClient(
                org.elasticsearch.client.RestClient.builder(
                        new org.apache.http.HttpHost(elasticsearch.getHost(), elasticsearch.getMappedPort(9200), "http")
                )
        );
        String apiKey = "471b8e74-26e5-4a1c-89c3-1640a4716009";
        cryptoDataService = new CryptoDataService(apiKey, client);
    }

    /**
     * Тест проверяет, что данные о криптовалютах успешно собираются и индексируются в Elasticsearch.
     *
     * @throws IOException        Если произошла ошибка при выполнении HTTP запроса или индексации данных.
     * @throws URISyntaxException Если произошла ошибка при построении URI.
     * @throws InterruptedException Если произошла ошибка при ожидании индексации данных.
     */
    @Test
    public void testCollectAndIndexData() throws IOException, URISyntaxException, InterruptedException {
        // Сбор и индексация данных
        cryptoDataService.collectAndIndexData();

        // Задержка, чтобы данные успели проиндексироваться
        Thread.sleep(15000); // 15 секунд


        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(client);
        JsonArray indexedData = elasticsearchClient.searchAllDocuments("crypto_data");

        // Проверки успешного выполнения
        assertThat(indexedData).isNotEmpty();
        assertThat(indexedData.size()).isGreaterThan(0);
        assertThat(indexedData.get(0).getAsJsonObject().get("symbol").getAsString()).isEqualTo("BTC");
    }
}