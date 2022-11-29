/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

/**
 *
 */
public class SymbolParsingTest extends AbstractSymbolTest {

    private final JavaParsingHelper parser = JavaParsingHelper.DEFAULT;
    
    public SymbolParsingTest() {
        super(true); // ASM assumes no debug symbols are available
    }

    @Override
    protected JClassSymbol resolveSymbol(Class<?> clazz) {
        ASTCompilationUnit unit = parser.parseClass(clazz);
        return unit.firstChild(ASTAnyTypeDeclaration.class).getSymbol();
    }
}
