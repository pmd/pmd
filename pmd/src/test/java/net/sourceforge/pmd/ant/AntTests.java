/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Tests for the net.sourceforge.pmd.ant package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 */
@RunWith(Suite.class)
@SuiteClasses({FormatterTest.class, PMDTaskTest.class})
public class AntTests {
}

