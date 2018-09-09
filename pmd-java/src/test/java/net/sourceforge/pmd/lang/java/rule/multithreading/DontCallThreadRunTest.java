/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

public class DontCallThreadRunTest extends MultithreadingRulesTest {
    // Used by DontCallThreadRun test cases
    public static class TestThread extends Thread {
        @Override
        public void run() {
            System.out.println("test");
        }
    }
}
