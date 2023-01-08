/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

public class ASTCompactConstructorDeclarationTest extends BaseParserTest {

    @Test
    public void compactConstructorWithLambda() {
        ASTCompactConstructorDeclaration compactConstructor = java.getNodes(ASTCompactConstructorDeclaration.class,
                "import java.util.Objects;"
                    + "record RecordWithLambdaInCompactConstructor(String foo) {"
                    + "     RecordWithLambdaInCompactConstructor {"
                    + "         Objects.requireNonNull(foo, () -> \"foo\");"
                    + "     }"
                    + "}")
                .get(0);
        Assert.assertEquals(1, compactConstructor.getBody().getNumChildren());
    }
}
