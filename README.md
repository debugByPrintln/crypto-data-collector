# Crypto Data Collector

## Автор
Мельников Никита Сергеевич
- Выполнение тестового задания: https://docs.google.com/document/d/1wW70asO6XmOLXWlnTDraFwZLavGuoIAHvppBAD6Pke4/edit?tab=t.0

## Описание

Crypto Data Collector — это приложение на Java, которое периодически собирает информацию о криптовалютах с помощью API CoinMarketCap и индексирует данные в Elasticsearch. 
Приложение также выполняет анализ данных, такой как вычисление средней цены криптовалюты за последний час и определение криптовалюты с максимальным процентным изменением цены за последний день.

## Требования

- Java 17
- Maven
- Docker
- API ключ от CoinMarketCap (можно получить на [CoinMarketCap](https://coinmarketcap.com/))

## Подготовка к запуску

Для запуска приложения необходимо внести следующие изменения в файл .env:
- Вставить свой ключ для CoinMarketCap API в поле API_KEY
- Установить интервал для сбора данных в секундах в поле DATA_GATHER_INTERVAL_IN_SECONDS (Обратите внимание, что при базовом плане доступно лишь 30 запросов в минуту)
- При необходимости, можно изменить API_URL на https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/listings/latest для доступа к тестовому API.

Для удобства тестирования данное приложение запрашивает информацию только о 3-х криптовалютах. Для того, чтобы запрашивать данные о большем количестве криптовалют, 
необходимо поменять параметр запроса limit в методе fetchCryptoData() класса CoinMarketCapApiClient() (Например, установить его равным 5000, как показано в примере документации CoinMarketCap API).

```java
parameters.add(new BasicNameValuePair("start", "1"));
parameters.add(new BasicNameValuePair("limit", "3")); <-------
parameters.add(new BasicNameValuePair("convert", "USD"));
```

## Запуск тестов

Для запуска тестов необходимо запустить комманду:
```bash
mvn clean test
```

## Запуск приложения

Для запуска приложения необходимо ввести команду:
```bash
docker-compose up --build
```

## Работа приложения 

Приложению потребуется время, чтобы развернуть Elasticsearch в Docker.
После запуска, в терминале должна начать появляться информация, содержащая сведения о том, что документ с определенным ID был индексирован, 
а так же сведения о средней цене BTC (выбран в качестве примера) за последний час 
и криптовалюте с наибольшем изменением цены за последние 24 часа.
Эти сведения отмечаются в терминале символом "-->" для большей наглядности.
Данная информация будет обновляться каждый N секунд (частота обновлений выставляется в поле DATA_GATHER_INTERVAL_IN_SECONDS в .env файле):

Пример вывода приложения:
```
<p style="color:red;">crypto-data-collector  | -->     Executing data collection job at: 2024-11-12T08:28:28.856416845</p>
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [HEAD http://elasticsearch:9200/crypto_data] returned 1 warnings: [299 Elasticsearch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security features are not enabled
. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [PUT http://elasticsearch:9200/crypto_data/_doc/1?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security feature
s are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | -->     Indexed document with ID: 1
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [PUT http://elasticsearch:9200/crypto_data/_doc/1027?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security feat
ures are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | -->     Indexed document with ID: 1027
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [PUT http://elasticsearch:9200/crypto_data/_doc/825?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security featu
res are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | -->     Indexed document with ID: 825
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [POST http://elasticsearch:9200/crypto_data/_search?typed_keys=true&max_concurrent_shard_requests=5&search_type=query_then_fetch&batched_reduce_size=512] returned 1 warnings: [299 Elasticsea
rch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/ref
erence/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | -->     Average price of BTC in the last hour: 88894.632813
crypto-data-collector  | Nov 12, 2024 8:28:29 AM org.elasticsearch.client.RestClient logResponse
crypto-data-collector  | WARNING: request [POST http://elasticsearch:9200/crypto_data/_search?typed_keys=true&max_concurrent_shard_requests=5&search_type=query_then_fetch&batched_reduce_size=512] returned 1 warnings: [299 Elasticsea
rch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/ref
erence/7.17/security-minimal-setup.html to enable security."]
crypto-data-collector  | -->     Crypto with max percent change in the last day: BTC with max percent change of: 9.551411
```

Предупреждения возникают,  потому что встроенные функции безопасности Elasticsearch не включены.
```
WARNING: request [PUT http://elasticsearch:9200/crypto_data/_doc/1?timeout=1m] returned 1 warnings: [299 Elasticsearch-7.17.0-bee86328705acaa9a6daede7140defd4d9ec56bd "Elasticsearch built-in security feature
s are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.17/security-minimal-setup.html to enable security."]
```
Их можно игнорировать, так как они не влияют на работу приложения.
Для их исправления неообходимо сгенерировать сертификаты безопасности и добавить их к файлу конфигурации elasticsearch.yml. 
Однако, так как Elasticsearch заупускается локально и используется для демонстрации работы приложения, я решил эту проблему не исправлять.

Также, может возникать следующая ошибка.
```
crypto-data-collector  | 08:42:35.630 [DefaultQuartzScheduler_Worker-1] ERROR org.quartz.core.JobRunShell - Job group1.dataCollectionJob threw an unhandled Exception: 
crypto-data-collector  | org.elasticsearch.ElasticsearchException: java.util.concurrent.ExecutionException: java.net.ConnectException: Connection refused
```
Она связана с тем, что Elastisearch еще не успел запуститься, а приложение уже пытается подключиться к нему.
В таком случае, нужно просто еще немного подождать.
