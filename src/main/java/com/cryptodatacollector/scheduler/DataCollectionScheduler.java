package com.cryptodatacollector.scheduler;

import com.cryptodatacollector.analysis.CryptoDataAnalyzer;
import com.cryptodatacollector.model.CryptoCurrency;
import com.cryptodatacollector.service.CryptoDataService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

/**
 * Класс DataCollectionScheduler отвечает за планирование и выполнение задач по сбору и анализу данных о криптовалютах.
 * Он использует Quartz Scheduler для периодического выполнения задачи сбора данных.
 *
 * @author debugByPrintln
 * @version 1.0
 */
public class DataCollectionScheduler {
    private final CryptoDataService cryptoDataService;
    private final CryptoDataAnalyzer cryptoDataAnalyzer;

    /**
     * Конструктор класса DataCollectionScheduler.
     *
     * @param cryptoDataService  Сервис для сбора и индексации данных о криптовалютах.
     * @param cryptoDataAnalyzer Сервис для анализа данных о криптовалютах.
     */
    public DataCollectionScheduler(CryptoDataService cryptoDataService, CryptoDataAnalyzer cryptoDataAnalyzer) {
        this.cryptoDataService = cryptoDataService;
        this.cryptoDataAnalyzer = cryptoDataAnalyzer;
    }

    /**
     * Запускает планировщик задач с указанным интервалом выполнения.
     *
     * @param intervalInSeconds Интервал выполнения задачи в секундах.
     * @throws SchedulerException Если произошла ошибка при запуске планировщика.
     */
    public void startScheduler(int intervalInSeconds) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(DataCollectionJob.class)
                .withIdentity("dataCollectionJob", "group1")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dataCollectionTrigger", "group1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.getContext().put("cryptoDataService", cryptoDataService);
        scheduler.getContext().put("cryptoDataAnalyzer", cryptoDataAnalyzer);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * Внутренний класс, реализующий интерфейс Job для выполнения задачи сбора и анализа данных.
     */
    public static class DataCollectionJob implements Job {

        /**
         * Метод выполнения задачи сбора и анализа данных.
         *
         * @param context Контекст выполнения задачи.
         * @throws JobExecutionException Если произошла ошибка при выполнении задачи.
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("-->     Executing data collection job at: " + LocalDateTime.now());
            CryptoDataService cryptoDataService = null;
            CryptoDataAnalyzer cryptoDataAnalyzer = null;
            try {
                cryptoDataService = (CryptoDataService) context.getScheduler().getContext().get("cryptoDataService");
                cryptoDataAnalyzer = (CryptoDataAnalyzer) context.getScheduler().getContext().get("cryptoDataAnalyzer");
                cryptoDataService.collectAndIndexData();

                String avgPriceCheckCryptoSymbol = "BTC";

                try{
                    double avgPrice = cryptoDataAnalyzer.getAveragePriceLastHour(avgPriceCheckCryptoSymbol);
                    System.out.printf("-->     Average price of %s in the last hour: %f \n", avgPriceCheckCryptoSymbol, avgPrice);
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                try{
                    CryptoCurrency maxPercentChangeCrypto = cryptoDataAnalyzer.getMaxPercentChangeCrypto();
                    if (maxPercentChangeCrypto != null){
                        System.out.printf("-->     Crypto with max percent change in the last day: %s with max percent change of: %f \n",
                                maxPercentChangeCrypto.getSymbol(),
                                maxPercentChangeCrypto.getPercentChange24h());
                    }
                    else{
                        System.out.println("No crypto with max percent change");
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }

            }
            catch (SchedulerException | IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}