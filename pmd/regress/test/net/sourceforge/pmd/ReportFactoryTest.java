/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:27:42 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.reports.ReportFactory;

public class ReportFactoryTest extends TestCase {
    public ReportFactoryTest(String name) {
        super(name);
    }

    public void testXML() {
        ReportFactory rf = new ReportFactory();
        rf.createReport("xml");
    }

    public void testHTML() {
        ReportFactory rf = new ReportFactory();
        rf.createReport("html");
    }

    public void testUnknownType() {
        ReportFactory rf = new ReportFactory();
        try {
            rf.createReport("foo");
            throw new RuntimeException("Should have thrown a RuntimeException");
        } catch (Exception e) {
            // cool
        }
    }
}
