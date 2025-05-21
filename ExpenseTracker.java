import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseTracker {
    static class Transaction {
        String type; // Income or Expense
        String category;
        double amount;
        LocalDate date;

        public Transaction(String type, String category, double amount, LocalDate date) {
            this.type = type;
            this.category = category;
            this.amount = amount;
            this.date = date;
        }

        public String toFileFormat() {
            return type + "," + category + "," + amount + "," + date;
        }
    }

    private static final List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Load Transactions from File");
            System.out.println("5. Save Transactions to File");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addTransaction("Income");
                case 2 -> addTransaction("Expense");
                case 3 -> viewMonthlySummary();
                case 4 -> loadFromFile();
                case 5 -> saveToFile();
                case 6 -> {
                    System.out.println("Exiting Expense Tracker.");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addTransaction(String type) {
        System.out.print("Enter category (" + (type.equals("Income") ? "Salary/Business" : "Food/Rent/Travel") + "): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), formatter);

        transactions.add(new Transaction(type, category, amount, date));
        System.out.println(type + " added successfully.");
    }

    private static void viewMonthlySummary() {
        System.out.print("Enter month and year to view summary (yyyy-MM): ");
        String input = scanner.nextLine();
        String[] parts = input.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> incomeCategories = new HashMap<>();
        Map<String, Double> expenseCategories = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                if (t.type.equals("Income")) {
                    totalIncome += t.amount;
                    incomeCategories.put(t.category, incomeCategories.getOrDefault(t.category, 0.0) + t.amount);
                } else {
                    totalExpense += t.amount;
                    expenseCategories.put(t.category, expenseCategories.getOrDefault(t.category, 0.0) + t.amount);
                }
            }
        }

        System.out.println("\nSummary for " + year + "-" + String.format("%02d", month));
        System.out.println("Total Income: " + totalIncome);
        for (String cat : incomeCategories.keySet()) {
            System.out.println("  " + cat + ": " + incomeCategories.get(cat));
        }
        System.out.println("Total Expense: " + totalExpense);
        for (String cat : expenseCategories.keySet()) {
            System.out.println("  " + cat + ": " + expenseCategories.get(cat));
        }
        System.out.println("Net Savings: " + (totalIncome - totalExpense));
    }

    private static void loadFromFile() {
        System.out.print("Enter file path to load: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4)
                    continue;
                String type = parts[0];
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                LocalDate date = LocalDate.parse(parts[3], formatter);

                transactions.add(new Transaction(type, category, amount, date));
                count++;
            }
            System.out.println(count + " transactions loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private static void saveToFile() {
        System.out.print("Enter file path to save: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Transaction t : transactions) {
                bw.write(t.toFileFormat());
                bw.newLine();
            }
            System.out.println("Transactions saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
