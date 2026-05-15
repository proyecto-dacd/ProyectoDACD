package org.ulpgc.dacd.controller.infrastructure.scraper;

import org.ulpgc.dacd.model.NewsArticle;
import java.util.List;

public interface NewsFeeder {
    List<NewsArticle> feed();
}