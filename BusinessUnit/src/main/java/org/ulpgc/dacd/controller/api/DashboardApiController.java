package org.ulpgc.dacd.controller.api;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.ulpgc.dacd.controller.ingestion.RealTimeSubscriber;
import org.ulpgc.dacd.controller.sentiment.SentimentProvider;
import org.ulpgc.dacd.model.entities.CryptoPrice;
import org.ulpgc.dacd.model.entities.MarketIndicators;
import org.ulpgc.dacd.model.logic.PearsonCalculator;
import org.ulpgc.dacd.model.persistence.DatamartManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DashboardApiController {
    private static final List<WsContext> clients = new CopyOnWriteArrayList<>();
    private final DatamartManager datamartManager;
    private final SentimentProvider sentimentProvider;

    public DashboardApiController(DatamartManager datamartManager, SentimentProvider sentimentProvider) {
        this.datamartManager = datamartManager;
        this.sentimentProvider = sentimentProvider;
    }

    public void startServer() {
        Javalin app = Javalin.create(config -> config.staticFiles.add("/public")).start(7070);
        setupWebSockets(app);
        setupEndpoints(app);
        System.out.println("[Dashboard] API lista en http://localhost:7070");
    }

    private void setupWebSockets(Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(clients::add);
            ws.onClose(clients::remove);
        });
    }

    private void setupEndpoints(Javalin app) {
        app.get("/api/monedas", ctx -> ctx.json(datamartManager.getAvailableCryptos()));
        app.get("/api/noticias/{id}", ctx -> ctx.json(datamartManager.getRelatedNews(ctx.pathParam("id"))));
        app.get("/api/indicadores/{id}", ctx -> ctx.json(processIndicators(ctx.pathParam("id"))));
    }


    private MarketIndicators processIndicators(String id) {
        List<Double> prices = datamartManager.getPriceHistory(id);
        List<Double> sentiments = datamartManager.getSentimentHistory(id);
        CryptoPrice latest = getLatestData(id);

        double priceChange = calculateVolatility(prices);
        boolean isVolatile = checkAlertStatus(priceChange);

        return new MarketIndicators(
                PearsonCalculator.calculate(prices, sentiments),
                sentimentProvider.getSentiment(datamartManager.getRelatedNews(id)),
                id,
                latest.price(),
                latest.timestamp(),
                prices,
                datamartManager.getTimestampHistory(id),
                priceChange,
                isVolatile
        );
    }

    private CryptoPrice getLatestData(String id) {
        return datamartManager.getLatestPrices().getOrDefault(id, new CryptoPrice(id, 0.0, "N/A", 0.0));
    }

    private double calculateVolatility(List<Double> prices) {
        if (prices.size() < 2) return 0.0;
        double current = prices.get(prices.size() - 1);
        double previous = prices.get(prices.size() - 2);
        return (previous != 0) ? ((current - previous) / previous) * 100 : 0.0;
    }

    private boolean checkAlertStatus(double priceChange) {
        return Math.abs(priceChange) >= RealTimeSubscriber.VOLATILITY_THRESHOLD;
    }

    public static void broadcastUpdate() {
        clients.removeIf(client -> !client.session.isOpen());
        clients.forEach(client -> client.send("update"));
    }
}