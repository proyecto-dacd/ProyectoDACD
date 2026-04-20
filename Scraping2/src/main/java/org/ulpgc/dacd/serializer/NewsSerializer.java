package org.ulpgc.dacd.serializer;

import org.ulpgc.dacd.model.NewsArticle;

public interface NewsSerializer {
    void serialize(NewsArticle article);
}