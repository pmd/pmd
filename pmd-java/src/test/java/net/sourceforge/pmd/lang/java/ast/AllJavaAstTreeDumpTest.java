/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    ParserCornersTest.class,
    Java14TreeDumpTest.class,
    Java15TreeDumpTest.class,
    Java16TreeDumpTest.class,
    Java17TreeDumpTest.class,
    Java18PreviewTreeDumpTest.class,
    Java19PreviewTreeDumpTest.class
})
class AllJavaAstTreeDumpTest {

}
