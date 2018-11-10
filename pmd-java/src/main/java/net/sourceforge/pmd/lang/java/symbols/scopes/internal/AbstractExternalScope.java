/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Abstract class for scopes whose declarations are
 * outside the currently analysed file, i.e., they proceed
 * by reflection (and need a classLoader). Includes import
 * scopes, package scope.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractExternalScope extends AbstractJScope {

    /** Classloader with analysis classpath. */
    protected final PMDASMClassLoader classLoader;
    /** Package name of the current compilation unit, used to check for accessibility. */
    protected final String thisPackage;


    /**
     * Constructor with the parent scope and the auxclasspath classloader.
     * Used to build the top-level scope.
     *
     * @param parent      Parent scope
     * @param classLoader ClassLoader used to resolve e.g. import-on-demand
     * @param thisPackage Package name of the current compilation unit, used to check for accessibility
     */
    AbstractExternalScope(JScope parent, PMDASMClassLoader classLoader, String thisPackage) {
        super(parent);
        this.classLoader = classLoader;
        this.thisPackage = thisPackage;
    }


    /** Gets a logger, used to have a different logger for different scopes. */
    protected abstract Logger getLogger();


    protected final boolean isAccessible(Class<?> c) {
        return isAccessible(c.getModifiers(), c.getPackage().getName());
    }


    protected final boolean isAccessible(Member member) {
        return isAccessible(member.getModifiers(), member.getDeclaringClass().getPackage().getName());
    }


    /**
     * Returns true if a member is accessible from the current ACU.
     * Returns true for protected members, wherever we are, which is an approximation
     * but won't cause problems in practice.
     */
    private boolean isAccessible(int modifiers, String memberPackageName) {
        if (Modifier.isPublic(modifiers)) {
            return true;
        } else if (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers)) {
            // We consider protected members inaccessible here, which is an first approximation.
            // In the ACU, the name is accessible only inside classes that inherit from the declaring class.
            // But inheriting from a class makes its static members accessible via simple name too.
            // So this will actually be picked up by InheritedScope when in the subclass.
            // If not in the subclass, the compilation would have failed.
            return false;
        } else {
            // then it's package private
            return thisPackage.equals(memberPackageName);
        }
    }


    /**
     * Tries to load a class and logs it if it is not found.
     *
     * @param fqcn Binary name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    protected final Class<?> loadClass(String fqcn) {
        try {
            return classLoader.loadClass(fqcn);
            // ClassTypeResolver used to just ignore ClassNotFoundException, was there a reason for that?
        } catch (ClassNotFoundException | LinkageError e2) {
            if (getLogger().isLoggable(Level.FINE)) {
                getLogger().log(Level.FINE, "Failed loading class " + fqcn + "with an incomplete classpath.", e2);
            }
            return null;
        }
    }

    /* TEST CASE

    private static class Baz {
        static void baz() {

        }


        static void foo() {

        }
    }


    private static class Foo extends Baz {
        static void foo() {

        }
    }

    private class Bar extends Foo {
        {
            foo(); // accessible
        }
    }
    */


}
