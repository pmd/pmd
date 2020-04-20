/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

final class ReflectedClassImpl extends AbstractTypeParamOwnerSymbol<Class<?>> implements JClassSymbol {

    private final Class<?> myClass;
    private final @Nullable JClassSymbol enclosing;

    private @Nullable JClassSymbol superclass;
    private List<JClassSymbol> superInterfaces;


    private List<JClassSymbol> declaredClasses;
    private List<JMethodSymbol> declaredMethods;
    private List<JConstructorSymbol> declaredConstructors;
    private List<JFieldSymbol> declaredFields;

    private ReflectedClassImpl(ReflectionSymFactory symbolFactory, Class<?> myClass) {
        this(symbolFactory, null, myClass);
    }

    /**
     * This assumes that the enclosing symbol is correct and doesn't
     * check it itself unless assertions are enabled.
     */
    private ReflectedClassImpl(ReflectionSymFactory symbolFactory, @Nullable JClassSymbol enclosing, Class<?> myClass) {
        super(symbolFactory, myClass);

        this.myClass = myClass;
        this.enclosing = enclosing;

        assert !myClass.isArray() : "This class cannot represent array types";

        assert enclosing == null && myClass.getEnclosingClass() == null
            || myClass.getEnclosingClass() != null && enclosing != null
            && myClass.getEnclosingClass().getName().equals(enclosing.getBinaryName())
            : "Wrong enclosing class " + enclosing + ", expecting " + myClass.getEnclosingClass();
    }

    @Override
    public @NonNull String getBinaryName() {
        return myClass.getName();
    }

    @NonNull
    @Override
    public String getSimpleName() {
        return myClass.getSimpleName();
    }

    @Override
    public String getCanonicalName() {
        return myClass.getCanonicalName();
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public @NonNull String getPackageName() {
        return myClass.isPrimitive() ? PRIMITIVE_PACKAGE
                                     : ClassUtils.getPackageName(myClass);
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return myClass;
    }

    @Override
    public boolean isPrimitive() {
        return myClass.isPrimitive();
    }

    @Nullable
    @Override
    public JClassSymbol getSuperclass() {
        if (superclass == null) {
            superclass = symFactory.getClassSymbol(myClass.getSuperclass());
        }
        return superclass;
    }


    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        if (superInterfaces == null) {
            superInterfaces = myClass.isArray() ? ReflectSymInternals.ARRAY_SUPER_INTERFACES
                                                : Arrays.stream(myClass.getInterfaces()).map(symFactory::getClassSymbol).collect(toList());
        }
        return superInterfaces;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        if (declaredClasses == null) {
            declaredClasses = Arrays.stream(myClass.getDeclaredClasses())
                                    .map(k -> createWithEnclosing(symFactory, this, k))
                                    .collect(toList());
        }
        return declaredClasses;
    }

    @Override
    public boolean isInterface() {
        return myClass.isInterface();
    }

    @Override
    public boolean isEnum() {
        return myClass.isEnum();
    }

    @Override
    public boolean isRecord() {
        // Class::isRecord is only available in jdk 14
        Class<?> sup = myClass.getSuperclass();
        return sup != null && "java.lang.Record".equals(sup.getName());
    }

    @Override
    public boolean isAnnotation() {
        return myClass.isAnnotation();
    }

    @Override
    public boolean isAnonymousClass() {
        return myClass.isAnonymousClass();
    }

    @Override
    public boolean isLocalClass() {
        return myClass.isLocalClass();
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public JClassSymbol getEnclosingClass() {
        return enclosing;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        // TODO implement for completeness
        //  this is not strictly needed for typeres though,
        //  since this would only return non-null for a local class,
        //  which are always represented by AST symbols (since they're
        //  only visible in the local scope they're declared in)
        return null;
    }

    @Override
    public int getModifiers() {
        return myClass.getModifiers();
    }


    @Nullable
    @Override
    public JTypeDeclSymbol getArrayComponent() {
        return null;
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        if (declaredMethods == null) {
            declaredMethods = Arrays.stream(myClass.getDeclaredMethods())
                                    .filter(it -> !it.isBridge() && !it.isSynthetic())
                                    .map(it -> new ReflectedMethodImpl(this, it))
                                    .collect(toList());
        }
        return declaredMethods;
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        if (declaredConstructors == null) {
            declaredConstructors = Arrays.stream(myClass.getDeclaredConstructors())
                                         .filter(it -> !it.isSynthetic())
                                         .map(it -> new ReflectedCtorImpl(this, it))
                                         .collect(toList());
        }
        return declaredConstructors;
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        if (declaredFields == null) {
            declaredFields = Arrays.stream(myClass.getDeclaredFields())
                                   .filter(it -> !it.isSynthetic())
                                   .map(it -> new ReflectedFieldImpl(this, it))
                                   .collect(toList());
        }
        return declaredFields;
    }

    static ReflectedClassImpl createWithEnclosing(ReflectionSymFactory symbolFactory,
                                                  @Nullable JClassSymbol enclosing,
                                                  Class<?> myClass) {
        return new ReflectedClassImpl(symbolFactory, enclosing, myClass);
    }

    static ReflectedClassImpl createOuterClass(ReflectionSymFactory symbolFactory, Class<?> myClass) {
        return new ReflectedClassImpl(symbolFactory, myClass);
    }

}
