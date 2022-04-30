/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    PlsqlTreeDumpTest.class,
    ParenthesisGroupTest.class,
    ExecuteImmediateBulkCollectTest.class
})
public class AllPlsqlAstTreeDumpTest {

}
