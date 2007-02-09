/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ASTImportDeclarationTest.class, ASTVariableDeclaratorIdTest.class, AccessNodeTest.class, ClassDeclTest.class, FieldDeclTest.class, MethodDeclTest.class, SimpleNodeTest.class })
public class ASTTests {

}


/*
 * $Log$
 * Revision 1.6  2007/02/09 01:38:05  allancaplan
 * Moving to JUnit 4
 *
 * Revision 1.5  2006/02/10 14:26:25  tomcopeland
 * Huge reformatting checkin
 *
 * Revision 1.4  2006/02/10 14:15:19  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.3  2004/04/08 18:51:07  tomcopeland
 * Implemented RFE 925839 - Added some more detail to the UseSingletonRule.
 *
 * Revision 1.2  2003/11/20 16:01:02  tomcopeland
 * Changing over license headers in the source code
 *
 * Revision 1.1  2003/09/29 14:32:31  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
