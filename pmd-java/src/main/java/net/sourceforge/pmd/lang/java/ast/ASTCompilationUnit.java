/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.TokenDocument;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;

// FUTURE Change this class to extend from SimpleJavaNode, as TypeNode is not appropriate (unless I'm wrong)
public final class ASTCompilationUnit extends AbstractJavaTypeNode implements RootNode {

    private ClassTypeResolver classTypeResolver;
    private List<Comment> comments;
    private Map<Integer, String> noPmdComments = Collections.emptyMap();
    private TokenDocument tokenDocument;

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
    public CharSequence getText() {
        return tokenDocument.getFullText();
    }


    void setTokenDocument(TokenDocument document) {
        this.tokenDocument = document;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * @deprecated Use {@code getPackageName().isEmpty()}
     */
    @Deprecated
    public boolean declarationsAreInDefaultPackage() {
        return getPackageDeclaration() == null;
    }

    @Nullable
    public ASTPackageDeclaration getPackageDeclaration() {
        return AstImplUtil.getChildAs(this, 0, ASTPackageDeclaration.class);
    }

    /**
     * Returns the package name of this compilation unit. If there is no
     * package declaration, then returns the empty string.
     */
    @NonNull
    public String getPackageName() {
        ASTPackageDeclaration pack = getPackageDeclaration();
        return pack == null ? "" : pack.getPackageNameImage();
    }

    /**
     * Returns the type declarations declared in this compilation unit.
     * This may be empty if this a package-info.java, or a modular
     * compilation unit.
     */
    public List<ASTAnyTypeDeclaration> getTypeDeclarations() {
        List<ASTTypeDeclaration> tds = findChildrenOfType(ASTTypeDeclaration.class);
        return tds.stream().map(it -> (ASTAnyTypeDeclaration) it.getFirstChild()).collect(Collectors.toList());
    }


    @InternalApi
    @Deprecated
    public ClassTypeResolver getClassTypeResolver() {
        return classTypeResolver;
    }


    @Override
    public ASTCompilationUnit getRoot() {
        return this;
    }

    @InternalApi
    @Deprecated
    public void setClassTypeResolver(ClassTypeResolver classTypeResolver) {
        this.classTypeResolver = classTypeResolver;
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return noPmdComments;
    }

    void setNoPmdComments(Map<Integer, String> noPmdComments) {
        this.noPmdComments = noPmdComments;
    }
}
