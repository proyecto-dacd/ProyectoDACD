package org.ulpgc.dacd.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class DashboardView extends JFrame {
    private DefaultTableModel tableModel;
    private JTextPane alertFeed;

    public DashboardView() {
        setTitle("Crypto Market Analyzer - Dashboard");
        setSize(1100, 600); // Tamaño ideal para los dos paneles
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initMarketPanel();
        initAlertPanel();
    }

    private void initMarketPanel() {
        String[] columnNames = {"Criptomoneda", "Precio (€)", "Última Actualización"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable marketTable = new JTable(tableModel);
        marketTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(marketTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📊 Mercado en Vivo"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initAlertPanel() {
        alertFeed = new JTextPane();
        alertFeed.setEditable(false);
        alertFeed.setBackground(new Color(30, 30, 30));

        JScrollPane scrollPane = new JScrollPane(alertFeed);
        scrollPane.setPreferredSize(new Dimension(450, 0)); // Ancho ideal para leer las noticias
        scrollPane.setBorder(BorderFactory.createTitledBorder("🚨 Historial de Alertas"));
        add(scrollPane, BorderLayout.EAST);
    }

    // MÉTODOS DE ACTUALIZACIÓN

    public void updatePrice(String id, double price, String timestamp) {
        SwingUtilities.invokeLater(() -> {
            boolean found = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(id.toUpperCase())) {
                    tableModel.setValueAt(price, i, 1);
                    tableModel.setValueAt(timestamp, i, 2);
                    found = true;
                    break;
                }
            }
            if (!found) tableModel.addRow(new Object[]{id.toUpperCase(), price, timestamp});
        });
    }

    public void addAlert(String title, String body, boolean isPositive) {
        SwingUtilities.invokeLater(() -> {
            Color titleColor = isPositive ? new Color(100, 255, 100) : new Color(255, 100, 100);

            appendToPane(title + "\n", titleColor);

            Color bodyColor = new Color(200, 200, 200);

            appendToPane(body + "\n", bodyColor);
        });
    }

    private void appendToPane(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Monospaced");
        aset = sc.addAttribute(aset, StyleConstants.FontSize, 12);
        aset = sc.addAttribute(aset, StyleConstants.Bold, true);

        try {
            int len = alertFeed.getDocument().getLength();
            alertFeed.getDocument().insertString(len, msg, aset);
            alertFeed.setCaretPosition(alertFeed.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}