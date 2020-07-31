/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

/**
 * Helper class to analyze {@link ASTImportDeclaration}s.
 */
public class ImportWrapper {
    private static final Logger LOG = Logger.getLogger(ImportWrapper.class.getName());

    private final ASTImportDeclaration node;
    private final String name;
    private final String fullname;
    private final boolean isStaticDemand;
    private final Set<String> allStaticDemands;

    public ImportWrapper(String fullname, String name) {
        this(fullname, name, null);
    }

    public ImportWrapper(String fullname, String name, ASTImportDeclaration node) {
        this(fullname, name, node, false, null);
    }

    public ImportWrapper(String fullname, String name, ASTImportDeclaration node, boolean isStaticDemand, RuleContext ctx) {
        this.fullname = fullname;
        this.name = name;
        this.node = node;
        this.isStaticDemand = isStaticDemand;
        this.allStaticDemands = collectStaticFieldsAndMethods(node, ctx);

    }

    /**
     * @param node
     */
    private Set<String> collectStaticFieldsAndMethods(ASTImportDeclaration node, RuleContext ctx) {
        if (!this.isStaticDemand || node == null || node.getType() == null) {
            return Collections.emptySet();
        }

        try {
            Set<String> names = new HashSet<>();
            Class<?> type = node.getType();
            // consider static fields, public and non-public
            for (Field f : type.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    names.add(f.getName());
                }
            }
            // and methods, too
            for (Method m : type.getDeclaredMethods()) {
                if (Modifier.isStatic(m.getModifiers())) {
                    names.add(m.getName());
                }
            }
            return names;
        } catch (LinkageError e) {
            String filename = ctx != null ? String.valueOf(ctx.getSourceCodeFile()) : "n/a";

            if (ctx != null) {
                ctx.getReport().addError(new ProcessingError(e, filename));
            } else {
                LOG.log(Level.WARNING, "Error while processing file " + filename, e);
            }
            return Collections.emptySet();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != ImportWrapper.class) {
            return false;
        }

        ImportWrapper i = (ImportWrapper) other;
        if (isStaticDemand != i.isStaticDemand) {
            return false;
        }
        if (name == null) {
            return fullname.equals(i.getFullName());
        }
        return name.equals(i.getName());
    }

    public boolean matches(ImportWrapper i) {
        if (isStaticDemand) {
            if (allStaticDemands.contains(i.fullname)) {
                return true;
            }
        }
        if (name == null && i.getName() == null) {
            return i.getFullName().equals(fullname);
        }
        return i.getName().equals(name);
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return Objects.hash(fullname, isStaticDemand);
        }
        return Objects.hash(name, isStaticDemand);
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullname;
    }

    public Node getNode() {
        return node;
    }

    public boolean isStaticOnDemand() {
        return isStaticDemand;
    }

    @Override
    public String toString() {
        return "Import[name=" + name + ",fullname=" + fullname + ",static*=" + isStaticDemand + ']';
    }
}
