package org.ulpgc.dacd;

import org.ulpgc.dacd.control.ActiveMQSubscriber;
import org.ulpgc.dacd.control.EventStore;
import org.ulpgc.dacd.control.FileEventStore;

public class MainEvent {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("ERROR: Falta la ruta del directorio.");
            System.err.println("Uso: Debes pasar la ruta donde se guardarán los eventos como argumento.");
            return;
        }

        String basePath = args[0];

        EventStore store = new FileEventStore(basePath);

        ActiveMQSubscriber subscriber = new ActiveMQSubscriber(store);

        subscriber.start();
    }
}