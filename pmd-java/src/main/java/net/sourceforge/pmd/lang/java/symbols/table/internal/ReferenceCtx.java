/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.AMBIGUOUS_NAME_REFERENCE;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.CANNOT_RESOLVE_MEMBER;
import static net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;

import java.util.HashSet;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Context of a usage reference ("in which class does the name occur?"),
 * which determines accessibility of referenced symbols. The context may
 * have no enclosing class, eg in the "extends" clause of a toplevel type.
 *
 * <p>This is an internal helper class for disambiguation pass
 */
public final class ReferenceCtx {

    final JavaAstProcessor processor;
    final String packageName;
    final @Nullable JClassSymbol enclosingClass;

    private ReferenceCtx(JavaAstProcessor processor, String packageName, @Nullable JClassSymbol enclosingClass) {
        this.processor = processor;
        this.packageName = packageName;
        this.enclosingClass = enclosingClass;
    }

    public static ReferenceCtx root(JavaAstProcessor processor, ASTCompilationUnit root) {
        return new ReferenceCtx(processor, root.getPackageName(), null);
    }

    public ReferenceCtx scopeDownToNested(JClassSymbol newEnclosing) {
        assert enclosingClass == null || enclosingClass.equals(newEnclosing.getEnclosingClass())
            : "Not a child class of the current context (" + this + "): " + newEnclosing;
        assert newEnclosing.getPackageName().equals(packageName)
            : "Mismatched package name";
        return new ReferenceCtx(processor, packageName, newEnclosing);
    }

    public @Nullable FieldSig findStaticField(JTypeDeclSymbol classSym, String name) {
        if (classSym instanceof JClassSymbol) {
            JClassType t = (JClassType) classSym.getTypeSystem().typeOf(classSym, false);
            return JavaResolvers.getMemberFieldResolver(t, packageName, enclosingClass, name).resolveFirst(name);
        }
        return null;
    }

    public @Nullable JClassSymbol findTypeMember(JTypeDeclSymbol classSym, String name, JavaNode errorLocation) {
        if (classSym instanceof JClassSymbol) {
            JClassType c = (JClassType) classSym.getTypeSystem().typeOf(classSym, false);
            @NonNull List<JClassType> found = JavaResolvers.getMemberClassResolver(c, packageName, enclosingClass, name).resolveHere(name);
            JClassType result = maybeAmbiguityError(name, errorLocation, found);
            return result == null ? null : result.getSymbol();
        }
        return null;
    }


    <T extends JTypeMirror> T maybeAmbiguityError(String name, JavaNode errorLocation, @NonNull List<? extends T> found) {
        if (found.isEmpty()) {
            return null;
        } else if (found.size() > 1) {
            // FIXME when type is reachable through several paths, there may be duplicates!
            HashSet<? extends T> distinct = new HashSet<>(found);
            if (distinct.size() == 1) {
                return distinct.iterator().next();
            }
            processor.getLogger().error(
                errorLocation,
                AMBIGUOUS_NAME_REFERENCE,
                name,
                canonicalNameOf(found.get(0).getSymbol()),
                canonicalNameOf(found.get(1).getSymbol())
            );
            // fallthrough and use the first one anyway
        }
        return found.get(0);
    }

    private String canonicalNameOf(JTypeDeclSymbol sym) {
        if (sym instanceof JClassSymbol) {
            return ((JClassSymbol) sym).getCanonicalName();
        } else {
            assert sym instanceof JTypeParameterSymbol;
            return sym.getEnclosingClass().getCanonicalName() + "#" + sym.getSimpleName();
        }
    }

    public JClassSymbol resolveClassFromBinaryName(String binary) {
        // we may report inaccessible members too
        return processor.getSymResolver().resolveClassFromBinaryName(binary);

    }

    public static ReferenceCtx ctxOf(ASTAnyTypeDeclaration node, JavaAstProcessor processor, boolean outsideContext) {
        assert node != null;

        if (outsideContext) {
            // then the context is the enclosing of the given type decl
            JClassSymbol enclosing = node.isTopLevel() ? null : node.getEnclosingType().getSymbol();
            return new ReferenceCtx(processor, node.getPackageName(), enclosing);
        } else {
            return new ReferenceCtx(processor, node.getPackageName(), node.getSymbol());
        }
    }

    public void reportUnresolvedMember(JavaNode location, Fallback fallbackStrategy, String memberName, JTypeDeclSymbol owner) {
        if (owner.isUnresolved()) {
            // would already have been reported on owner
            return;
        }

        String ownerName = owner instanceof JClassSymbol ? ((JClassSymbol) owner).getCanonicalName()
                                                         : "type variable " + owner.getSimpleName();

        this.processor.getLogger().warning(location, CANNOT_RESOLVE_MEMBER, memberName, ownerName, fallbackStrategy);
    }

    public SemanticErrorReporter getLogger() {
        return processor.getLogger();
    }

    @Override
    public String toString() {
        return "ReferenceCtx{"
            + "packageName='" + packageName + '\''
            + ", enclosingClass=" + enclosingClass
            + '}';
    }

    public JTypeMirror resolveSingleTypeName(JSymbolTable symTable, String image, JavaNode errorLoc) {
        return maybeAmbiguityError(image, errorLoc, symTable.types().resolve(image));
    }


    public JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        return processor.makeUnresolvedReference(canonicalName, typeArity);
    }

    public JClassSymbol makeUnresolvedReference(JTypeDeclSymbol outer, String simpleName, int typeArity) {
        return processor.makeUnresolvedReference(outer, simpleName, typeArity);
    }

    /**
     * Fallback strategy for unresolved stuff.
     */
    public enum Fallback {
        AMBIGUOUS("ambiguous"),
        FIELD_ACCESS("a field access"),
        PACKAGE_NAME("a package name"),
        TYPE("an unresolved type");

        private final String displayName;

        Fallback(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
