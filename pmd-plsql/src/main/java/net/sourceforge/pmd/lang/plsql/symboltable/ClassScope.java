/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode;
import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ClassScope extends AbstractScope {
    private static final Logger LOGGER = Logger.getLogger(ClassScope.class.getName());

    // FIXME - this breaks given sufficiently nested code
    private static ThreadLocal<Integer> anonymousInnerClassCounter = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return Integer.valueOf(1);
        }
    };

    private String className;

    public ClassScope(String className) {
        this.className = AbstractPLSQLNode.getCanonicalImage(className);
        anonymousInnerClassCounter.set(Integer.valueOf(1));
    }

    /**
     * This is only for anonymous inner classes.
     *
     * <p>FIXME - should have name like Foo$1, not Anonymous$1 to get this working
     * right, the parent scope needs to be passed in when instantiating a
     * ClassScope</p>
     */
    public ClassScope() {
        // this.className = getParent().getEnclosingClassScope().getClassName()
        // + "$" + String.valueOf(anonymousInnerClassCounter);
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence) occ;
        Set<NameDeclaration> declarations = findVariableHere(occurrence);
        Map<MethodNameDeclaration, List<NameOccurrence>> methodNames = getMethodDeclarations();
        if (!declarations.isEmpty() && occurrence.isMethodOrConstructorInvocation()) {
            for (NameDeclaration decl : declarations) {
                List<NameOccurrence> nameOccurrences = methodNames.get(decl);
                if (nameOccurrences == null) {
                    // TODO may be a class name: Foo.this.super();
                } else {
                    nameOccurrences.add(occurrence);
                    Node n = occurrence.getLocation();
                    if (n instanceof ASTName) {
                        ((ASTName) n).setNameDeclaration(decl);
                    } // TODO what to do with PrimarySuffix case?
                }
            }
        } else if (!declarations.isEmpty() && !occurrence.isThisOrSuper()) {
            Map<VariableNameDeclaration, List<NameOccurrence>> variableNames = getVariableDeclarations();
            for (NameDeclaration decl : declarations) {
                List<NameOccurrence> nameOccurrences = variableNames.get(decl);
                if (nameOccurrences == null) {
                    // TODO may be a class name
                } else {
                    nameOccurrences.add(occurrence);
                    Node n = occurrence.getLocation();
                    if (n instanceof ASTName) {
                        ((ASTName) n).setNameDeclaration(decl);
                    } // TODO what to do with PrimarySuffix case?
                }
            }
        }
        return declarations;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    public Map<MethodNameDeclaration, List<NameOccurrence>> getMethodDeclarations() {
        return getDeclarations(MethodNameDeclaration.class);
    }

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return getDeclarations(ClassNameDeclaration.class);
    }

    public ClassScope getEnclosingClassScope() {
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    protected Set<NameDeclaration> findVariableHere(PLSQLNameOccurrence occurrence) {
        Set<NameDeclaration> result = new HashSet<>();
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableDeclarations.isEmpty() && methodDeclarations.isEmpty()) {
                // this could happen if you do this:
                // public class Foo {
                // private String x = super.toString();
                // }
                return result;
            }
            // return any name declaration, since all we really want is to get
            // the scope
            // for example, if there's a
            // public class Foo {
            // private static final int X = 2;
            // private int y = Foo.X;
            // }
            // we'll look up Foo just to get a handle to the class scope
            // and then we'll look up X.
            if (!variableDeclarations.isEmpty()) {
                result.add(variableDeclarations.keySet().iterator().next());
                return result;
            }
            result.add(methodDeclarations.keySet().iterator().next());
            return result;
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd : methodDeclarations.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    int args = occurrence.getArgumentCount();
                    if (args == mnd.getParameterCount() || mnd.isVarargs() && args >= mnd.getParameterCount() - 1) {
                        // FIXME if several methods have the same name
                        // and parameter count, only one will get caught here
                        // we need to make some attempt at type lookup and
                        // discrimination
                        // or, failing that, mark this as a usage of all those
                        // methods
                        result.add(mnd);
                    }
                }
            }
            return result;
        }

        List<String> images = new ArrayList<>();
        images.add(occurrence.getImage());

        if (null == occurrence.getImage()) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("occurrence==" + occurrence.toString() + "with Argumanet Count == "
                        + occurrence.getArgumentCount() + " for className=" + className);
            }
        }

        if (occurrence.getImage().startsWith(className)) {
            images.add(clipClassName(occurrence.getImage()));
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }
        return result;
    }

    @Override
    public String toString() {
        String res = "ClassScope (" + className + "): ";
        Map<ClassNameDeclaration, List<NameOccurrence>> classNames = getClassDeclarations();
        Map<MethodNameDeclaration, List<NameOccurrence>> methodNames = getMethodDeclarations();
        Map<VariableNameDeclaration, List<NameOccurrence>> variableNames = getVariableDeclarations();
        if (!classNames.isEmpty()) {
            res += "(" + classNames.keySet() + ")";
        }
        if (!methodNames.isEmpty()) {
            for (MethodNameDeclaration mnd : methodNames.keySet()) {
                res += mnd.toString();
                int usages = methodNames.get(mnd).size();
                res += "(begins at line " + mnd.getNode().getBeginLine() + ", " + usages + " usages)";
                res += ",";
            }
        }
        if (!variableNames.isEmpty()) {
            res += "(" + variableNames.keySet() + ")";
        }
        return res;
    }

    private String clipClassName(String s) {
        return s.substring(s.indexOf('.') + 1);
    }
}
