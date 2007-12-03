package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SourceType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;

@RunWith(Parameterized.class)
public class SourceTypeTest {

    private String id;

    private SourceType expected;

    public SourceTypeTest(String id, SourceType expected) {
        this.id = id;
        this.expected = expected;
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] { 
                { "java 1.3", SourceType.JAVA_13 }, 
                { "java 1.4", SourceType.JAVA_14 }, 
                { "java 1.5", SourceType.JAVA_15 }, 
                { "java 1.6", SourceType.JAVA_16 },
                { "java 1.7", SourceType.JAVA_17 },
            });
    }

    @Test
    public void testGetSourceTypeForId() {
        assertEquals(expected, SourceType.getSourceTypeForId(id));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SourceTypeTest.class);
    }
}
