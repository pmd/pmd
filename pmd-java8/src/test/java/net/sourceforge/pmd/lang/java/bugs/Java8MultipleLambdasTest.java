/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.bugs;

import java.io.StringReader;
import org.junit.Ignore;
import org.junit.Test;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;

@Ignore
public class Java8MultipleLambdasTest {

    private static final String MULTIPLE_JAVA_8_LAMBDAS =
            "public class MultipleLambdas {" + PMD.EOL +
            "  Observer a = (o, arg) -> System.out.println(\"a_\" + arg);" + PMD.EOL +
            "  Observer b = (o, arg) -> System.out.println(\"b_\" + arg);" + PMD.EOL +
            "}";

    @Test
    public void should_not_fail() {
        parseCode(MULTIPLE_JAVA_8_LAMBDAS);
    }

    private void parseCode(String code) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(code));
        SymbolFacade stb = new SymbolFacade();
        stb.initializeWith(acu);
    }


    // Was failing with :
    // java.lang.RuntimeException: Variable: image = 'i', line = 3 is already in the symbol table
    //    at net.AbstractJavaScope.checkForDuplicatedNameDeclaration(AbstractJavaScope.java:27)
    //    at net.sourceforge.pmd.lang.java.symboltable.AbstractJavaScope.addDeclaration(AbstractJavaScope.java:21)
    //    at net.sourceforge.pmd.lang.java.symboltable.ScopeAndDeclarationFinder.visit(ScopeAndDeclarationFinder.java:294)
    //    at net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId.jjtAccept(ASTVariableDeclaratorId.java:30)
    //    at net.AbstractJavaNode.childrenAccept(AbstractJavaNode.java:55)
    //    at net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter.visit(JavaParserVisitorAdapter.java:9)
    //    at net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter.visit(JavaParserVisitorAdapter.java:455)
    //    at net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression.jjtAccept(ASTLambdaExpression.java:21)
    //    at net.sourceforge.pmd.lang.java.ast.AbstractJavaNode.childrenAccept(AbstractJavaNode.java:55)
    //    at net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter.visit(JavaParserVisitorAdapter.java:9)
    //    at net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter.visit(JavaParserVisitorAdapter.java:312)
}
