import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;


public class EmployeeManagementSystem {

    // Business rules (kept as named constants so test design can target them directly)
    private static final double STANDARD_MONTHLY_HOURS = 160.0;
    private static final double OVERTIME_RATE_MULTIPLIER = 1.5;
    private static final double MANAGEMENT_BONUS_RATE = 0.10;
    private static final double HIGH_INCOME_THRESHOLD = 5000.0;
    private static final double HIGH_INCOME_TAX_RATE = 0.25;
    private static final double STANDARD_TAX_RATE = 0.15;

    // Letters, spaces, apostrophes and hyphens only (e.g. "Jean-Luc", "O'Brien") — no digits
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z'-]*(?: [A-Za-z][A-Za-z'-]*)*$");

    // Parallel-array employee store (kept simple: one class, no helper types)
    private final List<String> employeeIds = new ArrayList<>();
    private final List<String> employeeNames = new ArrayList<>();
    private final List<String> departments = new ArrayList<>();
    private final List<Double> baseSalaries = new ArrayList<>();
    private final List<Double> netPayments = new ArrayList<>();

    // Every public operation reports its outcome as a message for the user; none throws.

    public String addEmployee(String id, String name, String department, double baseSalary) {
        if (id == null || id.trim().isEmpty()) {
            return "Error: Employee ID cannot be empty.";
        }
        if (name == null || name.trim().isEmpty()) {
            return "Error: Employee name cannot be empty.";
        }
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            return "Error: Employee name must contain letters only.";
        }
        if (baseSalary < 0) {
            return "Error: Base salary cannot be negative.";
        }
        if (employeeIds.contains(id)) {
            return "Error: Employee ID already exists.";
        }
        employeeIds.add(id);
        employeeNames.add(name);
        departments.add(department);
        baseSalaries.add(baseSalary);
        netPayments.add(0.0);
        return "Employee added successfully.";
    }

    public String addEmployee(String id, String name, String department, String baseSalaryText) {
        Double baseSalary = parseNumber(baseSalaryText);
        if (baseSalary == null) {
            return "Error: Base salary must be a valid number.";
        }
        return addEmployee(id, name, department, baseSalary.doubleValue());
    }

    public String removeEmployee(String id) {
        int index = employeeIds.indexOf(id);
        if (index == -1) {
            return "Error: Employee not found.";
        }
        employeeIds.remove(index);
        employeeNames.remove(index);
        departments.remove(index);
        baseSalaries.remove(index);
        netPayments.remove(index);
        return "Employee removed successfully.";
    }

    public String processPayment(String id, double hoursWorked, double overtimeHours) {
        int index = employeeIds.indexOf(id);
        if (index == -1) {
            return "Error: Employee not found.";
        }
        if (hoursWorked < 0 || overtimeHours < 0) {
            return "Error: Hours cannot be negative.";
        }

        double baseSalary = baseSalaries.get(index);
        double hourlyRate = baseSalary / STANDARD_MONTHLY_HOURS;
        double overtimePay = overtimeHours * hourlyRate * OVERTIME_RATE_MULTIPLIER;
        double grossPay = baseSalary + overtimePay;

        if ("Management".equalsIgnoreCase(departments.get(index))) {
            grossPay += grossPay * MANAGEMENT_BONUS_RATE;
        }

        double taxRate = (grossPay >= HIGH_INCOME_THRESHOLD) ? HIGH_INCOME_TAX_RATE : STANDARD_TAX_RATE;
        double netPay = grossPay - (grossPay * taxRate);

        netPayments.set(index, netPay);
        return String.format(Locale.US, "Payment processed successfully. Net pay: %.2f", netPay);
    }

    public String processPayment(String id, String hoursWorkedText, String overtimeHoursText) {
        Double hoursWorked = parseNumber(hoursWorkedText);
        Double overtimeHours = parseNumber(overtimeHoursText);
        if (hoursWorked == null || overtimeHours == null) {
            return "Error: Hours must be valid numbers.";
        }
        return processPayment(id, hoursWorked.doubleValue(), overtimeHours.doubleValue());
    }

    public String generatePayrollReport() {
        if (employeeIds.isEmpty()) {
            return "No employees to report.";
        }

        StringBuilder report = new StringBuilder();
        double total = 0;
        double highest = -1;
        String topEarner = "";

        report.append("===== PAYROLL REPORT =====\n");
        for (int i = 0; i < employeeIds.size(); i++) {
            double net = netPayments.get(i);
            report.append(String.format(Locale.US, "%s | %s | %s | Net Pay: %.2f%n",
                    employeeIds.get(i), employeeNames.get(i), departments.get(i), net));
            total += net;
            if (net > highest) {
                highest = net;
                topEarner = employeeNames.get(i);
            }
        }

        double average = total / employeeIds.size();
        report.append(String.format(Locale.US, "Total Payroll: %.2f%n", total));
        report.append(String.format(Locale.US, "Average Net Pay: %.2f%n", average));
        report.append(String.format(Locale.US, "Top Earner: %s (%.2f)%n", topEarner, highest));
        return report.toString();
    }

    public int getEmployeeCount() {
        return employeeIds.size();
    }

    // Returns -1 when the employee does not exist
    public double getNetPay(String id) {
        int index = employeeIds.indexOf(id);
        if (index == -1) {
            return -1;
        }
        return netPayments.get(index);
    }

    public boolean containsEmployee(String id) {
        return employeeIds.contains(id);
    }

    // Returns null for text that is not a finite number
    private static Double parseNumber(String text) {
        if (text == null) {
            return null;
        }
        try {
            double value = Double.parseDouble(text.trim());
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        EmployeeManagementSystem system = new EmployeeManagementSystem();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.println("\n--- Organization X: Employee Management System ---");
            System.out.println("1. Add employee");
            System.out.println("2. Remove employee");
            System.out.println("3. Process payment");
            System.out.println("4. Generate payroll report");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Department: ");
                    String dept = scanner.nextLine();
                    System.out.print("Base salary: ");
                    String salary = scanner.nextLine();
                    System.out.println(system.addEmployee(id, name, dept, salary));
                    break;
                case "2":
                    System.out.print("ID to remove: ");
                    System.out.println(system.removeEmployee(scanner.nextLine()));
                    break;
                case "3":
                    System.out.print("ID: ");
                    String payId = scanner.nextLine();
                    System.out.print("Hours worked: ");
                    String hours = scanner.nextLine();
                    System.out.print("Overtime hours: ");
                    String overtime = scanner.nextLine();
                    System.out.println(system.processPayment(payId, hours, overtime));
                    break;
                case "4":
                    System.out.println(system.generatePayrollReport());
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}
