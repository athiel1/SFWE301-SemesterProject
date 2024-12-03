package Inventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class StockForecasting {
    private Map<Integer, List<Integer>> usageHistory = new HashMap<>();
    private Map<Integer, Double> averageMonthlyUsage = new HashMap<>();
    private Map<Integer, Double> seasonalityFactors = new HashMap<>();
    private static final int MONTHS_TO_FORECAST = 3;
    private static final double SAFETY_STOCK_FACTOR = 1.5;
    
    public void loadUsageHistory() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                StockForecasting.class.getResourceAsStream("/Inventory/UsageHistory.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int itemId = Integer.parseInt(values[0]);
                List<Integer> monthlyUsage = new ArrayList<>();
                
                // Skip item ID and name, start from index 2
                for (int i = 2; i < values.length; i++) {
                    monthlyUsage.add(Integer.parseInt(values[i].trim()));
                }
                
                usageHistory.put(itemId, monthlyUsage);
                calculateAverageUsage(itemId, monthlyUsage);
                calculateSeasonality(itemId, monthlyUsage);
            }
        } catch (IOException e) {
            System.out.println("Error reading usage history: " + e.getMessage());
        }
    }
    
    private void calculateAverageUsage(int itemId, List<Integer> usage) {
        double average = usage.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        averageMonthlyUsage.put(itemId, average);
    }
    
    private void calculateSeasonality(int itemId, List<Integer> usage) {
        if (usage.size() >= 12) {
            double average = averageMonthlyUsage.getOrDefault(itemId, 1.0);
            double lastMonthUsage = usage.get(usage.size() - 1);
            double seasonalityFactor = (lastMonthUsage > 0) ? lastMonthUsage / average : 1.0; // Default to 1.0
            seasonalityFactors.put(itemId, seasonalityFactor);
        } else {
            seasonalityFactors.put(itemId, 1.0); // Default seasonality factor
        }
    }

    
    public ForecastResult generateForecast(int itemId, int currentStock, int lowStockThreshold) {
        if (!usageHistory.containsKey(itemId)) {
            return new ForecastResult(currentStock, 0, false, "No historical data available");
        }

        // Retrieve average usage and seasonality
        double avgUsage = averageMonthlyUsage.getOrDefault(itemId, 0.0);
        double seasonality = seasonalityFactors.getOrDefault(itemId, 1.0);
        double forecastedMonthlyUsage = avgUsage * seasonality;

        // Handle cases where forecasted usage is zero or invalid
        if (forecastedMonthlyUsage <= 0) {
            return new ForecastResult(currentStock, 0, false, 
                "Forecast unavailable due to insufficient or inconsistent usage data.");
        }

        // Calculate how many months current stock will last
        double monthsUntilDepletion = currentStock / forecastedMonthlyUsage;

        // Calculate projected stock after the forecast period
        double projectedStock = currentStock - (forecastedMonthlyUsage * MONTHS_TO_FORECAST);

        // Initialize variables for recommendation
        double recommendedReorder = 0;
        boolean willReachLowStock = false;
        String recommendation;

        // Determine if stock will run below the threshold
        if (projectedStock < lowStockThreshold) {
            willReachLowStock = true;
            // Calculate recommended reorder amount
            recommendedReorder = (lowStockThreshold + (avgUsage * SAFETY_STOCK_FACTOR)) - projectedStock;

            // Generate specific recommendation message
            recommendation = String.format(
                "Stock will reach below threshold in %.1f months. Recommend ordering %.0f units.",
                monthsUntilDepletion, recommendedReorder);
        } else {
            recommendation = "Stock levels sufficient for the forecast period.";
        }

        return new ForecastResult(projectedStock, recommendedReorder, willReachLowStock, recommendation);
    }


    public void displayForecastForAllItems(InventoryService inventoryService) {
        System.out.println("\n=== Stock Forecast Report ===");
        System.out.printf("%-5s %-20s %-12s %-15s %-15s %-40s%n",
                "ID", "Name", "Current", "Projected", "Reorder Qty", "Recommendation");
        System.out.println("-".repeat(110));

        for (InventoryItem item : inventoryService.getAllItems()) {
            ForecastResult forecast = generateForecast(item.getId(), item.getQuantity(), 120);
            System.out.printf("%-5d %-20s %-12d %-15.0f %-15.0f %-40s%n",
                    item.getId(),
                    item.getName(),
                    item.getQuantity(),
                    forecast.projectedStock,
                    forecast.recommendedReorder,
                    forecast.recommendation);
        }
    }

class ForecastResult {
    public final double projectedStock;
    public final double recommendedReorder;
    public final boolean willReachLowStock;
    public final String recommendation;
    
    public ForecastResult(double projectedStock, double recommendedReorder, 
                         boolean willReachLowStock, String recommendation) {
        this.projectedStock = projectedStock;
        this.recommendedReorder = recommendedReorder;
        this.willReachLowStock = willReachLowStock;
        this.recommendation = recommendation;
    }
}
}
