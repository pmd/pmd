package net.sourceforge.pmd.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.lang.ParserOptions;

import org.junit.Test;

public class ParserOptionsTest {

    @Test
    public void testSuppressMarker() throws Exception {
	ParserOptions parserOptions = new ParserOptions();
	assertNull(parserOptions.getSuppressMarker());
	parserOptions.setSuppressMarker("foo");
	assertEquals("foo", parserOptions.getSuppressMarker());
    }

    @Test
    public void testEqualsHashcode() throws Exception {
	ParserOptions options1 = new ParserOptions();
	options1.setSuppressMarker("foo");
	ParserOptions options2 = new ParserOptions();
	options2.setSuppressMarker("bar");
	ParserOptions options3 = new ParserOptions();
	options3.setSuppressMarker("foo");
	ParserOptions options4 = new ParserOptions();
	options4.setSuppressMarker("bar");
	verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    // 1 and 3 are equals, as are 2 and 4.
    @SuppressWarnings("PMD.UseAssertSameInsteadOfAssertTrue")
    public static void verifyOptionsEqualsHashcode(ParserOptions options1, ParserOptions options2,
	    ParserOptions options3, ParserOptions options4) {
	// Objects should be different
	assertNotSame(options1, options2);
	assertNotSame(options1, options2);
	assertNotSame(options1, options3);
	assertNotSame(options2, options3);
	assertNotSame(options2, options4);
	assertNotSame(options3, options4);

	// Check all 16 equality combinations
	assertEquals(options1, options1);
	assertFalse(options1.equals(options2));
	assertEquals(options1, options3);
	assertFalse(options1.equals(options4));

	assertFalse(options2.equals(options1));
	assertEquals(options2, options2);
	assertFalse(options2.equals(options3));
	assertEquals(options2, options4);

	assertEquals(options3, options1);
	assertFalse(options3.equals(options2));
	assertEquals(options3, options3);
	assertFalse(options3.equals(options4));

	assertFalse(options4.equals(options1));
	assertEquals(options4, options2);
	assertFalse(options4.equals(options3));
	assertEquals(options4, options4);

	// Hashcodes should match up
	assertFalse(options1.hashCode() == options2.hashCode());
	assertTrue(options1.hashCode() == options3.hashCode());
	assertFalse(options1.hashCode() == options4.hashCode());
	assertFalse(options2.hashCode() == options3.hashCode());
	assertTrue(options2.hashCode() == options4.hashCode());
	assertFalse(options3.hashCode() == options4.hashCode());
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(ParserOptionsTest.class);
    }
}
