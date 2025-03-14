/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.testdata;

/**
 * Test data for the metrics visitor
 *
 * @author Clément Fournier
 */
public class MetricsVisitorTestData {

    public String x;
    private String y;
    protected String z;
    String t;

    public MetricsVisitorTestData() {

    }

    private MetricsVisitorTestData(String x) {

    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public void setX(String n) {
        x = n;
    }

    public void setY(String n) {
        y = n;
    }


    public static class NestedClass {

        public NestedClass() {

        }

        public void nestedMethod1() {

        }
    }

    public void mymethod1() {

    }

    private void mymethod2() {

    }

    protected static void mystatic1() {

    }

    private static void mystatic2(String k) {

    }

    private static void mystatic2(String k, String l) {

    }

}
