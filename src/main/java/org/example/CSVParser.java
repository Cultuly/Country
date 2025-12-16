package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CSVParser {
    public static class ParseResult {
        private List<Country> countries;
        private List<Region> regions;
        private List<Subregion> subregions;

        public ParseResult(List<Country> countries, List<Region> regions, List<Subregion> subregions) {
            this.countries = countries;
            this.regions = regions;
            this.subregions = subregions;
        }

        public List<Country> getCountries() { return countries; }
        public List<Region> getRegions() { return regions; }
        public List<Subregion> getSubregions() { return subregions; }
    }

    public static ParseResult parseCSV(String filePath) {
        List<Country> countries = new ArrayList<>();
        List<Region> regions = new ArrayList<>();
        List<Subregion> subregions = new ArrayList<>();

        Map<String, Integer> regionMap = new HashMap<>();
        Map<String, Integer> subregionMap = new HashMap<>();

        int regionId = 1;
        int subregionId = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = parseCSVLine(line);
                if (values.length < 5) continue;

                String country = cleanupValue(values[0]);
                String subregion = cleanupValue(values[1]);
                String region = cleanupValue(values[2]);
                String internetUsersStr = cleanupValue(values[3]);
                String populationStr = values.length > 4 ? cleanupValue(values[4]) : "0";

                long internetUsers = parseLongSafe(internetUsersStr);
                long population = parseLongSafe(populationStr);

                int regionIdVal;
                if (!regionMap.containsKey(region)) {
                    regionMap.put(region, regionId);
                    regions.add(new Region(regionId, region));
                    regionIdVal = regionId;
                    regionId++;
                } else {
                    regionIdVal = regionMap.get(region);
                }

                int subregionIdVal;
                if (!subregionMap.containsKey(subregion)) {
                    subregionMap.put(subregion, subregionId);
                    subregions.add(new Subregion(subregionId, subregion, regionIdVal));
                    subregionIdVal = subregionId;
                    subregionId++;
                } else {
                    subregionIdVal = subregionMap.get(subregion);
                }

                countries.add(new Country(country, subregionIdVal, internetUsers, population));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ParseResult(countries, regions, subregions);
    }

    private static String cleanupValue(String value) {
        return value.replace("\"", "").trim();
    }

    private static long parseLongSafe(String value) {
        String cleaned = value.replace(",", "");
        try {
            return cleaned.isEmpty() ? 0 : Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());

        return values.toArray(new String[0]);
    }
}