/**
 * Test harness executing the black-box (BB) and white-box (WB) test cases
 * documented in the Test Case Report. Uses plain assertions (no external
 * test framework). Each case compares the message the system gives the user
 * against the expected message and prints PASS/FAIL with the actual message.
 */
public class EmployeeManagementSystemTest {

    private static int passCount = 0;
    private static int failCount = 0;

    private static final String ADDED = "Employee added successfully.";
    private static final String REMOVED = "Employee removed successfully.";
    private static final String NOT_FOUND = "Error: Employee not found.";
    private static final String EMPTY_REPORT = "No employees to report.";

    public static void main(String[] args) {
        // ---------- Black-box (functional) test cases ----------
        testBB01_AddValidEmployee();
        testBB02_AddDuplicateId();
        testBB03_AddEmptyId();
        testBB04_AddNameWithDigits();
        testBB05_AddNegativeSalary();
        testBB06_AddNonNumericSalary();
        testBB07_RemoveExistingEmployee();
        testBB08_RemoveUnknownEmployee();
        testBB09_PaymentStandardTax();
        testBB10_PaymentManagementHighTax();
        testBB11_PaymentNegativeHours();
        testBB12_PaymentNonNumericHours();
        testBB13_PaymentUnknownEmployee();
        testBB14_ReportWithEmployees();
        testBB15_ReportWithNoEmployees();

        // ---------- White-box (structural) test cases ----------
        testWB01_AddEmployee_NullId();
        testWB02_AddEmployee_BlankName();
        testWB03_AddEmployee_NamePatternRejects();
        testWB04_AddEmployee_SuccessPathHyphenApostropheName();
        testWB05_AddEmployee_NegativeSalary();
        testWB06_AddEmployee_DuplicateId();
        testWB07_RemoveEmployee_NotFound();
        testWB08_RemoveEmployee_Found();
        testWB09_ProcessPayment_NotFound();
        testWB10_ProcessPayment_NegativeHoursWorked();
        testWB11_ProcessPayment_NegativeOvertime();
        testWB12_ProcessPayment_ManagementBonusTrue();
        testWB13_ProcessPayment_ManagementBonusFalse();
        testWB14_ProcessPayment_HighTaxBoundary5000();
        testWB15_ProcessPayment_StandardTaxJustBelow();
        testWB16_Report_EmptyList();
        testWB17_Report_LoopAndTopEarnerBranches();
        testWB18_ParseNumber_NonNumericText();
        testWB19_ParseNumber_NaNAndInfinityRejected();
        testWB20_TextOverloads_ValidNumbers();
        testWB21_ParseNumber_NullText();
        testWB22_ProcessPaymentText_OnlyOvertimeInvalid();

        System.out.println("\n==============================");
        System.out.printf("TOTAL: %d passed, %d failed%n", passCount, failCount);
        System.out.println("==============================");
    }

    // ---------------- helpers ----------------

    private static void expect(String testId, String expected, String actual) {
        if (expected.equals(actual)) {
            passCount++;
            System.out.printf("[PASS] %s -> \"%s\"%n", testId, actual);
        } else {
            failCount++;
            System.out.printf("[FAIL] %s -> expected \"%s\" but got \"%s\"%n", testId, expected, actual);
        }
    }

    private static void expectTrue(String testId, boolean condition, String detail) {
        if (condition) {
            passCount++;
            System.out.printf("[PASS] %s -> %s%n", testId, detail);
        } else {
            failCount++;
            System.out.printf("[FAIL] %s -> %s%n", testId, detail);
        }
    }

    // ---------------- Black-box test cases ----------------

