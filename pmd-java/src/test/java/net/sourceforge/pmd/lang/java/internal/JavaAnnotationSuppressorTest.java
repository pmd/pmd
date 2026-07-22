/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

class JavaAnnotationSuppressorTest {
    /**
     * As Import Declaration cannot be annotated on their own, they should still be suppressable
     * by annotating their following sibling (which is most likely a type declaration).
     */
    @Test
    void suppressTopLevelNodes() {
        ASTCompilationUnit root = JavaParsingHelper.DEFAULT.parse("import foo; @SupressWarnings(\"\") class Bar {}");
        ASTImportDeclaration astImportDeclaration = root.firstChild(ASTImportDeclaration.class);
        ASTAnnotation astAnnotation = root.descendants(ASTAnnotation.class).first();

        JavaAnnotationSuppressor suppressor = new JavaAnnotationSuppressor();
        List<ASTAnnotation> annotations = suppressor.getAnnotations(astImportDeclaration).toList();
        assertTrue(annotations.contains(astAnnotation));
    }

    @Test
    void suppressTopLevelNodesConsiderOnlyNextSibling() {
        ASTCompilationUnit root = JavaParsingHelper.DEFAULT.parse("import foo; public class FirstClass {}\n @SupressWarnings(\"\") class Bar {}");
        ASTImportDeclaration astImportDeclaration = root.firstChild(ASTImportDeclaration.class);
        ASTAnnotation astAnnotation = root.descendants(ASTAnnotation.class).first();

        JavaAnnotationSuppressor suppressor = new JavaAnnotationSuppressor();
        List<ASTAnnotation> annotations = suppressor.getAnnotations(astImportDeclaration).toList();
        assertFalse(annotations.contains(astAnnotation));
        assertTrue(annotations.isEmpty());
    }

    @Test
    void suppressAnnotatableNode() {
        ASTCompilationUnit root = JavaParsingHelper.DEFAULT.parse("@SupressWarnings(\"\") class Bar {}");
        ASTClassDeclaration astClassDeclaration = root.firstChild(ASTClassDeclaration.class);
        ASTAnnotation astAnnotation = root.descendants(ASTAnnotation.class).first();

        JavaAnnotationSuppressor suppressor = new JavaAnnotationSuppressor();
        List<ASTAnnotation> annotations = suppressor.getAnnotations(astClassDeclaration).toList();
        assertTrue(annotations.contains(astAnnotation));
    }
}
