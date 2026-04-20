package org.ulpgc.dacd.control;

public interface EventStore {
    void save(String topic, String eventJson);
}