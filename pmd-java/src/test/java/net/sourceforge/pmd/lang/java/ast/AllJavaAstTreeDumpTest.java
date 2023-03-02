/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ParserCornersTest.class,
    Java15TreeDumpTest.class,
    Java16TreeDumpTest.class,
    Java17TreeDumpTest.class,
    Java19PreviewTreeDumpTest.class,
    Java20PreviewTreeDumpTest.class
})
public class AllJavaAstTreeDumpTest {

}
