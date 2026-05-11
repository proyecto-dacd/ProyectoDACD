package org.ulpgc.dacd.infrastructure.scraper;

import org.ulpgc.dacd.model.NewsArticle;

public class NewsArticleValidator {

    public boolean isValid(NewsArticle article) {
        if (article == null) return false;
        if (article.getTitle() == null || article.getTitle().isBlank()) return false;
        if (article.getBody() == null || article.getBody().isBlank()) return false;
        if (article.getUrl() == null || article.getUrl().isBlank()) return false;

        String title = article.getTitle().toLowerCase();
        String articleUrl = article.getUrl().toLowerCase();

        if (isExcluded(title, articleUrl)) {
            return false;
        }

        return isCryptoRelevant(title);
    }

    private boolean isExcluded(String title, String articleUrl) {
        return title.contains("getting started")
                || title.contains("cutting edge")
                || title.equals("artificial intelligence")
                || articleUrl.equals("https://decrypt.co/")
                || articleUrl.contains("/university")
                || articleUrl.contains("/emerge")
                || articleUrl.contains("/collections/");
    }

    private boolean isCryptoRelevant(String title) {
        return title.contains("bitcoin")
                || title.contains("btc")
                || title.contains("ethereum")
                || title.contains("eth")
                || title.contains("xrp")
                || title.contains("ripple")
                || title.contains("solana")
                || title.contains("sol")
                || title.contains("avalanche")
                || title.contains("crypto")
                || title.contains("etf")
                || title.contains("defi")
                || title.contains("blockchain")
                || title.contains("binance")
                || title.contains("bnb")
                || title.contains("staking")
                || title.contains("token")
                || title.contains("tokens")
                || title.contains("stablecoin")
                || title.contains("nft")
                || title.contains("web3")
                || title.contains("miner")
                || title.contains("futures");
    }
}