/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ASTImportDeclarationTest.class, ASTVariableDeclaratorIdTest.class, AccessNodeTest.class, ClassDeclTest.class, FieldDeclTest.class, MethodDeclTest.class, SimpleNodeTest.class })
public class ASTTests {

}
