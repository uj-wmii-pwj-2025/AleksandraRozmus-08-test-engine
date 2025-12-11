package uj.wmii.pwj.anns;

public class TestLogger {
    private final String DEFAULT = "\u001B[0m";
    private final String GREEN = "\u001B[32m";
    private final String RED = "\u001B[31m";
    private final String YELLOW = "\u001B[33m";

    public void printStartInfo(String className) {
        System.out.println("========================================================");
        System.out.println(GREEN + " _____          _                      _            ");
        System.out.println("/__   \\___  ___| |_    ___ _ __   __ _(_)_ __   ___ ");
        System.out.println("  / /\\/ _ \\/ __| __|  / _ \\ '_ \\ / _` | | '_ \\ / _ \\");
        System.out.println(" / / |  __/\\__ \\ |_  |  __/ | | | (_| | | | | |  __/");
        System.out.println(" \\/   \\___||___/\\__|  \\___|_| |_|\\__, |_|_| |_|\\___|");
        System.out.println("                                 |___/              " + DEFAULT);
        System.out.println();
        System.out.printf("Testing class: %s\n", className);
        System.out.println("========================================================");
    }

    public void printTestStartHeader(int current, int total, String testName) {
        final String CYAN = "\u001B[36m";
        System.out.println(CYAN + "\n+-------- Test " + current + "/" + total + ": " + testName + " --------+\n" + DEFAULT);
    }

    public void logTestResult(String label, TestResult result, Object actual, String expected) {
        String info;
        switch (result) {
            case FAIL -> info = "Expected: " + expected + ", Actual: " + actual;
            case ERROR -> info = actual.toString();
            default -> info = "";
        }
        printResult(label, result, info);
    }

    public void printResult(String testName, TestResult result, String info) {
        String color = switch (result) {
            case PASS -> GREEN;
            case FAIL -> RED;
            case ERROR -> YELLOW;
        };

        String output = switch (result) {
            case PASS -> "[PASS] " + testName;
            case FAIL -> "[FAIL] " + testName + "  " + info;
            case ERROR -> "[ERROR] " + testName + "  " + info;
        };
        System.out.println(color + output + DEFAULT);
    }

    public void printSummary(int total, int pass, int fail, int error) {
        System.out.println("\n============ TEST SUMMARY ==============");
        System.out.println("Total: " + total + " | PASS: " + pass + " | FAIL: " + fail + " | ERROR: " + error);
        System.out.println("========================================");
    }
}