    private static void testBB01_AddValidEmployee() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-01", ADDED, sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000));
    }

    private static void testBB02_AddDuplicateId() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        expect("BB-02", "Error: Employee ID already exists.", sys.addEmployee("E001", "Someone Else", "Sales", 2000));
    }

    private static void testBB03_AddEmptyId() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-03", "Error: Employee ID cannot be empty.", sys.addEmployee("", "Alice Uwase", "Engineering", 3000));
    }

    private static void testBB04_AddNameWithDigits() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-04", "Error: Employee name must contain letters only.", sys.addEmployee("E001", "Alice123", "Engineering", 3000));
    }

    private static void testBB05_AddNegativeSalary() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-05", "Error: Base salary cannot be negative.", sys.addEmployee("E001", "Alice Uwase", "Engineering", -100));
    }

    private static void testBB06_AddNonNumericSalary() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-06", "Error: Base salary must be a valid number.", sys.addEmployee("E001", "Alice Uwase", "Engineering", "abc"));
    }

    private static void testBB07_RemoveExistingEmployee() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        expect("BB-07", REMOVED, sys.removeEmployee("E001"));
    }

    private static void testBB08_RemoveUnknownEmployee() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-08", NOT_FOUND, sys.removeEmployee("E999"));
    }

    private static void testBB09_PaymentStandardTax() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        // gross 3000, 15% tax
        expect("BB-09", "Payment processed successfully. Net pay: 2550.00", sys.processPayment("E001", 160, 0));
    }

    private static void testBB10_PaymentManagementHighTax() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E002", "Jean Baptiste", "Management", 6000);
        // gross 6000 + 10% bonus = 6600, 25% tax
        expect("BB-10", "Payment processed successfully. Net pay: 4950.00", sys.processPayment("E002", 160, 0));
    }

    private static void testBB11_PaymentNegativeHours() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        expect("BB-11", "Error: Hours cannot be negative.", sys.processPayment("E001", -10, 0));
    }

    private static void testBB12_PaymentNonNumericHours() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        expect("BB-12", "Error: Hours must be valid numbers.", sys.processPayment("E001", "abc", "0"));
    }

    private static void testBB13_PaymentUnknownEmployee() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-13", NOT_FOUND, sys.processPayment("E999", 160, 0));
    }

    private static void testBB14_ReportWithEmployees() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice Uwase", "Engineering", 3000);
        sys.processPayment("E001", 160, 0);
        String report = sys.generatePayrollReport();
        expectTrue("BB-14", report.contains("E001 | Alice Uwase | Engineering | Net Pay: 2550.00")
                && report.contains("Total Payroll: 2550.00")
                && report.contains("Top Earner: Alice Uwase (2550.00)"),
                "report lists the employee, total payroll and top earner");
    }

    private static void testBB15_ReportWithNoEmployees() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("BB-15", EMPTY_REPORT, sys.generatePayrollReport());
    }

    // ---------------- White-box test cases ----------------

    private static void testWB01_AddEmployee_NullId() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-01", "Error: Employee ID cannot be empty.", sys.addEmployee(null, "Alice", "Engineering", 3000));
    }

    private static void testWB02_AddEmployee_BlankName() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-02", "Error: Employee name cannot be empty.", sys.addEmployee("E001", "  ", "Engineering", 3000));
    }

    private static void testWB03_AddEmployee_NamePatternRejects() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-03", "Error: Employee name must contain letters only.", sys.addEmployee("E001", "Bob2", "Engineering", 3000));
    }

    private static void testWB04_AddEmployee_SuccessPathHyphenApostropheName() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-04", ADDED, sys.addEmployee("E001", "Jean-Luc O'Brien", "Engineering", 3000));
    }

    private static void testWB05_AddEmployee_NegativeSalary() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-05", "Error: Base salary cannot be negative.", sys.addEmployee("E001", "Alice", "Engineering", -1));
    }

    private static void testWB06_AddEmployee_DuplicateId() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-06", "Error: Employee ID already exists.", sys.addEmployee("E001", "Bob", "Sales", 2000));
    }

    private static void testWB07_RemoveEmployee_NotFound() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-07", NOT_FOUND, sys.removeEmployee("E999"));
    }

    private static void testWB08_RemoveEmployee_Found() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-08", REMOVED, sys.removeEmployee("E001"));
    }

    private static void testWB09_ProcessPayment_NotFound() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-09", NOT_FOUND, sys.processPayment("E999", 160, 0));
    }

    private static void testWB10_ProcessPayment_NegativeHoursWorked() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-10", "Error: Hours cannot be negative.", sys.processPayment("E001", -1, 0));
    }

    private static void testWB11_ProcessPayment_NegativeOvertime() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-11", "Error: Hours cannot be negative.", sys.processPayment("E001", 160, -5));
    }

    private static void testWB12_ProcessPayment_ManagementBonusTrue() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E002", "Jean Baptiste", "Management", 1000);
        // gross 1000 + 10% = 1100, 15% tax
        expect("WB-12", "Payment processed successfully. Net pay: 935.00", sys.processPayment("E002", 160, 0));
    }

    private static void testWB13_ProcessPayment_ManagementBonusFalse() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E003", "Marie Claire", "Sales", 1000);
        // gross 1000, 15% tax
        expect("WB-13", "Payment processed successfully. Net pay: 850.00", sys.processPayment("E003", 160, 0));
    }

    private static void testWB14_ProcessPayment_HighTaxBoundary5000() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E004", "Boundary Case", "Sales", 5000);
        // gross exactly 5000 is >= threshold, 25% tax
        expect("WB-14", "Payment processed successfully. Net pay: 3750.00", sys.processPayment("E004", 160, 0));
    }

    private static void testWB15_ProcessPayment_StandardTaxJustBelow() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E005", "Below Threshold", "Sales", 4999);
        // gross 4999 is < threshold, 15% tax
        expect("WB-15", "Payment processed successfully. Net pay: 4249.15", sys.processPayment("E005", 160, 0));
    }

    private static void testWB16_Report_EmptyList() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-16", EMPTY_REPORT, sys.generatePayrollReport());
    }

    private static void testWB17_Report_LoopAndTopEarnerBranches() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        sys.addEmployee("E002", "Jean", "Management", 6000);
        sys.addEmployee("E003", "Marie", "Sales", 2500);
        sys.processPayment("E001", 160, 10);
        sys.processPayment("E002", 160, 5);
        sys.processPayment("E003", 160, 0);
        String report = sys.generatePayrollReport();
        // nets 2789.06, 5182.03, 2125.00: the (net > highest) test is true, true, false
        expectTrue("WB-17", report.contains("Alice") && report.contains("Jean") && report.contains("Marie")
                && report.contains("Total Payroll: 10096.09")
                && report.contains("Top Earner: Jean (5182.03)"),
                "loop ran 3 iterations; top earner Jean (5182.03)");
    }

    private static void testWB18_ParseNumber_NonNumericText() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-18", "Error: Base salary must be a valid number.", sys.addEmployee("E001", "Alice", "Engineering", "12abc"));
    }

    private static void testWB19_ParseNumber_NaNAndInfinityRejected() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-19", "Error: Hours must be valid numbers.", sys.processPayment("E001", "NaN", "Infinity"));
    }

    private static void testWB21_ParseNumber_NullText() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        expect("WB-21", "Error: Base salary must be a valid number.", sys.addEmployee("E001", "Alice", "Engineering", (String) null));
    }

    private static void testWB22_ProcessPaymentText_OnlyOvertimeInvalid() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        sys.addEmployee("E001", "Alice", "Engineering", 3000);
        expect("WB-22", "Error: Hours must be valid numbers.", sys.processPayment("E001", "160", "abc"));
    }

    private static void testWB20_TextOverloads_ValidNumbers() {
        EmployeeManagementSystem sys = new EmployeeManagementSystem();
        String added = sys.addEmployee("E001", "Alice", "Engineering", " 3000 ");
        String paid = sys.processPayment("E001", "160", "0");
        expectTrue("WB-20", added.equals(ADDED) && paid.equals("Payment processed successfully. Net pay: 2550.00"),
                "text overloads parse valid numbers (\"" + added + "\" / \"" + paid + "\")");
    }
}
