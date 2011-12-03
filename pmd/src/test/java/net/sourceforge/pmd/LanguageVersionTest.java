package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LanguageVersionTest {

    private String terseName;
    private LanguageVersion expected;

    public LanguageVersionTest(String terseName, LanguageVersion expected) {
        this.terseName = terseName;
        this.expected = expected;
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] { { "java 1.3", LanguageVersion.JAVA_13 },
                { "java 1.4", LanguageVersion.JAVA_14 }, { "java 1.5", LanguageVersion.JAVA_15 },
                { "java 1.6", LanguageVersion.JAVA_16 }, { "java 1.7", LanguageVersion.JAVA_17 },
                { "jsp", LanguageVersion.JSP }, });
    }

    @Test
    public void testGetLanguageVersionForTerseName() {
        assertEquals(expected, LanguageVersion.findByTerseName(terseName));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LanguageVersionTest.class);
    }
}
