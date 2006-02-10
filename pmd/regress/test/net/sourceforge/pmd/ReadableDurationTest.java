package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;

public class ReadableDurationTest extends TestCase {

    public void testMillisSeconds() {
        assertEquals("0s", new Report.ReadableDuration(35).getTime());
    }

    public void testSeconds() {
        assertEquals("25s", new Report.ReadableDuration(25 * 1000).getTime());
    }

    public void testWholeMinutes() {
        assertEquals("5m 0s", new Report.ReadableDuration(60 * 1000 * 5).getTime());
    }

    public void testMinutesAndSeconds() {
        assertEquals("5m 10s", new Report.ReadableDuration((60 * 1000 * 5) + (1000 * 10)).getTime());
    }

    public void testHours() {
        assertEquals("2h 0m 0s", new Report.ReadableDuration(60 * 1000 * 120).getTime());
    }
}
