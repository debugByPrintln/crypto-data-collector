package com.cryptodatacollector.api;

import com.cryptodatacollector.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс CoinMarketCapApiClient предоставляет методы для взаимодействия с API CoinMarketCap.
 * Он позволяет получать данные о криптовалютах, такие как список последних котировок.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class CoinMarketCapApiClient {
    private static final String apiUrl = Dotenv.load().get("API_URL");
    private static final String apiKey = Dotenv.load().get("API_KEY");
    private final Gson gson;

    /**
     * Конструктор класса CoinMarketCapApiClient.
     */
    public CoinMarketCapApiClient() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    /**
     * Получает данные о криптовалютах с помощью CoinMarketCap API.
     *
     * @return JsonArray с данными о криптовалютах.
     * @throws IOException        Если произошла ошибка при выполнении HTTP запроса.
     * @throws URISyntaxException Если произошла ошибка при построении URI.
     */
    public JsonArray fetchCryptoData() throws IOException, URISyntaxException {
        List<NameValuePair> parameters = new ArrayList<>();

        // При необходимости, параметры запроса можно изменить
        parameters.add(new BasicNameValuePair("start", "1"));
        parameters.add(new BasicNameValuePair("limit", "3"));
        parameters.add(new BasicNameValuePair("convert", "USD"));

        String responseContent = makeAPICall(apiUrl, parameters);
        JsonObject responseJson = gson.fromJson(responseContent, JsonObject.class);

        if (!responseJson.has("data") || !responseJson.get("data").isJsonArray()) {
            throw new IllegalArgumentException("Invalid JSON response: 'data' array is missing or null");
        }

        return responseJson.getAsJsonArray("data");
    }

    /**
     * Выполняет HTTP GET запрос к указанному URI с заданными параметрами.
     *
     * @param uri        Базовый URI для запроса.
     * @param parameters Список параметров запроса.
     * @return Строка с содержимым ответа от сервера.
     * @throws URISyntaxException Если произошла ошибка при построении URI.
     * @throws IOException        Если произошла ошибка при выполнении HTTP запроса.
     */
    private String makeAPICall(String uri, List<NameValuePair> parameters) throws URISyntaxException, IOException {
        String responseContent = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        try (CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }

        return responseContent;
    }
}