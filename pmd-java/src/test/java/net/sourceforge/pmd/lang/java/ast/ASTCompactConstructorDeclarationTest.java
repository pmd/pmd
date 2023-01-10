/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTCompactConstructorDeclarationTest extends BaseParserTest {

    @Test
    void compactConstructorWithLambda() {
        ASTCompactConstructorDeclaration compactConstructor = java.getNodes(ASTCompactConstructorDeclaration.class,
                "import java.util.Objects;"
                    + "record RecordWithLambdaInCompactConstructor(String foo) {"
                    + "     RecordWithLambdaInCompactConstructor {"
                    + "         Objects.requireNonNull(foo, () -> \"foo\");"
                    + "     }"
                    + "}")
                .get(0);
        assertEquals(1, compactConstructor.getBody().getNumChildren());
    }
}
