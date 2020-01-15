/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;


/**
 * Implicit type imports from {@literal java.lang}.
 */
final class JavaLangSymbolTable extends AbstractSymbolTable {

    private static final Map<String, JTypeDeclSymbol> COMMON_JAVA_LANG;
    private final ASTCompilationUnit fileNode;


    static {
        List<Class<?>> classes = Arrays.asList(
            // These are just those that seem the most common,
            // I didn't run any statistics or anything
            // If a type is not in there it will be queried like all
            // the others through the ClassLoader
            java.lang.AssertionError.class,
            java.lang.Boolean.class,
            java.lang.Byte.class,
            java.lang.Character.class,
            java.lang.CharSequence.class,
            java.lang.Class.class,
            java.lang.ClassCastException.class,
            java.lang.ClassLoader.class,
            java.lang.ClassNotFoundException.class,
            java.lang.Cloneable.class,
            java.lang.Comparable.class,
            java.lang.Deprecated.class,
            java.lang.Double.class,
            java.lang.Enum.class,
            java.lang.Error.class,
            java.lang.Exception.class,
            java.lang.Float.class,
            java.lang.FunctionalInterface.class,
            java.lang.IllegalAccessException.class,
            java.lang.IllegalArgumentException.class,
            java.lang.IllegalStateException.class,
            java.lang.IndexOutOfBoundsException.class,
            java.lang.Integer.class,
            java.lang.InternalError.class,
            java.lang.InterruptedException.class,
            java.lang.Iterable.class,
            java.lang.LinkageError.class,
            java.lang.Long.class,
            java.lang.Math.class,
            java.lang.NegativeArraySizeException.class,
            java.lang.NoClassDefFoundError.class,
            java.lang.NoSuchFieldError.class,
            java.lang.NoSuchFieldException.class,
            java.lang.NoSuchMethodError.class,
            java.lang.NoSuchMethodException.class,
            java.lang.NullPointerException.class,
            java.lang.Number.class,
            java.lang.NumberFormatException.class,
            java.lang.Object.class,
            java.lang.OutOfMemoryError.class,
            java.lang.Override.class,
            java.lang.Package.class,
            java.lang.Process.class,
            java.lang.ReflectiveOperationException.class,
            java.lang.Runnable.class,
            java.lang.Runtime.class,
            java.lang.RuntimeException.class,
            java.lang.SafeVarargs.class,
            java.lang.Short.class,
            java.lang.StackOverflowError.class,
            java.lang.String.class,
            java.lang.StringBuffer.class,
            java.lang.StringBuilder.class,
            java.lang.SuppressWarnings.class,
            java.lang.System.class,
            java.lang.Thread.class,
            java.lang.Throwable.class,
            java.lang.Void.class
        );

        Map<String, JTypeDeclSymbol> theJavaLang = new HashMap<>();


        for (Class<?> aClass : classes) {

            JClassSymbol reference = ReflectSymInternals.createSharedSym(aClass);

            theJavaLang.put(aClass.getSimpleName(), reference);
            theJavaLang.put(aClass.getCanonicalName(), reference);
        }
        COMMON_JAVA_LANG = Collections.unmodifiableMap(theJavaLang);
    }


    /**
     * Constructor with just the parent table.
     *
     * @param parent Parent table
     * @param helper Resolve helper
     */
    JavaLangSymbolTable(JSymbolTable parent, SymbolTableHelper helper, ASTCompilationUnit acu) {
        super(parent, helper);
        fileNode = acu;
    }


    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        JTypeDeclSymbol got = COMMON_JAVA_LANG.get(simpleName);
        if (got == null && simpleName.indexOf('.') < 0) {
            got = loadClassIgnoreFailure("java.lang." + simpleName);
        }

        if (got == null) {
            return null;
        }

        return new ClassResolveResult(got, this, fileNode);
    }


}
