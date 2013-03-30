/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.symboltable.ScopeAndDeclarationFinder;

import org.junit.Test;

/**
 * @author Philip Graf
 */
public class JavaRuleViolationTest {
    
    /**
     * Verifies that {@link JavaRuleViolation} sets the variable name for an {@link ASTFormalParameter} node.
     */
    @Test
    public void testASTFormalParameterVariableName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        final ASTFormalParameter node = ast.getFirstDescendantOfType(ASTFormalParameter.class);
        final RuleContext context = new RuleContext();
        final JavaRuleViolation violation = new JavaRuleViolation(null, context, node, null);
        assertEquals("x", violation.getVariableName());
    }
    
    private ASTCompilationUnit parse(final String code) {
        final LanguageVersionHandler languageVersionHandler = Language.JAVA.getDefaultVersion().getLanguageVersionHandler();
        final ParserOptions options = languageVersionHandler.getDefaultParserOptions();
        final ASTCompilationUnit ast = (ASTCompilationUnit) languageVersionHandler.getParser(options).parse(null, new StringReader(code));
        // set scope of AST nodes
        ast.jjtAccept(new ScopeAndDeclarationFinder(), null);
        return ast;
    }
    
}
