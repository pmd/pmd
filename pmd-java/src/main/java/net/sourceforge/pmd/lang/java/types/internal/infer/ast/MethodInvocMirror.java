/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaResolvers;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.InternalMethodTypeItf;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadSet;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors.MirrorMaker;

class MethodInvocMirror extends BaseInvocMirror<ASTMethodCall> {


    MethodInvocMirror(JavaExprMirrors mirrors, ASTMethodCall call,
                      boolean isStandalone,
                      @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        super(mirrors, call, isStandalone, parent, subexprMaker);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        JMethodSig ctdecl = getStandaloneCtdecl().getMethodType();
        return mayBePoly && isContextDependent(ctdecl) ? null : ctdecl.getReturnType();
    }

    private static boolean isContextDependent(JMethodSig m) {
        m = InternalMethodTypeItf.cast(m).adaptedMethod();
        return m.isGeneric() && TypeOps.mentionsAny(m.getReturnType(), m.getTypeParameters());
    }

    @Override
    public @NonNull TypeSpecies getStandaloneSpecies() {
        return TypeSpecies.getSpecies(getStandaloneCtdecl().getMethodType().getReturnType());
    }

    @Override
    public List<JMethodSig> getAccessibleCandidates() {
        ASTExpression lhs = myNode.getQualifier();
        if (lhs == null) {
            // already filters accessibility
            return myNode.getSymbolTable().methods().resolve(getName());
        } else if (myNode.getEnclosingType() == null) {
            return Collections.emptyList();
        } else {
            JTypeMirror lhsType;
            if (lhs instanceof ASTConstructorCall) {
                ASTConstructorCall ctor = (ASTConstructorCall) lhs;
                ASTAnonymousClassDeclaration anon = ctor.getAnonymousClassDeclaration();
                // put methods declared in the anonymous class in scope
                lhsType = anon != null ? anon.getTypeMirror(getTypingContext())
                                       : ctor.getTypeMirror(getTypingContext()); // may resolve diamonds
            } else {
                lhsType = lhs.getTypeMirror(getTypingContext());
            }
            lhsType = TypeOps.getMemberSource(lhsType);
            Predicate<JMethodSymbol> isMember;
            if (lhsType instanceof JIntersectionType) {
                JTypeMirror primary = ((JIntersectionType) lhsType).getPrimaryBound();
                // null if the intersection induces an interface (JLS 4.9)
                JTypeDeclSymbol classBound = primary.isTop() ? null : primary.getSymbol();
                isMember = it -> JavaResolvers.isIntersectionMemberMethod(it, classBound);
            } else {
                JTypeDeclSymbol type = lhsType.getSymbol();
                isMember = it -> JavaResolvers.isMemberMethod(it, type);
            }
            JClassSymbol enclosing = myNode.getEnclosingType().getSymbol();
            Predicate<JMethodSymbol> accessible = TypeOps.accessibleMethodFilter(getName(), enclosing);
            if (lhs instanceof ASTTypeExpression) {
                // Instance methods are candidates even for a TypeName qualifier. If one
                // of them is chosen, the call is an error (JLS 15.12.3), as javac reports it.
                // For a protected method of another package, the accessibility filter only
                // looks at the innermost enclosing class and not at the qualifier, so these
                // methods are checked here against JLS 6.6.2 instead.
                JTypeMirror qualifier = lhsType;
                Predicate<JMethodSymbol> filter = accessible;
                accessible = it -> isProtectedOfAnotherPackage(it, enclosing)
                    ? it.nameEquals(getName()) && isProtectedAccessibleThrough(it, qualifier, enclosing)
                    : filter.test(it);
            }
            return lhsType.streamMethods(accessible.and(isMember))
                          .collect(OverloadSet.collectMostSpecific(lhsType)); // remove overridden, hidden methods
        }
    }


    private static boolean isProtectedOfAnotherPackage(JMethodSymbol method, JClassSymbol enclosing) {
        return Modifier.isProtected(method.getModifiers())
            && !method.getPackageName().equals(enclosing.getPackageName());
    }

    // A protected method of another package is accessible only in the body of
    // a subclass S of its declaring class, which includes the classes nested
    // in S (JLS 6.6.2). For an instance method, the qualifier type must also
    // be S or a subclass of S (JLS 6.6.2.1).
    private static boolean isProtectedAccessibleThrough(JMethodSymbol method, JTypeMirror qualifier, JClassSymbol enclosing) {
        JClassSymbol owner = method.getEnclosingClass();
        for (JClassSymbol s = enclosing; s != null; s = s.getEnclosingClass()) {
            boolean isSubclass = !s.isInterface() && s.getTypeSystem().declaration(s).getAsSuper(owner) != null;
            if (isSubclass && (method.isStatic() || qualifier.getAsSuper(s) != null)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JTypeMirror getErasedReceiverType() {
        return getReceiverType().getErasure();
    }

    @Override
    public @NonNull JTypeMirror getReceiverType() {
        ASTExpression qualifier = myNode.getQualifier();
        if (qualifier != null) {
            return qualifier.getTypeMirror(getTypingContext());
        } else {
            return myNode.getEnclosingType().getTypeMirror();
        }
    }

    @Override
    public String getName() {
        return myNode.getMethodName();
    }


}
