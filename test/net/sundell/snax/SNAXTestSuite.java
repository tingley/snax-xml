package net.sundell.snax;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestParser.class
})
public class SNAXTestSuite {
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("net.sundell.snax.SNAXTestSuite");
    }
}
