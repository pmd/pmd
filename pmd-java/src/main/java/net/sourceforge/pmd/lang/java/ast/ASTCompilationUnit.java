/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;

// FUTURE Change this class to extend from SimpleJavaNode, as TypeNode is not appropriate (unless I'm wrong)
public class ASTCompilationUnit extends AbstractJavaTypeNode implements RootNode {

    private ClassTypeResolver classTypeResolver;
    private List<Comment> comments;

    @InternalApi
    @Deprecated
    public ASTCompilationUnit(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTCompilationUnit(JavaParser p, int id) {
        super(p, id);
    }

    public List<Comment> getComments() {
        return comments;
    }

    @InternalApi
    @Deprecated
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * @deprecated Use {@code getPackageName().isEmpty()}
     */
    @Deprecated
    public boolean declarationsAreInDefaultPackage() {
        return getPackageDeclaration() == null;
    }

    public ASTPackageDeclaration getPackageDeclaration() {
        if (getNumChildren() > 0) {
            Node n = getChild(0);
            return n instanceof ASTPackageDeclaration ? (ASTPackageDeclaration) n : null;
        }
        return null;
    }

    /**
     * Returns the package name of this compilation unit. If this is in
     * the default package, returns the empty string.
     */
    // @NonNull
    public String getPackageName() {
        ASTPackageDeclaration pdecl = getPackageDeclaration();
        return pdecl == null ? "" : pdecl.getPackageNameImage();
    }

    @InternalApi
    @Deprecated
    public ClassTypeResolver getClassTypeResolver() {
        return classTypeResolver;
    }

    @InternalApi
    @Deprecated
    public void setClassTypeResolver(ClassTypeResolver classTypeResolver) {
        this.classTypeResolver = classTypeResolver;
    }
}
