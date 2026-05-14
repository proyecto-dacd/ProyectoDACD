package org.ulpgc.dacd.model.logic;

import java.util.List;

public class PearsonCalculator {

    public static double calculate(List<Double> precios, List<Double> sentimientos) {
        if (precios == null || sentimientos == null || precios.size() != sentimientos.size() || precios.size() < 2) {
            return 0.0;
        }

        int n = precios.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            double x = precios.get(i);
            double y = sentimientos.get(i);
            sumX += x; sumY += y; sumXY += (x * y);
            sumX2 += (x * x); sumY2 += (y * y);
        }

        double numerador = (n * sumXY) - (sumX * sumY);
        double denominador = Math.sqrt(((n * sumX2) - (sumX * sumX)) * ((n * sumY2) - (sumY * sumY)));

        return denominador == 0 ? 0 : numerador / denominador;
    }
}