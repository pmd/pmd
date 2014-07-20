/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * This scope represents one Java class.
 * It can have variable declarations, method declarations and inner class declarations.
 */
public class ClassScope extends AbstractJavaScope {

    // FIXME - this breaks given sufficiently nested code
    private static ThreadLocal<Integer> anonymousInnerClassCounter = new ThreadLocal<Integer>() {
        protected Integer initialValue() { return Integer.valueOf(1); }
    };

    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousInnerClassCounter.set(Integer.valueOf(1));
    }

    /**
     * This is only for anonymous inner classes
     * <p/>
     * FIXME - should have name like Foo$1, not Anonymous$1
     * to get this working right, the parent scope needs
     * to be passed in when instantiating a ClassScope
     */
    public ClassScope() {
        //this.className = getParent().getEnclosingClassScope().getClassName() + "$" + String.valueOf(anonymousInnerClassCounter);
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
    }

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return getDeclarations(ClassNameDeclaration.class);
    }

    public Map<MethodNameDeclaration, List<NameOccurrence>> getMethodDeclarations() {
        return getDeclarations(MethodNameDeclaration.class);
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    public NameDeclaration addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence)occurrence;
        NameDeclaration decl = findVariableHere(javaOccurrence);
        if (decl != null && (javaOccurrence.isMethodOrConstructorInvocation() || javaOccurrence.isMethodReference())) {
            List<NameOccurrence> nameOccurrences = getMethodDeclarations().get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name: Foo.this.super();
            } else {
                nameOccurrences.add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }

        } else if (decl != null && !javaOccurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name

                // search inner classes
                for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                    Scope innerClassScope = innerClass.getScope();
                    if (innerClassScope.contains(javaOccurrence)) {
                        innerClassScope.addNameOccurrence(javaOccurrence);
                    }
                }
            } else {
                nameOccurrences.add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return decl;
    }

    public String getClassName() {
        return this.className;
    }

    protected NameDeclaration findVariableHere(JavaNameOccurrence occurrence) {
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        if (occurrence.isThisOrSuper() ||
                (occurrence.getImage() != null && occurrence.getImage().equals(className))) {
            if (variableDeclarations.isEmpty() && methodDeclarations.isEmpty()) {
                // this could happen if you do this:
                // public class Foo {
                //  private String x = super.toString();
                // }
                return null;
            }
            // return any name declaration, since all we really want is to get the scope
            // for example, if there's a
            // public class Foo {
            //  private static final int X = 2;
            //  private int y = Foo.X;
            // }
            // we'll look up Foo just to get a handle to the class scope
            // and then we'll look up X.
            if (!variableDeclarations.isEmpty()) {
                return variableDeclarations.keySet().iterator().next();
            }
            return methodDeclarations.keySet().iterator().next();
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd: methodDeclarations.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    List<String> parameterTypes = determineParameterTypes(mnd);
                    List<String> argumentTypes = determineArgumentTypes(occurrence, parameterTypes);

                    if (!mnd.isVarargs()
                            && occurrence.getArgumentCount() == mnd.getParameterCount()
                            && parameterTypes.equals(argumentTypes)) {
                        return mnd;
                    } else if (mnd.isVarargs()) {
                        int varArgIndex = parameterTypes.size() - 1;
                        String varArgType = parameterTypes.get(varArgIndex);
                        if (parameterTypes.subList(0, varArgIndex).equals(argumentTypes.subList(0, varArgIndex))) {
                            boolean sameType = true;
                            for (int i = varArgIndex; i < argumentTypes.size(); i++) {
                                if (!varArgType.equals(argumentTypes.get(i))) {
                                    sameType = false;
                                    break;
                                }
                            }
                            if (sameType) {
                                return mnd;
                            }
                        }
                    }
                }
            }
            return null;
        }
        if (occurrence.isMethodReference()) {
            for (MethodNameDeclaration mnd: methodDeclarations.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    return mnd;
                }
            }
            return null;
        }

        List<String> images = new ArrayList<String>();
        if (occurrence.getImage() != null) {
            images.add(occurrence.getImage());
            if (occurrence.getImage().startsWith(className)) {
                images.add(clipClassName(occurrence.getImage()));
            }
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableDeclarations.keySet().iterator());
        NameDeclaration result = finder.getDecl();

        // search inner classes
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (result == null && !classDeclarations.isEmpty()) {
            for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                Applier.apply(finder, innerClass.getScope().getDeclarations().keySet().iterator());
                result = finder.getDecl();
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Provide a list of types of the parameters of the given method declaration.
     * The types are simple type images. 
     * @param mnd the method declaration.
     * @return List of types
     */
    private List<String> determineParameterTypes(MethodNameDeclaration mnd) {
        List<String> parameterTypes = new ArrayList<String>();
        List<ASTFormalParameter> parameters = mnd.getMethodNameDeclaratorNode().findDescendantsOfType(ASTFormalParameter.class);
        for (ASTFormalParameter p : parameters) {
            parameterTypes.add(p.getTypeNode().getTypeImage());
        }
        return parameterTypes;
    }

    /**
     * Provide a list of types of the arguments of the given method call.
     * The types are simple type images. If the argument type cannot be determined (e.g. because it is itself
     * the result of a method call), the parameter type is used - so it is assumed, it is of the correct type.
     * This might cause confusion when methods are overloaded.
     * @param occurrence the method call
     * @param parameterTypes the parameter types of the called method
     * @return the list of argument types
     */
    private List<String> determineArgumentTypes(JavaNameOccurrence occurrence, List<String> parameterTypes) {
        final String unknown_type = "unknown";
        List<String> argumentTypes = new ArrayList<String>();
        ASTArgumentList arguments = occurrence.getLocation().jjtGetParent().jjtGetParent().getFirstDescendantOfType(ASTArgumentList.class);
        if (arguments != null) {
            for (int i = 0; i < arguments.jjtGetNumChildren(); i++) {
                ASTName name = arguments.jjtGetChild(i).getFirstDescendantOfType(ASTName.class);
                String typeImage = unknown_type;
                if (name != null) {
                    Scope s = name.getScope();
                    while (s != null) {
                        if (s.contains(new JavaNameOccurrence(name, name.getImage()))) {
                            break;
                        }
                        s = s.getParent();
                    }
                    if (s != null) {
                        Map<VariableNameDeclaration, List<NameOccurrence>> vars = s.getDeclarations(VariableNameDeclaration.class);
                        for (VariableNameDeclaration d : vars.keySet()) {
                            if (d.getImage().equals(name.getImage())) {
                                typeImage = d.getTypeImage();
                                break;
                            }
                        }
                    }
                } else {
                    ASTLiteral literal = arguments.jjtGetChild(i).getFirstDescendantOfType(ASTLiteral.class);
                    if (literal != null) {
                        if (literal.isCharLiteral()) {
                            typeImage = "char";
                        } else if (literal.isStringLiteral()) {
                            typeImage = "String";
                        } else if (literal.isFloatLiteral()) {
                            typeImage = "float";
                        } else if (literal.isIntLiteral()) {
                            typeImage = "int";
                        }
                    }
                }
                argumentTypes.add(typeImage);
            }
        }
        // replace the unknown type with the correct parameter type of the method.
        // in case the argument is itself a method call, we can't determine the result type of the called
        // method. Therefore the parameter type is used.
        // This might cause confusion, if method overloading is used.
        for (int i = 0; i < argumentTypes.size() && i < parameterTypes.size(); i++) {
            if (unknown_type.equals(argumentTypes.get(i))) {
                argumentTypes.set(i, parameterTypes.get(i));
            }
        }
        return argumentTypes;
    }

    public String toString() {
        StringBuilder res = new StringBuilder("ClassScope (").append(className).append("): ");
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (classDeclarations.isEmpty()) {
            res.append("Inner Classes ").append(glomNames(classDeclarations.keySet())).append("; ");
        }
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        if (!methodDeclarations.isEmpty()) {
            for (MethodNameDeclaration mnd: methodDeclarations.keySet()) {
                res.append(mnd.toString());
                int usages = methodDeclarations.get(mnd).size();
                res.append("(begins at line ").append(mnd.getNode().getBeginLine()).append(", ").append(usages).append(" usages)");
                res.append(", ");
            }
        }
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        if (!variableDeclarations.isEmpty()) {
            res.append("Variables ").append(glomNames(variableDeclarations.keySet()));
        }
        return res.toString();
    }

    private String clipClassName(String s) {
        return s.substring(s.indexOf('.') + 1);
    }
}
