package org.ulpgc.dacd;

import org.ulpgc.dacd.model.NewsArticle;

public interface NewsSerializer {
    void serialize(NewsArticle article);
}