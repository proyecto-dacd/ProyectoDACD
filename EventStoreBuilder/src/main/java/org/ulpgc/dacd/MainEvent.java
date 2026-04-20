package org.ulpgc.dacd;

import org.ulpgc.dacd.control.ActiveMQSubscriber;
import org.ulpgc.dacd.control.EventStore;
import org.ulpgc.dacd.control.FileEventStore;

public class MainEvent {
    public static void main(String[] args) {
        EventStore store = new FileEventStore("datalake");

        ActiveMQSubscriber subscriber = new ActiveMQSubscriber(store);

        subscriber.start();
    }
}