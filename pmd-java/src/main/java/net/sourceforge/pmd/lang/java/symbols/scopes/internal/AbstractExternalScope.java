/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

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
public abstract class AbstractExternalScope extends AbstractJScope {

    /** Classloader. */
    protected final PMDASMClassLoader classLoader;


    /**
     * Constructor with the parent scope and the auxclasspath classloader.
     * Used to build the top-level scope.
     *
     * @param parent Parent scope
     */
    AbstractExternalScope(JScope parent, ClassLoader classLoader) {
        super(parent);
        this.classLoader = PMDASMClassLoader.getInstance(classLoader);
    }


    AbstractExternalScope(JScope parent) {
        super(parent);
        this.classLoader = ((AbstractExternalScope) parent).classLoader;
    }


    /** Gets a logger, used to have a different logger for different scopes. */
    protected abstract Logger getLogger();


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
}
