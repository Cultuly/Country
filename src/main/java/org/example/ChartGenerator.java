package org.example;

import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class ChartGenerator {
    public static void generateChart(Connection connection) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            String query = "SELECT s.name AS subregion, "
                    + "(SUM(c.internet_users) * 100.0 / SUM(c.population)) AS percentage "
                    + "FROM Country c "
                    + "JOIN Subregion s ON c.subregion_id = s.id "
                    + "WHERE c.population > 0 AND c.internet_users > 0 "
                    + "GROUP BY s.name "
                    + "ORDER BY percentage DESC "
                    + "LIMIT 10";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    String subregion = rs.getString("subregion");
                    double percentage = rs.getDouble("percentage");
                    dataset.addValue(percentage, "Процент", subregion);
                }
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Процентное соотношение пользователей в интернете по субрегионам",
                    "Субрегион",
                    "Процент пользователей (%)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            ChartFrame frame = new ChartFrame("Статистика по интернет-пользователям", chart);
            frame.pack();
            frame.setSize(900, 600);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(ChartFrame.DISPOSE_ON_CLOSE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}