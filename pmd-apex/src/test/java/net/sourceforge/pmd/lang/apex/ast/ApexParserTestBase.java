/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

<<<<<<< HEAD
import com.google.summit.ast.declaration.TypeDeclaration;

=======
>>>>>>> origin/master
public class ApexParserTestBase {

    protected final ApexParsingHelper apex = ApexParsingHelper.DEFAULT.withResourceContext(getClass());

<<<<<<< HEAD
    protected ApexRootNode<? extends TypeDeclaration> parse(String code) {
        return apex.parse(code);
    }

    protected ApexRootNode<? extends TypeDeclaration> parse(String code, String fileName) {
        return apex.parse(code, null, fileName);
    }

    protected ApexRootNode<? extends TypeDeclaration> parseResource(String code) {
        return apex.parseResource(code);
=======

    protected ASTUserClassOrInterface<?> parse(String code) {
        return apex.parse(code).getMainNode();
    }

    protected ASTUserClassOrInterface<?> parse(String code, String fileName) {
        return apex.parse(code, null, fileName).getMainNode();
    }

    protected ASTUserClassOrInterface<?> parseResource(String code) {
        return apex.parseResource(code).getMainNode();
>>>>>>> origin/master
    }
}
