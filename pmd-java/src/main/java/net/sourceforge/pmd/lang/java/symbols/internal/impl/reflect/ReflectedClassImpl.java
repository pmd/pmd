/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

final class ReflectedClassImpl extends AbstractTypeParamOwnerSymbol<Class<?>> implements JClassSymbol {

    private final Class<?> myClass;
    private final @Nullable ReflectedClassImpl enclosing;

    private @Nullable JClassSymbol superclass;
    private List<JClassSymbol> superInterfaces;


    private List<JClassSymbol> declaredClasses;
    private List<JMethodSymbol> declaredMethods;
    private List<JConstructorSymbol> declaredConstructors;
    private List<JFieldSymbol> declaredFields;

    private ReflectedClassImpl(SymbolFactory symbolFactory, Class<?> myClass) {
        this(symbolFactory, null, myClass);
    }

    /**
     * This assumes that the enclosing symbol is correct and doesn't
     * check it itself unless assertions are enabled.
     */
    private ReflectedClassImpl(SymbolFactory symbolFactory, @Nullable ReflectedClassImpl enclosing, Class<?> myClass) {
        super(symbolFactory, myClass);

        this.myClass = myClass;
        this.enclosing = enclosing;

        assert !myClass.isArray() : "This class cannot represent array types";

        try {
            assert enclosing == null && myClass.getEnclosingClass() == null
                || enclosing != null && myClass.getEnclosingClass() != null
                && myClass.getEnclosingClass().getName().equals(enclosing.getBinaryName())
                : "Wrong enclosing class " + enclosing + " for " + myClass + ", expecting " + myClass.getEnclosingClass();
        } catch (AssertionError e) {
            // sometimes this fails, but the Class instance is wrong
            // e.printStackTrace();
        }
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
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        final Executable method = myClass.getEnclosingMethod();
        if (method != null && enclosing != null) {
            return enclosing.getDeclaredMethods()
                            .stream()
                            .filter(it -> it instanceof ReflectedMethodImpl)
                            .filter(it -> ((ReflectedMethodImpl) it).reflected == method)
                            .findFirst()
                            .orElse(null);
        }

        Constructor<?> ctor = myClass.getEnclosingConstructor();
        if (ctor != null && enclosing != null) {
            return enclosing.getConstructors().stream()
                            .filter(it -> it instanceof ReflectedCtorImpl)
                            .filter(it -> ((ReflectedCtorImpl) it).reflected == ctor)
                            .findFirst()
                            .orElse(null);
        }
        return null;
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
            superclass = getTypeSystem().getClassSymbol(myClass.getSuperclass());
        }
        return superclass;
    }


    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        if (superInterfaces == null) {
            superInterfaces = CollectionUtil.map(myClass.getInterfaces(), getTypeSystem()::getClassSymbol);
        }
        return superInterfaces;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        if (declaredClasses == null) {
            declaredClasses = CollectionUtil.map(
                Arrays.asList(myClass.getDeclaredClasses()),
                k -> createWithEnclosing(factory, this, k)
            );
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
            Method[] declared = myClass.getDeclaredMethods();
            List<JMethodSymbol> list = new ArrayList<>(declared.length);
            for (Method it : declared) {
                if (!it.isBridge() && !it.isSynthetic()) {
                    list.add(new ReflectedMethodImpl(this, it));
                }
            }
            this.declaredMethods = Collections.unmodifiableList(list);
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

    @Override
    public OptionalBool fastIsSubClassOf(JClassSymbol symbol) {
        Class<?> other = symbol.getJvmRepr();
        if (other != null) {
            return other.isAssignableFrom(myClass) ? OptionalBool.YES : OptionalBool.NO;
        }
        return OptionalBool.UNKNOWN;
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        Type superclass = myClass.getGenericSuperclass();
        if (superclass == null) {
            return null;
        }
        return (JClassType) TypesFromReflection.fromReflect(getTypeSystem(), superclass, getLexicalScope(), substitution);
    }

    @Override
    @SuppressWarnings( {"unchecked", "rawtypes"})
    public List<JClassType> getSuperInterfaceTypes(Substitution substitution) {
        return (List<JClassType>) (List) TypesFromReflection.fromReflect(getTypeSystem(), getLexicalScope(), substitution, myClass.getGenericInterfaces());
    }

    static ReflectedClassImpl createWithEnclosing(SymbolFactory symbolFactory,
                                                  @Nullable ReflectedClassImpl enclosing,
                                                  Class<?> myClass) {
        return new ReflectedClassImpl(symbolFactory, enclosing, myClass);
    }

    static ReflectedClassImpl createOuterClass(SymbolFactory symbolFactory, Class<?> myClass) {
        return new ReflectedClassImpl(symbolFactory, myClass);
    }

}
