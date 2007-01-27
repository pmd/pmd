package test.net.sourceforge.pmd;

import net.sourceforge.pmd.SourceType;
import junit.framework.TestCase;

public class SourceTypeTest extends TestCase {

    public void testGetSourceTypeForId(){
        assertEquals(SourceType.getSourceTypeForId("java 1.3"), SourceType.JAVA_13);
        assertEquals(SourceType.getSourceTypeForId("java 1.4"), SourceType.JAVA_14);
        assertEquals(SourceType.getSourceTypeForId("java 1.5"), SourceType.JAVA_15);
        assertEquals(SourceType.getSourceTypeForId("java 1.6"), SourceType.JAVA_16);
    }
}
