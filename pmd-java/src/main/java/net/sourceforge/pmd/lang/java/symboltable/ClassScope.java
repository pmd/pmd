/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
        @Override
        protected Integer initialValue() {
            return Integer.valueOf(1);
        }
    };

    private final String className;

    private boolean isEnum;

    /**
     * The current class scope declaration. Technically it belongs to out parent scope,
     * but knowing it we can better resolve this, super and direct class references such as Foo.X
     */
    private final ClassNameDeclaration classDeclaration;

    public ClassScope(final String className, final ClassNameDeclaration classNameDeclaration) {
        this.className = Objects.requireNonNull(className);
        anonymousInnerClassCounter.set(Integer.valueOf(1));
        this.classDeclaration = classNameDeclaration;
    }

    /**
     * This is only for anonymous inner classes.
     *
     * <p>FIXME - should have name like Foo$1, not Anonymous$1 to get this working
     * right, the parent scope needs to be passed in when instantiating a
     * ClassScope</p>
     *
     * @param classNameDeclaration The declaration of this class, as known to the parent scope.
     */
    public ClassScope(final ClassNameDeclaration classNameDeclaration) {
        // this.className = getParent().getEnclosingClassScope().getClassName()
        // + "$" + String.valueOf(anonymousInnerClassCounter);
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
        classDeclaration = classNameDeclaration;
    }

    public ClassNameDeclaration getClassDeclaration() {
        return classDeclaration;
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

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence) occurrence;
        Set<NameDeclaration> declarations = findVariableHere(javaOccurrence);
        if (!declarations.isEmpty()
                && (javaOccurrence.isMethodOrConstructorInvocation() || javaOccurrence.isMethodReference())) {
            for (NameDeclaration decl : declarations) {
                List<NameOccurrence> nameOccurrences = getMethodDeclarations().get(decl);
                if (nameOccurrences == null) {
                    // TODO may be a class name: Foo.this.super();

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

    @Override
    protected Set<NameDeclaration> findVariableHere(JavaNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || className.equals(occurrence.getImage())) {
            // Reference to ourselves!
            return Collections.<NameDeclaration>singleton(classDeclaration);
        }

        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        Set<NameDeclaration> result = new HashSet<>();
        if (occurrence.isMethodOrConstructorInvocation()) {
            final boolean hasAuxclasspath = getEnclosingScope(SourceFileScope.class).hasAuxclasspath();
            matchMethodDeclaration(occurrence, methodDeclarations.keySet(), hasAuxclasspath, result);

            if (isEnum && "valueOf".equals(occurrence.getImage())) {
                result.add(createBuiltInMethodDeclaration("valueOf", "String"));
            }

            if (result.isEmpty()) {
                for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                    matchMethodDeclaration(occurrence, innerClass.getScope().getDeclarations(MethodNameDeclaration.class).keySet(), hasAuxclasspath, result);
                }
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

        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableDeclarations.keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }

        // search inner classes
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (result.isEmpty() && !classDeclarations.isEmpty()) {
            for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                Applier.apply(finder, innerClass.getScope().getDeclarations(VariableNameDeclaration.class).keySet().iterator());
                if (finder.getDecl() != null) {
                    result.add(finder.getDecl());
                }
            }
        }
        return result;
    }

    private void matchMethodDeclaration(JavaNameOccurrence occurrence,
            Set<MethodNameDeclaration> methodDeclarations, final boolean hasAuxclasspath,
            Set<NameDeclaration> result) {
        for (MethodNameDeclaration mnd : methodDeclarations) {
            if (mnd.getImage().equals(occurrence.getImage())) {
                List<TypedNameDeclaration> parameterTypes = determineParameterTypes(mnd);
                List<TypedNameDeclaration> argumentTypes = determineArgumentTypes(occurrence, parameterTypes);

                if (!mnd.isVarargs() && occurrence.getArgumentCount() == mnd.getParameterCount()
                        && (!hasAuxclasspath || parameterTypes.equals(argumentTypes))) {
                    result.add(mnd);
                } else if (mnd.isVarargs()) {
                    int varArgIndex = parameterTypes.size() - 1;
                    TypedNameDeclaration varArgType = parameterTypes.get(varArgIndex);

                    // first parameter is varArg, calling method might have
                    // 0 or more arguments
                    // or the calling method has enough arguments to fill in
                    // the parameters before the vararg
                    if ((varArgIndex == 0 || argumentTypes.size() >= varArgIndex)
                            && (!hasAuxclasspath || parameterTypes
                                    .subList(0, varArgIndex).equals(argumentTypes.subList(0, varArgIndex)))) {

                        if (!hasAuxclasspath) {
                            result.add(mnd);
                            continue;
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

        /*
         * jjtAddChild resizes it's child node list according to known indexes.
         * Going backwards makes sure the first time it gets the right size avoiding copies.
         */
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            ASTFormalParameter formalParameter = new ASTFormalParameter(JavaParserTreeConstants.JJTFORMALPARAMETER);
            formalParameters.jjtAddChild(formalParameter, i);
            formalParameter.jjtSetParent(formalParameters);

            ASTVariableDeclaratorId variableDeclaratorId = new ASTVariableDeclaratorId(
                    JavaParserTreeConstants.JJTVARIABLEDECLARATORID);
            variableDeclaratorId.setImage("arg" + i);
            formalParameter.jjtAddChild(variableDeclaratorId, 1);
            variableDeclaratorId.jjtSetParent(formalParameter);

            ASTType type = new ASTType(JavaParserTreeConstants.JJTTYPE);
            formalParameter.jjtAddChild(type, 0);
            type.jjtSetParent(formalParameter);

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

        return new MethodNameDeclaration(methodDeclarator);
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
        List<ASTFormalParameter> parameters = mnd.getMethodNameDeclaratorNode()
                .findDescendantsOfType(ASTFormalParameter.class);
        if (parameters.isEmpty()) {
            return Collections.emptyList();
        }

        List<TypedNameDeclaration> parameterTypes = new ArrayList<>(parameters.size());
        SourceFileScope fileScope = getEnclosingScope(SourceFileScope.class);
        Map<String, Node> qualifiedTypeNames = fileScope.getQualifiedTypeNames();

        for (ASTFormalParameter p : parameters) {
            if (p.isExplicitReceiverParameter()) {
                continue;
            }

            String typeImage = p.getTypeNode().getTypeImage();
            // typeImage might be qualified/unqualified. If it refers to a type,
            // defined in the same toplevel class,
            // we should normalize the name here.
            // It might also refer to a type, that is imported.
            typeImage = qualifyTypeName(typeImage);
            Node declaringNode = qualifiedTypeNames.get(typeImage);
            Class<?> resolvedType = fileScope.resolveType(typeImage);
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

        final SourceFileScope fileScope = getEnclosingScope(SourceFileScope.class);

        // Is it an inner class being accessed?
        String qualified = findQualifiedName(typeImage, fileScope.getQualifiedTypeNames().keySet());
        if (qualified != null) {
            return qualified;
        }

        // Is it an explicit import?
        qualified = findQualifiedName(typeImage, fileScope.getExplicitImports());
        if (qualified != null) {
            return qualified;
        }

        // Is it an inner class of an explicit import?
        int dotIndex = typeImage.indexOf('.');
        if (dotIndex != -1) {
            qualified = findQualifiedName(typeImage.substring(0, dotIndex), fileScope.getExplicitImports());
            if (qualified != null) {
                return qualified.concat(typeImage.substring(dotIndex));
            }
        }

        return typeImage;
    }

    private String findQualifiedName(String typeImage, Set<String> candidates) {
        int nameLength = typeImage.length();
        for (String qualified : candidates) {
            int fullLength = qualified.length();
            if (qualified.endsWith(typeImage)
                    && (fullLength == nameLength || qualified.charAt(fullLength - nameLength - 1) == '.')) {
                return qualified;
            }
        }

        return null;
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
        ASTArgumentList arguments = null;
        Node nextSibling;
        if (occurrence.getLocation() instanceof ASTPrimarySuffix) {
            nextSibling = getNextSibling(occurrence.getLocation());
        } else {
            nextSibling = getNextSibling(occurrence.getLocation().getParent());
        }

        if (nextSibling != null) {
            arguments = nextSibling.getFirstDescendantOfType(ASTArgumentList.class);
        }

        if (arguments == null) {
            return Collections.emptyList();
        }

        List<TypedNameDeclaration> argumentTypes = new ArrayList<>(arguments.getNumChildren());
        Map<String, Node> qualifiedTypeNames = getEnclosingScope(SourceFileScope.class).getQualifiedTypeNames();

        for (int i = 0; i < arguments.getNumChildren(); i++) {
            Node argument = arguments.getChild(i);
            Node child = null;
            boolean isMethodCall = false;
            if (argument.getNumChildren() > 0 && argument.getChild(0).getNumChildren() > 0
                    && argument.getChild(0).getChild(0).getNumChildren() > 0) {
                child = argument.getChild(0).getChild(0).getChild(0);
                isMethodCall = argument.getChild(0).getNumChildren() > 1;
            }
            TypedNameDeclaration type = null;
            if (!isMethodCall) {
                if (child instanceof ASTName) {
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
                    } else if (literal.getNumChildren() == 1
                            && literal.getChild(0) instanceof ASTBooleanLiteral) {
                        type = new SimpleTypedNameDeclaration("boolean", Boolean.TYPE);
                    }
                } else if (child instanceof ASTAllocationExpression
                        && child.getChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType classInterface = (ASTClassOrInterfaceType) child.getChild(0);
                    type = convertToSimpleType(classInterface);
                }
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

        // then search class level types, from inner-most to outer-most
        List<ASTClassOrInterfaceDeclaration> enclosingClasses = argument
                .getParentsOfType(ASTClassOrInterfaceDeclaration.class);
        for (ASTClassOrInterfaceDeclaration enclosing : enclosingClasses) {
            ASTTypeParameters classLevelTypeParameters = enclosing.getFirstChildOfType(ASTTypeParameters.class);
            if (classLevelTypeParameters != null) {
                types.addAll(classLevelTypeParameters.findDescendantsOfType(ASTTypeParameter.class));
            }
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
        if (current.getParent().getNumChildren() > current.getIndexInParent() + 1) {
            return current.getParent().getChild(current.getIndexInParent() + 1);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("ClassScope (").append(className).append("): ");
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (classDeclarations.isEmpty()) {
            res.append("Inner Classes ").append(glomNames(classDeclarations.keySet())).append("; ");
        }
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        if (!methodDeclarations.isEmpty()) {
            for (Map.Entry<MethodNameDeclaration, List<NameOccurrence>> entry : methodDeclarations.entrySet()) {
                res.append(entry.getKey().toString());
                int usages = entry.getValue().size();
                res.append("(begins at line ").append(entry.getKey().getNode().getBeginLine()).append(", ").append(usages)
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
