/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.pmd.build.filefilter.RulesetFilenameFilter;

import org.junit.Test;

public class RulesetFilenameFilterTest {

    @Test
    public void testAccept() {
	RulesetFilenameFilter filter = new RulesetFilenameFilter();
	File directory = new File(".");

	assertTrue(filter.accept(directory, "codesize.xml"));
	
	assertFalse(filter.accept(directory, "some-text-file.txt"));
	assertFalse(filter.accept(directory, "all-java.xml"));
	assertFalse(filter.accept(directory, "dogfood.xml"));
    }

}
