package de.heaal.eaf.unittest;
import org.junit.runner.JUnitCore;

public class TestRunner {
    public static void main(String[] args) {
        org.junit.runner.Result result = JUnitCore.runClasses(GeneticAlgorithmTest.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            System.out.println("Some tests failed!");
            System.out.println(result.getFailures());
        }

    }
}
