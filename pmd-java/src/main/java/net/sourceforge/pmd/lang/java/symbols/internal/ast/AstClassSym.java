/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPermitsList;
import net.sourceforge.pmd.lang.java.ast.ASTRecordComponent;
import net.sourceforge.pmd.lang.java.ast.ASTRecordComponentList;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JRecordComponentSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.ImplicitMemberSymbols;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.CollectionUtil;


final class AstClassSym
    extends AbstractAstTParamOwner<ASTTypeDeclaration>
    implements JClassSymbol {

    private final @Nullable JTypeParameterOwnerSymbol enclosing;
    private final List<JClassSymbol> declaredClasses;
    private final List<JMethodSymbol> declaredMethods;
    private final List<JConstructorSymbol> declaredCtors;
    private final List<JFieldSymbol> declaredFields;
    private final List<JFieldSymbol> enumConstants; // subset of declaredFields
    private final List<JRecordComponentSymbol> recordComponents;
    private final PSet<String> annotAttributes;

    private List<JClassSymbol> permittedSubclasses;

    AstClassSym(ASTTypeDeclaration node,
                AstSymFactory factory,
                @Nullable JTypeParameterOwnerSymbol enclosing) {
        super(node, factory);
        this.enclosing = enclosing;

        // evaluate everything strictly
        // this populates symbols on the relevant AST nodes

        final List<JClassSymbol> myClasses = new ArrayList<>();
        final List<JMethodSymbol> myMethods = new ArrayList<>();
        final List<JConstructorSymbol> myCtors = new ArrayList<>();
        final List<JFieldSymbol> myFields = new ArrayList<>();
        final List<JFieldSymbol> enumConstants;
        final List<JRecordComponentSymbol> recordComponents;

        if (isRecord()) {
            ASTRecordComponentList components = Objects.requireNonNull(node.getRecordComponents(),
                                                                       "Null component list for " + node);
            recordComponents = mapComponentsToMutableList(factory, components, myFields);

            JConstructorSymbol canonicalRecordCtor = ImplicitMemberSymbols.recordConstructor(this, recordComponents, components.isVarargs());
            myCtors.add(canonicalRecordCtor);
            InternalApiBridge.setSymbol(components, canonicalRecordCtor);

        } else {
            recordComponents = Collections.emptyList();
        }

        if (isEnum()) {
            enumConstants = new ArrayList<>();
            node.getEnumConstants()
                .forEach(constant -> {
                    AstFieldSym fieldSym = new AstFieldSym(constant.getVarId(), factory, this);
                    enumConstants.add(fieldSym);
                    myFields.add(fieldSym);
                });
        } else {
            enumConstants = null;
        }

        for (ASTBodyDeclaration dnode : node.getDeclarations()) {

            if (dnode instanceof ASTTypeDeclaration) {
                myClasses.add(new AstClassSym((ASTTypeDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTMethodDeclaration) {
                if (!recordComponents.isEmpty() && ((ASTMethodDeclaration) dnode).getArity() == 0) {
                    // filter out record component, so that the accessor is not generated
                    recordComponents.removeIf(f -> f.nameEquals(((ASTMethodDeclaration) dnode).getName()));
                }
                myMethods.add(new AstMethodSym((ASTMethodDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTConstructorDeclaration) {
                myCtors.add(new AstCtorSym((ASTConstructorDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTFieldDeclaration) {
                for (ASTVariableId varId : ((ASTFieldDeclaration) dnode).getVarIds()) {
                    myFields.add(new AstFieldSym(varId, factory, this));
                }
            }
        }
        

        if (!recordComponents.isEmpty()) {
            // then the recordsComponents contains all record components
            // for which we must synthesize an accessor (explicitly declared
            // accessors have been filtered out)
            for (JRecordComponentSymbol component : recordComponents) {
                myMethods.add(ImplicitMemberSymbols.recordAccessor(this, component));
            }
        }

        if (myCtors.isEmpty() && isClass() && !isAnonymousClass()) {
            myCtors.add(ImplicitMemberSymbols.defaultCtor(this));
        }

        if (this.isEnum()) {
            myMethods.add(ImplicitMemberSymbols.enumValues(this));
            myMethods.add(ImplicitMemberSymbols.enumValueOf(this));
        }

        this.declaredClasses = Collections.unmodifiableList(myClasses);
        this.declaredMethods = Collections.unmodifiableList(myMethods);
        this.declaredCtors = Collections.unmodifiableList(myCtors);
        this.declaredFields = Collections.unmodifiableList(myFields);
        this.enumConstants = CollectionUtil.makeUnmodifiableAndNonNull(enumConstants);
        this.recordComponents = CollectionUtil.makeUnmodifiableAndNonNull(recordComponents);
        this.annotAttributes = isAnnotation()
                               ? getDeclaredMethods().stream().filter(JMethodSymbol::isAnnotationAttribute).map(JElementSymbol::getSimpleName).collect(CollectionUtil.toPersistentSet())
                               : HashTreePSet.empty();
    }


    private List<JRecordComponentSymbol> mapComponentsToMutableList(AstSymFactory factory,
                                                          ASTRecordComponentList components,
                                                          List<JFieldSymbol> fieldSyms) {
        List<JRecordComponentSymbol> list = new ArrayList<>();
        for (ASTRecordComponent comp : components) {
            list.add(new AstRecordComponentSym(comp, factory, this));
            fieldSyms.add(new AstFieldSym(comp.getVarId(), factory, this));
        }
        return list;
    }

    @Override
    public @NonNull String getSimpleName() {
        return node.getSimpleName();
    }


    @Override
    public @NonNull String getBinaryName() {
        return node.getBinaryName();
    }

    @Override
    public @Nullable String getCanonicalName() {
        return node.getCanonicalName();
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        if (enclosing instanceof JClassSymbol) {
            return (JClassSymbol) enclosing;
        } else if (enclosing instanceof JExecutableSymbol) {
            return enclosing.getEnclosingClass();
        }
        assert enclosing == null;
        return null;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return enclosing instanceof JExecutableSymbol ? (JExecutableSymbol) enclosing : null;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return declaredClasses;
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return declaredMethods;
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        return declaredCtors;
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return declaredFields;
    }

    @Override
    public @NonNull List<JFieldSymbol> getEnumConstants() {
        return enumConstants;
    }

    @Override
    public @NonNull List<JRecordComponentSymbol> getRecordComponents() {
        return recordComponents;
    }


    @Override
    public List<JClassSymbol> getPermittedSubtypes() {
        // permitted subclasses are populated lazily because they require
        // symbol and type resolution to determine which types are sealed.
        if (permittedSubclasses == null) {
            ASTPermitsList permits = node.getPermitsClause();
            if (permits != null) {
                this.permittedSubclasses = permits.toList().stream().map(it -> {
                    JTypeDeclSymbol symbol = it.getTypeMirror().getSymbol();
                    if (symbol instanceof JClassSymbol) {
                        return (JClassSymbol) symbol;
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(CollectionUtil.toUnmodifiableList());
            } else if (isSealed()) {
                // sealed with no permits clause: infer permitted
                this.permittedSubclasses = inferPermittedSubclasses();
            } else {
                this.permittedSubclasses = Collections.emptyList();
            }
        }
        return permittedSubclasses;
    }

    private List<JClassSymbol> inferPermittedSubclasses() {
        /*
         *  If the declaration of a sealed class C lacks a permits clause,
         * then the permitted direct subclasses of C are as follows:
         *
         *  1. If C is not an enum class, then its permitted direct subclasses
         *     are those classes declared in the same compilation unit as C (§7.3)
         *     which have a canonical name (§6.7) and whose direct superclass is C.
         *
         *     That is, the permitted direct subclasses are inferred as the classes
         *     in the same compilation unit that specify C as their direct superclass.
         *     The requirement for a canonical name means that no local classes or
         *     anonymous classes will be considered.
         *
         *     It is a compile-time error if the declaration of a sealed class C lacks
         *     a permits clause and C has no permitted direct subclasses.
         *
         *  2. If C is an enum class, then its permitted direct subclasses, if any,
         *     are specified in §8.9.
         */
        if (!isEnum()) {
            boolean isInterface = isInterface();
            List<JClassSymbol> list = node
                .getRoot().descendants(ASTTypeDeclaration.class).crossFindBoundaries()
                .filter(it -> it.getCanonicalName() != null)
                .filter(it -> {
                    if (isInterface) {
                        return it.getSuperInterfaceTypeNodes().any(ty -> Objects.equals(ty.getTypeMirror().getSymbol(), this));
                    }
                    return NodeStream.of(it.getSuperClassTypeNode()).any(ty -> Objects.equals(ty.getTypeMirror().getSymbol(), this));
                }).toList(ASTTypeDeclaration::getSymbol);
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isSealed() {
        return node.hasModifiers(JModifier.SEALED);
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        TypeSystem ts = getTypeSystem();

        if (node.isEnum()) {

            return factory.enumSuperclass(this);

        } else if (node instanceof ASTClassDeclaration) {

            ASTClassType superClass = node.getSuperClassTypeNode();
            return superClass == null
                   ? ts.OBJECT
                   // this cast relies on the fact that the superclass is not a type variable
                   : (JClassType) TypeOps.subst(superClass.getTypeMirror(), substitution);

        } else if (isAnonymousClass()) {

            if (node.getParent() instanceof ASTEnumConstant) {

                return node.getEnclosingType().getTypeMirror().subst(substitution);

            } else if (node.getParent() instanceof ASTConstructorCall) {

                @NonNull JTypeMirror sym = ((ASTConstructorCall) node.getParent()).getTypeMirror();

                return sym instanceof JClassType && !sym.isInterface()
                       ? (JClassType) sym
                       : factory.types().OBJECT;
            }

        } else if (isRecord()) {

            return factory.recordSuperclass();

        } else if (isAnnotation()) {

            return ts.OBJECT;

        }

        return null;
    }

    @Override
    public @Nullable JClassSymbol getSuperclass() {
        // notice this relies on the fact that the extends clause
        // (or the type node of the constructor call, for an anonymous class),
        // was disambiguated early

        // We special case anonymous classes so as not to trigger overload resolution
        if (isAnonymousClass() && node.getParent() instanceof ASTConstructorCall) {

            @NonNull JTypeMirror sym = ((ASTConstructorCall) node.getParent()).getTypeNode().getTypeMirror();

            return sym instanceof JClassType && !sym.isInterface()
                   ? ((JClassType) sym).getSymbol()
                   : factory.types().OBJECT.getSymbol();

        }

        JClassType sup = getSuperclassType(Substitution.EMPTY);
        return sup == null ? null : sup.getSymbol();
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        List<JClassSymbol> itfs = CollectionUtil.mapNotNull(
            node.getSuperInterfaceTypeNodes(),
            n -> {
                // we play safe here, but the symbol is either a JClassSymbol
                // or a JTypeParameterSymbol, with the latter case being a
                // compile-time error
                JTypeDeclSymbol sym = n.getTypeMirror().getSymbol();
                return sym instanceof JClassSymbol ? (JClassSymbol) sym : null;
            }
        );
        if (isAnnotation()) {
            itfs = CollectionUtil.concatView(Collections.singletonList(factory.annotationSym()), itfs);
        }
        return itfs;
    }

    @Override
    public List<JClassType> getSuperInterfaceTypes(Substitution subst) {
        List<JClassType> itfs = CollectionUtil.map(node.getSuperInterfaceTypeNodes(), n -> (JClassType) TypeOps.subst(n.getTypeMirror(), subst));
        if (isAnnotation()) {
            itfs = CollectionUtil.concatView(Collections.singletonList(factory.annotationType()), itfs);
        }
        return itfs;
    }

    @Override
    public @Nullable JTypeDeclSymbol getArrayComponent() {
        return null;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return node.isInterface();
    }

    @Override
    public boolean isEnum() {
        return node.isEnum();
    }

    @Override
    public boolean isRecord() {
        return node.isRecord();
    }

    @Override
    public boolean isAnnotation() {
        return node.isAnnotation();
    }

    @Override
    public boolean isLocalClass() {
        return node.isLocal();
    }

    @Override
    public boolean isAnonymousClass() {
        return node.isAnonymous();
    }

    @Override
    public PSet<String> getAnnotationAttributeNames() {
        return annotAttributes;
    }
}
