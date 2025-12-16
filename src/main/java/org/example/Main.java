package org.example;


public class Main {
    public static void main(String[] args) {
        String csvPath = "src/main/java/org/example/Country.csv";
        String dbPath = "countries.db";

        System.out.println("Начало обработки данных...");

        CSVParser.ParseResult result = CSVParser.parseCSV(csvPath);
        System.out.println("Данные успешно распарсены. Всего стран: " + result.getCountries().size());

        DatabaseManager dbManager = new DatabaseManager(dbPath);
        dbManager.insertData(result.getRegions(), result.getSubregions(), result.getCountries());
        System.out.println("Данные успешно сохранены в базу данных.");

        dbManager.executeQueries();

        ChartGenerator.generateChart(dbManager.getConnection());

        dbManager.close();
        System.out.println("Работа программы завершена.");
    }
}