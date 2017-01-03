/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaParserTreeConstants;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * This scope represents one Java class. It can have variable declarations,
 * method declarations and inner class declarations.
 */
public class ClassScope extends AbstractJavaScope {

    private static final Set<String> PRIMITIVE_TYPES;

    static {
        PRIMITIVE_TYPES = new HashSet<>();
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("double");
    }

    // FIXME - this breaks given sufficiently nested code
    private static ThreadLocal<Integer> anonymousInnerClassCounter = new ThreadLocal<Integer>() {
        protected Integer initialValue() {
            return Integer.valueOf(1);
        }
    };

    private final String className;

    private boolean isEnum;

    public ClassScope(final String className) {
        this.className = Objects.requireNonNull(className);
        anonymousInnerClassCounter.set(Integer.valueOf(1));
    }

    /**
     * This is only for anonymous inner classes
     * <p/>
     * FIXME - should have name like Foo$1, not Anonymous$1 to get this working
     * right, the parent scope needs to be passed in when instantiating a
     * ClassScope
     */
    public ClassScope() {
        // this.className = getParent().getEnclosingClassScope().getClassName()
        // + "$" + String.valueOf(anonymousInnerClassCounter);
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
    }

    public void setIsEnum(boolean isEnum) {
        this.isEnum = isEnum;
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

    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence) occurrence;
        Set<NameDeclaration> declarations = findVariableHere(javaOccurrence);
        if (!declarations.isEmpty()
                && (javaOccurrence.isMethodOrConstructorInvocation() || javaOccurrence.isMethodReference())) {
            for (NameDeclaration decl : declarations) {
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
            }
        } else if (!declarations.isEmpty() && !javaOccurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
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
        }
        return declarations;
    }

    public String getClassName() {
        return this.className;
    }

    protected Set<NameDeclaration> findVariableHere(JavaNameOccurrence occurrence) {
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        if (occurrence.isThisOrSuper() || className.equals(occurrence.getImage())) {
            if (variableDeclarations.isEmpty() && methodDeclarations.isEmpty()) {
                // this could happen if you do this:
                // public class Foo {
                // private String x = super.toString();
                // }
                return Collections.emptySet();
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
                return Collections.<NameDeclaration>singleton(variableDeclarations.keySet().iterator().next());
            }
            return Collections.<NameDeclaration>singleton(methodDeclarations.keySet().iterator().next());
        }

        Set<NameDeclaration> result = new HashSet<>();
        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd : methodDeclarations.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    List<TypedNameDeclaration> parameterTypes = determineParameterTypes(mnd);
                    List<TypedNameDeclaration> argumentTypes = determineArgumentTypes(occurrence, parameterTypes);

                    if (!mnd.isVarargs() && occurrence.getArgumentCount() == mnd.getParameterCount()
                            && (!getEnclosingScope(SourceFileScope.class).hasAuxclasspath()
                                    || parameterTypes.equals(argumentTypes))) {
                        result.add(mnd);
                    } else if (mnd.isVarargs()) {
                        int varArgIndex = parameterTypes.size() - 1;
                        TypedNameDeclaration varArgType = parameterTypes.get(varArgIndex);

                        // first parameter is varArg, calling method might have
                        // 0 or more arguments
                        // or the calling method has enough arguments to fill in
                        // the parameters before the vararg
                        if ((varArgIndex == 0 || argumentTypes.size() >= varArgIndex)
                                && (!getEnclosingScope(SourceFileScope.class).hasAuxclasspath() || parameterTypes
                                        .subList(0, varArgIndex).equals(argumentTypes.subList(0, varArgIndex)))) {

                            if (!getEnclosingScope(SourceFileScope.class).hasAuxclasspath()) {
                                result.add(mnd);
                            }

                            boolean sameType = true;
                            for (int i = varArgIndex; i < argumentTypes.size(); i++) {
                                if (!varArgType.equals(argumentTypes.get(i))) {
                                    sameType = false;
                                    break;
                                }
                            }
                            if (sameType) {
                                result.add(mnd);
                            }
                        }
                    }
                }
            }
            if (isEnum && "valueOf".equals(occurrence.getImage())) {
                result.add(createBuiltInMethodDeclaration("valueOf", "String"));
            }
            return result;
        }
        if (occurrence.isMethodReference()) {
            for (MethodNameDeclaration mnd : methodDeclarations.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    result.add(mnd);
                }
            }
            return result;
        }

        List<String> images = new ArrayList<>();
        if (occurrence.getImage() != null) {
            images.add(occurrence.getImage());
            if (occurrence.getImage().startsWith(className)) {
                images.add(clipClassName(occurrence.getImage()));
            }
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableDeclarations.keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }

        // search inner classes
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (result.isEmpty() && !classDeclarations.isEmpty()) {
            for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                Applier.apply(finder, innerClass.getScope().getDeclarations().keySet().iterator());
                if (finder.getDecl() != null) {
                    result.add(finder.getDecl());
                }
            }
        }
        return result;
    }

    /**
     * Creates a fake method name declaration for built-in methods from Java
     * like the Enum Method "valueOf".
     *
     * @param methodName
     *            the method name
     * @param parameterTypes
     *            the reference types of each parameter of the method
     * @return a method name declaration
     */
    private MethodNameDeclaration createBuiltInMethodDeclaration(final String methodName,
            final String... parameterTypes) {
        ASTMethodDeclaration methodDeclaration = new ASTMethodDeclaration(JavaParserTreeConstants.JJTMETHODDECLARATION);
        methodDeclaration.setPublic(true);
        methodDeclaration.setScope(this);

        ASTMethodDeclarator methodDeclarator = new ASTMethodDeclarator(JavaParserTreeConstants.JJTMETHODDECLARATOR);
        methodDeclarator.setImage(methodName);
        methodDeclarator.setScope(this);

        ASTFormalParameters formalParameters = new ASTFormalParameters(JavaParserTreeConstants.JJTFORMALPARAMETERS);
        formalParameters.setScope(this);

        methodDeclaration.jjtAddChild(methodDeclarator, 0);
        methodDeclarator.jjtSetParent(methodDeclaration);
        methodDeclarator.jjtAddChild(formalParameters, 0);
        formalParameters.jjtSetParent(methodDeclarator);

        for (int i = 0; i < parameterTypes.length; i++) {
            ASTFormalParameter formalParameter = new ASTFormalParameter(JavaParserTreeConstants.JJTFORMALPARAMETER);
            formalParameters.jjtAddChild(formalParameter, i);
            formalParameter.jjtSetParent(formalParameters);

            ASTType type = new ASTType(JavaParserTreeConstants.JJTTYPE);
            formalParameter.jjtAddChild(type, 0);
            type.jjtSetParent(formalParameter);

            ASTVariableDeclaratorId variableDeclaratorId = new ASTVariableDeclaratorId(
                    JavaParserTreeConstants.JJTVARIABLEDECLARATORID);
            variableDeclaratorId.setImage("arg" + i);
            formalParameter.jjtAddChild(variableDeclaratorId, 1);
            variableDeclaratorId.jjtSetParent(formalParameter);

            if (PRIMITIVE_TYPES.contains(parameterTypes[i])) {
                ASTPrimitiveType primitiveType = new ASTPrimitiveType(JavaParserTreeConstants.JJTPRIMITIVETYPE);
                primitiveType.setImage(parameterTypes[i]);
                type.jjtAddChild(primitiveType, 0);
                primitiveType.jjtSetParent(type);
            } else {
                ASTReferenceType referenceType = new ASTReferenceType(JavaParserTreeConstants.JJTREFERENCETYPE);
                type.jjtAddChild(referenceType, 0);
                referenceType.jjtSetParent(type);

                // TODO : this could actually be a primitive array...
                ASTClassOrInterfaceType classOrInterfaceType = new ASTClassOrInterfaceType(
                        JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);
                classOrInterfaceType.setImage(parameterTypes[i]);
                referenceType.jjtAddChild(classOrInterfaceType, 0);
                classOrInterfaceType.jjtSetParent(referenceType);
            }
        }

        MethodNameDeclaration mnd = new MethodNameDeclaration(methodDeclarator);
        return mnd;
    }

    /**
     * Provide a list of types of the parameters of the given method
     * declaration. The types are simple type images.
     * 
     * @param mnd
     *            the method declaration.
     * @return List of types
     */
    private List<TypedNameDeclaration> determineParameterTypes(MethodNameDeclaration mnd) {
        List<TypedNameDeclaration> parameterTypes = new ArrayList<>();
        List<ASTFormalParameter> parameters = mnd.getMethodNameDeclaratorNode()
                .findDescendantsOfType(ASTFormalParameter.class);
        for (ASTFormalParameter p : parameters) {
            String typeImage = p.getTypeNode().getTypeImage();
            // typeImage might be qualified/unqualified. If it refers to a type,
            // defined in the same toplevel class,
            // we should normalize the name here.
            // It might also refer to a type, that is imported.
            typeImage = qualifyTypeName(typeImage);
            Node declaringNode = getEnclosingScope(SourceFileScope.class).getQualifiedTypeNames().get(typeImage);
            Class<?> resolvedType = this.getEnclosingScope(SourceFileScope.class).resolveType(typeImage);
            if (resolvedType == null) {
                resolvedType = resolveGenericType(p, typeImage);
            }
            parameterTypes.add(new SimpleTypedNameDeclaration(typeImage, resolvedType, determineSuper(declaringNode)));
        }
        return parameterTypes;
    }

    private String qualifyTypeName(String typeImage) {
        if (typeImage == null) {
            return null;
        }

        Set<String> qualifiedNames = new LinkedHashSet<>();
        final SourceFileScope fileScope = getEnclosingScope(SourceFileScope.class);
        qualifiedNames.addAll(fileScope.getQualifiedTypeNames().keySet());
        qualifiedNames.addAll(fileScope.getExplicitImports());

        int nameLength = typeImage.length();

        for (String qualified : qualifiedNames) {
            int fullLength = qualified.length();
            if (qualified.endsWith(typeImage)
                    && (fullLength == nameLength || qualified.charAt(fullLength - nameLength - 1) == '.')) {
                return qualified;
            }
        }
        return typeImage;
    }

    /**
     * Provide a list of types of the arguments of the given method call. The
     * types are simple type images. If the argument type cannot be determined
     * (e.g. because it is itself the result of a method call), the parameter
     * type is used - so it is assumed, it is of the correct type. This might
     * cause confusion when methods are overloaded.
     * 
     * @param occurrence
     *            the method call
     * @param parameterTypes
     *            the parameter types of the called method
     * @return the list of argument types
     */
    private List<TypedNameDeclaration> determineArgumentTypes(JavaNameOccurrence occurrence,
            List<TypedNameDeclaration> parameterTypes) {
        List<TypedNameDeclaration> argumentTypes = new ArrayList<>();
        Map<String, Node> qualifiedTypeNames = getEnclosingScope(SourceFileScope.class).getQualifiedTypeNames();
        ASTArgumentList arguments = null;
        Node nextSibling = null;
        if (occurrence.getLocation() instanceof ASTPrimarySuffix) {
            nextSibling = getNextSibling(occurrence.getLocation());
        } else {
            nextSibling = getNextSibling(occurrence.getLocation().jjtGetParent());
        }
        if (nextSibling != null) {
            arguments = nextSibling.getFirstDescendantOfType(ASTArgumentList.class);
        }

        if (arguments != null) {
            for (int i = 0; i < arguments.jjtGetNumChildren(); i++) {
                Node argument = arguments.jjtGetChild(i);
                Node child = null;
                boolean isMethodCall = false;
                if (argument.jjtGetNumChildren() > 0 && argument.jjtGetChild(0).jjtGetNumChildren() > 0
                        && argument.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() > 0) {
                    child = argument.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
                    isMethodCall = argument.jjtGetChild(0).jjtGetNumChildren() > 1;
                }
                TypedNameDeclaration type = null;
                if (child instanceof ASTName && !isMethodCall) {
                    ASTName name = (ASTName) child;
                    Scope s = name.getScope();
                    final JavaNameOccurrence nameOccurrence = new JavaNameOccurrence(name, name.getImage());
                    while (s != null) {
                        if (s.contains(nameOccurrence)) {
                            break;
                        }
                        s = s.getParent();
                    }
                    if (s != null) {
                        Map<VariableNameDeclaration, List<NameOccurrence>> vars = s
                                .getDeclarations(VariableNameDeclaration.class);
                        for (VariableNameDeclaration d : vars.keySet()) {
                            // in case of simple lambda expression, the type
                            // might be unknown
                            if (d.getImage().equals(name.getImage()) && d.getTypeImage() != null) {
                                String typeName = d.getTypeImage();
                                typeName = qualifyTypeName(typeName);
                                Node declaringNode = qualifiedTypeNames.get(typeName);
                                type = new SimpleTypedNameDeclaration(typeName,
                                        this.getEnclosingScope(SourceFileScope.class).resolveType(typeName),
                                        determineSuper(declaringNode));
                                break;
                            }
                        }
                    }
                } else if (child instanceof ASTLiteral) {
                    ASTLiteral literal = (ASTLiteral) child;
                    if (literal.isCharLiteral()) {
                        type = new SimpleTypedNameDeclaration("char", literal.getType());
                    } else if (literal.isStringLiteral()) {
                        type = new SimpleTypedNameDeclaration("String", literal.getType());
                    } else if (literal.isFloatLiteral()) {
                        type = new SimpleTypedNameDeclaration("float", literal.getType());
                    } else if (literal.isDoubleLiteral()) {
                        type = new SimpleTypedNameDeclaration("double", literal.getType());
                    } else if (literal.isIntLiteral()) {
                        type = new SimpleTypedNameDeclaration("int", literal.getType());
                    } else if (literal.isLongLiteral()) {
                        type = new SimpleTypedNameDeclaration("long", literal.getType());
                    } else if (literal.jjtGetNumChildren() == 1
                            && literal.jjtGetChild(0) instanceof ASTBooleanLiteral) {
                        type = new SimpleTypedNameDeclaration("boolean", Boolean.TYPE);
                    }
                } else if (child instanceof ASTAllocationExpression
                        && child.jjtGetChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType classInterface = (ASTClassOrInterfaceType) child.jjtGetChild(0);
                    type = convertToSimpleType(classInterface);
                }
                if (type == null && !parameterTypes.isEmpty()) {
                    // replace the unknown type with the correct parameter type
                    // of the method.
                    // in case the argument is itself a method call, we can't
                    // determine the result type of the called
                    // method. Therefore the parameter type is used.
                    // This might cause confusion, if method overloading is
                    // used.

                    // the method might be vararg, so, there might be more
                    // arguments than parameterTypes
                    if (parameterTypes.size() > i) {
                        type = parameterTypes.get(i);
                    } else {
                        // last parameter is the vararg type
                        type = parameterTypes.get(parameterTypes.size() - 1);
                    }
                }
                if (type != null && type.getType() == null) {
                    Class<?> typeBound = resolveGenericType(argument, type.getTypeImage());
                    if (typeBound != null) {
                        type = new SimpleTypedNameDeclaration(type.getTypeImage(), typeBound);
                    }
                }
                argumentTypes.add(type);
            }
        }
        return argumentTypes;
    }

    private SimpleTypedNameDeclaration determineSuper(Node declaringNode) {
        SimpleTypedNameDeclaration result = null;
        if (declaringNode instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration classDeclaration = (ASTClassOrInterfaceDeclaration) declaringNode;
            ASTImplementsList implementsList = classDeclaration.getFirstChildOfType(ASTImplementsList.class);
            if (implementsList != null) {
                List<ASTClassOrInterfaceType> types = implementsList.findChildrenOfType(ASTClassOrInterfaceType.class);
                SimpleTypedNameDeclaration type = convertToSimpleType(types);
                result = type;
            }
            ASTExtendsList extendsList = classDeclaration.getFirstChildOfType(ASTExtendsList.class);
            if (extendsList != null) {
                List<ASTClassOrInterfaceType> types = extendsList.findChildrenOfType(ASTClassOrInterfaceType.class);
                SimpleTypedNameDeclaration type = convertToSimpleType(types);
                if (result == null) {
                    result = type;
                } else {
                    result.addNext(type);
                }
            }
        }
        return result;
    }

    private SimpleTypedNameDeclaration convertToSimpleType(List<ASTClassOrInterfaceType> types) {
        SimpleTypedNameDeclaration result = null;
        for (ASTClassOrInterfaceType t : types) {
            SimpleTypedNameDeclaration type = convertToSimpleType(t);
            if (result == null) {
                result = type;
            } else {
                result.addNext(type);
            }
        }
        return result;
    }

    private SimpleTypedNameDeclaration convertToSimpleType(ASTClassOrInterfaceType t) {
        String typeImage = t.getImage();
        typeImage = qualifyTypeName(typeImage);
        Node declaringNode = getEnclosingScope(SourceFileScope.class).getQualifiedTypeNames().get(typeImage);
        return new SimpleTypedNameDeclaration(typeImage,
                this.getEnclosingScope(SourceFileScope.class).resolveType(typeImage), determineSuper(declaringNode));
    }

    public Class<?> resolveType(final String name) {
        return this.getEnclosingScope(SourceFileScope.class).resolveType(qualifyTypeName(name));
    }

    /**
     * Tries to resolve a given typeImage as a generic Type. If the Generic Type
     * is found, any defined ClassOrInterfaceType below this type declaration is
     * used (this is typically a type bound, e.g. {@code <T extends List>}.
     *
     * @param argument
     *            the node, from where to start searching.
     * @param typeImage
     *            the type as string
     * @return the resolved class or <code>null</code> if nothing was found.
     */
    private Class<?> resolveGenericType(Node argument, String typeImage) {
        List<ASTTypeParameter> types = new ArrayList<>();
        // first search only within the same method
        ASTClassOrInterfaceBodyDeclaration firstParentOfType = argument
                .getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        if (firstParentOfType != null) {
            types.addAll(firstParentOfType.findDescendantsOfType(ASTTypeParameter.class));
        }

        // then search class level types
        ASTClassOrInterfaceDeclaration enclosingClassOrEnum = argument
                .getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (enclosingClassOrEnum == null) {
            argument.getFirstParentOfType(ASTEnumDeclaration.class);
        }
        ASTTypeParameters classLevelTypeParameters = null;
        if (enclosingClassOrEnum != null) {
            classLevelTypeParameters = enclosingClassOrEnum.getFirstChildOfType(ASTTypeParameters.class);
        }
        if (classLevelTypeParameters != null) {
            types.addAll(classLevelTypeParameters.findDescendantsOfType(ASTTypeParameter.class));
        }
        return resolveGenericType(typeImage, types);
    }

    private Class<?> resolveGenericType(String typeImage, List<ASTTypeParameter> types) {
        for (ASTTypeParameter type : types) {
            if (typeImage.equals(type.getImage())) {
                ASTClassOrInterfaceType bound = type.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                if (bound != null) {
                    if (bound.getType() != null) {
                        return bound.getType();
                    } else {
                        return this.getEnclosingScope(SourceFileScope.class).resolveType(bound.getImage());
                    }
                } else {
                    // type parameter found, but no binding.
                    return Object.class;
                }
            }
        }
        return null;
    }

    private Node getNextSibling(Node current) {
        Node nextSibling = null;
        for (int i = 0; i < current.jjtGetParent().jjtGetNumChildren() - 1; i++) {
            if (current.jjtGetParent().jjtGetChild(i) == current) {
                nextSibling = current.jjtGetParent().jjtGetChild(i + 1);
                break;
            }
        }
        return nextSibling;
    }

    public String toString() {
        StringBuilder res = new StringBuilder("ClassScope (").append(className).append("): ");
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (classDeclarations.isEmpty()) {
            res.append("Inner Classes ").append(glomNames(classDeclarations.keySet())).append("; ");
        }
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        if (!methodDeclarations.isEmpty()) {
            for (MethodNameDeclaration mnd : methodDeclarations.keySet()) {
                res.append(mnd.toString());
                int usages = methodDeclarations.get(mnd).size();
                res.append("(begins at line ").append(mnd.getNode().getBeginLine()).append(", ").append(usages)
                        .append(" usages)");
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
