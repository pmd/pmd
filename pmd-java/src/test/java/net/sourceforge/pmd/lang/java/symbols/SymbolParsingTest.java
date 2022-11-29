/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import org.junit.Assert;
import org.junit.Test;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.LocalVarAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;

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
    
    @Test
    public void testAnnotOnLocalVar() {
        // This only checks Target.LOCAL_VAR annotations, do not confuse with TYPE_USE on return types
        JClassSymbol sym = resolveSymbol(SomeClass.class);

        JMethodSymbol method = sym.getDeclaredMethods().stream().filter(it -> it.getSimpleName().equals("withAnnotatedLocal"))
                .findFirst().orElseThrow(AssertionError::new);
        
        ASTVariableDeclarator variableDeclarator = method.tryGetNode().descendants(ASTVariableDeclarator.class).first();
        JVariableSymbol localSym = variableDeclarator.getSymbolTable().variables().resolveFirst("local").getSymbol();
        
        PSet<SymAnnot> declaredAnnotations = localSym.getDeclaredAnnotations();
        
        Assert.assertEquals(1, declaredAnnotations.size());
        Assert.assertNotNull(localSym.getDeclaredAnnotation(LocalVarAnnotation.class));
    }
}
