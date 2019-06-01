/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;

// FUTURE Change this class to extend from SimpleJavaNode, as TypeNode is not appropriate (unless I'm wrong)
public final class ASTCompilationUnit extends AbstractJavaTypeNode implements RootNode {

    private ClassTypeResolver classTypeResolver;
    private List<Comment> comments;

    ASTCompilationUnit(int id) {
        super(id);
    }

    ASTCompilationUnit(JavaParser p, int id) {
        super(p, id);
    }

    public List<Comment> getComments() {
        return comments;
    }

    void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean declarationsAreInDefaultPackage() {
        return getPackageDeclaration() == null;
    }

    @Nullable
    public ASTPackageDeclaration getPackageDeclaration() {
        if (jjtGetNumChildren() > 0) {
            Node n = jjtGetChild(0);
            return n instanceof ASTPackageDeclaration ? (ASTPackageDeclaration) n : null;
        }
        return null;
    }

    public ClassTypeResolver getClassTypeResolver() {
        return classTypeResolver;
    }

    @InternalApi
    @Deprecated
    public void setClassTypeResolver(ClassTypeResolver classTypeResolver) {
        this.classTypeResolver = classTypeResolver;
    }
}
