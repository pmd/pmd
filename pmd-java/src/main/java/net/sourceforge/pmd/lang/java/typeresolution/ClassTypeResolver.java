/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSingleMemberAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArgument;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeBound;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.Token;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinitionBuilder;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

//
// Helpful reading:
// http://www.janeg.ca/scjp/oper/promotions.html
// http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html
//

public class ClassTypeResolver extends JavaParserVisitorAdapter {

    private static final Logger LOG = Logger.getLogger(ClassTypeResolver.class.getName());

    private static final Map<String, Class<?>> PRIMITIVE_TYPES;
    private static final Map<String, String> JAVA_LANG;

    static {
        // Note: Assumption here that primitives come from same parent
        // ClassLoader regardless of what ClassLoader we are passed
        Map<String, Class<?>> thePrimitiveTypes = new HashMap<>();
        thePrimitiveTypes.put("void", Void.TYPE);
        thePrimitiveTypes.put("boolean", Boolean.TYPE);
        thePrimitiveTypes.put("byte", Byte.TYPE);
        thePrimitiveTypes.put("char", Character.TYPE);
        thePrimitiveTypes.put("short", Short.TYPE);
        thePrimitiveTypes.put("int", Integer.TYPE);
        thePrimitiveTypes.put("long", Long.TYPE);
        thePrimitiveTypes.put("float", Float.TYPE);
        thePrimitiveTypes.put("double", Double.TYPE);
        PRIMITIVE_TYPES = Collections.unmodifiableMap(thePrimitiveTypes);

        Map<String, String> theJavaLang = new HashMap<>();
        theJavaLang.put("Boolean", "java.lang.Boolean");
        theJavaLang.put("Byte", "java.lang.Byte");
        theJavaLang.put("Character", "java.lang.Character");
        theJavaLang.put("CharSequence", "java.lang.CharSequence");
        theJavaLang.put("Class", "java.lang.Class");
        theJavaLang.put("ClassLoader", "java.lang.ClassLoader");
        theJavaLang.put("Cloneable", "java.lang.Cloneable");
        theJavaLang.put("Comparable", "java.lang.Comparable");
        theJavaLang.put("Compiler", "java.lang.Compiler");
        theJavaLang.put("Double", "java.lang.Double");
        theJavaLang.put("Float", "java.lang.Float");
        theJavaLang.put("InheritableThreadLocal", "java.lang.InheritableThreadLocal");
        theJavaLang.put("Integer", "java.lang.Integer");
        theJavaLang.put("Long", "java.lang.Long");
        theJavaLang.put("Math", "java.lang.Math");
        theJavaLang.put("Number", "java.lang.Number");
        theJavaLang.put("Object", "java.lang.Object");
        theJavaLang.put("Package", "java.lang.Package");
        theJavaLang.put("Process", "java.lang.Process");
        theJavaLang.put("Runnable", "java.lang.Runnable");
        theJavaLang.put("Runtime", "java.lang.Runtime");
        theJavaLang.put("RuntimePermission", "java.lang.RuntimePermission");
        theJavaLang.put("SecurityManager", "java.lang.SecurityManager");
        theJavaLang.put("Short", "java.lang.Short");
        theJavaLang.put("StackTraceElement", "java.lang.StackTraceElement");
        theJavaLang.put("StrictMath", "java.lang.StrictMath");
        theJavaLang.put("String", "java.lang.String");
        theJavaLang.put("StringBuffer", "java.lang.StringBuffer");
        theJavaLang.put("System", "java.lang.System");
        theJavaLang.put("Thread", "java.lang.Thread");
        theJavaLang.put("ThreadGroup", "java.lang.ThreadGroup");
        theJavaLang.put("ThreadLocal", "java.lang.ThreadLocal");
        theJavaLang.put("Throwable", "java.lang.Throwable");
        theJavaLang.put("Void", "java.lang.Void");
        JAVA_LANG = Collections.unmodifiableMap(theJavaLang);
    }

    private final PMDASMClassLoader pmdClassLoader;
    private Map<String, String> importedClasses;
    private List<String> importedOnDemand;
    private int anonymousClassCounter = 0;

    /**
     * Contains Class -> JavaTypeDefinitions map for raw Class types. Also helps to avoid infinite recursion
     * when determining default upper bounds.
     */
    private Map<Class<?>, JavaTypeDefinition> classToDefaultUpperBounds = new HashMap<>();

    public ClassTypeResolver() {
        this(ClassTypeResolver.class.getClassLoader());
    }

    public ClassTypeResolver(ClassLoader classLoader) {
        pmdClassLoader = PMDASMClassLoader.getInstance(classLoader);
    }

    // FUTURE ASTCompilationUnit should not be a TypeNode. Clean this up
    // accordingly.
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        String className = null;
        try {
            importedOnDemand = new ArrayList<>();
            importedClasses = new HashMap<>();
            className = getClassName(node);
            if (className != null) {
                populateClassName(node, className);
            }
        } catch (ClassNotFoundException e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Could not find class " + className + ", due to: " + e);
            }
        } catch (NoClassDefFoundError e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Could not find class " + className + ", due to: " + e);
            }
        } catch (LinkageError e) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Could not find class " + className + ", due to: " + e);
            }
        } finally {
            populateImports(node);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        // no need to visit children, the only child, ASTName, will have no type
        return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName importedType = (ASTName) node.jjtGetChild(0);

        if (importedType.getType() != null) {
            node.setType(importedType.getType());
        } else {
            populateType(node, importedType.getImage());
        }

        if (node.getType() != null) {
            node.setPackage(node.getType().getPackage());
        }

        // no need to visit children, the only child, ASTName, will have no type
        return data;
    }

    @Override
    public Object visit(ASTTypeDeclaration node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        super.visit(node, data);

        String typeName = node.getImage();

        if (node.isAnonymousClass()) {
            anonymousClassCounter++;
            AbstractNode parent = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (parent == null) {
                parent = node.getFirstParentOfType(ASTEnumDeclaration.class);
            }
            typeName = parent.getImage() + "$" + anonymousClassCounter;
        }

        populateType(node, typeName);

        ASTTypeArguments typeArguments = node.getFirstChildOfType(ASTTypeArguments.class);

        if (typeArguments != null) {
            JavaTypeDefinitionBuilder builder = JavaTypeDefinition.builder(node.getType());

            for (int index = 0; index < typeArguments.jjtGetNumChildren(); ++index) {
                builder.addTypeArg(((TypeNode) typeArguments.jjtGetChild(index)).getTypeDefinition());
            }

            node.setTypeDefinition(builder.build());
        } else if (isGeneric(node.getType()) && node.getTypeDefinition().getGenericArgs().size() == 0) {
            node.setTypeDefinition(getDefaultUpperBounds(null, node.getType()));
        }

        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        populateType(node, node.getImage());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        populateType(node, node.getImage());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        populateType(node, node.getImage());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        Class<?> accessingClass = getEnclosingTypeDeclaration(node);

        String[] dotSplitImage = node.getImage().split("\\.");
        JavaTypeDefinition previousType
                = getTypeDefinitionOfVariableFromScope(node.getScope(), dotSplitImage[0], accessingClass);


        if (node.getNameDeclaration() != null
                && previousType == null // if it's not null, then let other code handle things
                && node.getNameDeclaration().getNode() instanceof TypeNode) {
            // Carry over the type from the declaration
            Class<?> nodeType = ((TypeNode) node.getNameDeclaration().getNode()).getType();
            // generic classes and class with generic super types could have the wrong type assigned here
            if (nodeType != null && !isGeneric(nodeType) && !isGeneric(nodeType.getSuperclass())) {
                node.setType(nodeType);
            }
        }

        if (node.getType() == null) {
            // TODO: handle cases where static fields are accessed in a fully qualified way
            //       make sure it handles same name classes and packages
            // TODO: handle generic static field cases

            // handles cases where first part is a fully qualified name
            populateType(node, node.getImage());

            if (node.getType() == null) {
                for (int i = 1; i < dotSplitImage.length; ++i) {
                    if (previousType == null) {
                        break;
                    }

                    previousType = getFieldType(previousType, dotSplitImage[i], accessingClass);
                }

                if (previousType != null) {
                    node.setTypeDefinition(previousType);
                }
            }
        }

        return super.visit(node, data);
    }

    /**
     * Searches a JavaTypeDefinition and it's superclasses until a field with name {@code fieldImage} that
     * is visible from the {@code accessingClass} class. Once it's found, it's possibly generic type is
     * resolved with the help of {@code typeToSearch} TypeDefinition.
     *
     * @param typeToSearch   The type def. to search the field in.
     * @param fieldImage     The simple name of the field.
     * @param accessingClass The class that is trying to access the field, some Class declared in the current ACU.
     * @return JavaTypeDefinition of the resolved field or null if it could not be found.
     */
    private JavaTypeDefinition getFieldType(JavaTypeDefinition typeToSearch, String fieldImage, Class<?>
            accessingClass) {
        while (typeToSearch != null) {
            try {
                Field field = typeToSearch.getType().getDeclaredField(fieldImage);
                if (isMemberVisibleFromClass(typeToSearch.getType(), field.getModifiers(), accessingClass)) {
                    return getNextTypeDefinition(typeToSearch, field.getGenericType());
                }
            } catch (NoSuchFieldException e) { /* swallow */ }

            // transform the type into it's supertype
            typeToSearch = getNextTypeDefinition(typeToSearch, typeToSearch.getType().getGenericSuperclass());
        }

        return null;
    }

    /**
     * Search for a field by it's image stating from a scope and taking into account if it's visible from the
     * accessingClass Class. The method takes into account that Nested inherited fields shadow outer scope fields.
     *
     * @param scope          The scope to start the search from.
     * @param image          The name of the field, local variable or method parameter.
     * @param accessingClass The Class (which is defined in the current ACU) that is trying to access the field.
     * @return Type def. of the field, or null if it could not be resolved.
     */
    private JavaTypeDefinition getTypeDefinitionOfVariableFromScope(Scope scope, String image, Class<?>
            accessingClass) {
        if (accessingClass == null) {
            return null;
        }

        for (/* empty */; scope != null; scope = scope.getParent()) {
            // search each enclosing scope one by one
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry
                    : scope.getDeclarations(VariableNameDeclaration.class).entrySet()) {
                if (entry.getKey().getImage().equals(image)) {
                    ASTType typeNode = entry.getKey().getDeclaratorId().getTypeNode();

                    if (typeNode == null) {
                        // TODO : Type is infered, ie, this is a lambda such as (var) -> var.equals(other)
                        return null;
                    }

                    if (typeNode.jjtGetChild(0) instanceof ASTReferenceType) {
                        return ((TypeNode) typeNode.jjtGetChild(0)).getTypeDefinition();
                    } else { // primitive type
                        return JavaTypeDefinition.build(typeNode.getType());
                    }
                }
            }

            // Nested class' inherited fields shadow enclosing variables
            if (scope instanceof ClassScope) {
                try {
                    // get the superclass type def. ot the Class the ClassScope belongs to
                    JavaTypeDefinition superClass
                            = getSuperClassTypeDefinition(((ClassScope) scope).getClassDeclaration().getNode(),
                                                          null);
                    // TODO: check if anonymous classes are class scope

                    // try searching this type def.
                    JavaTypeDefinition foundTypeDef = getFieldType(superClass, image, accessingClass);

                    if (foundTypeDef != null) { // if null, then it's not an inherited field
                        return foundTypeDef;
                    }
                } catch (ClassCastException e) {
                    // if there is an anonymous class, getClassDeclaration().getType() will throw
                    // TODO: maybe there is a better way to handle this, maybe this hides bugs
                }
            }
        }

        return null;
    }

    /**
     * Given a type def. and a Type, resolves the type into a JavaTypeDefinition. Takes into account
     * simple Classes, TypeVariables, ParameterizedTypes and WildCards types. Can resolve nested Generic
     * type arguments.
     *
     * @param context     The JavaTypeDefinition in which the {@code genericType} was declared.
     * @param genericType The Type to resolve.
     * @return JavaTypeDefinition of the {@code genericType}.
     */
    private JavaTypeDefinition getNextTypeDefinition(JavaTypeDefinition context, Type genericType) {
        return getNextTypeDefinition(context, genericType, null);
    }

    private JavaTypeDefinition getNextTypeDefinition(JavaTypeDefinition context, Type genericType,
                                                     JavaTypeDefinitionBuilder buildTypeInAdvance) {
        if (genericType == null) {
            return null;
        }

        if (genericType instanceof Class) { // Raw types take this branch as well
            return getDefaultUpperBounds(context, (Class) genericType);
        } else if (genericType instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            JavaTypeDefinitionBuilder typeDef = JavaTypeDefinition.builder((Class) parameterizedType.getRawType());

            if (buildTypeInAdvance != null) {
                buildTypeInAdvance.addTypeArg(typeDef.build());
            }

            // recursively determine each type argument's type def.
            for (Type type : parameterizedType.getActualTypeArguments()) {
                typeDef.addTypeArg(getNextTypeDefinition(context, type));
            }

            return typeDef.build();
        } else if (genericType instanceof TypeVariable) {
            int ordinal = getTypeParameterOrdinal(context.getType(), ((TypeVariable) genericType).getName());
            if (ordinal != -1) {
                return context.getGenericArgs().get(ordinal);
            }
        } else if (genericType instanceof WildcardType) {
            Type[] wildcardUpperBounds = ((WildcardType) genericType).getUpperBounds();
            if (wildcardUpperBounds.length != 0) { // upper bound wildcard
                return getNextTypeDefinition(context, wildcardUpperBounds[0]);
            } else { // lower bound wildcard
                return JavaTypeDefinition.build(Object.class);
            }
        }

        return null;
    }

    /**
     * Returns the ordinal of the type parameter with the name {@code parameterName} in {@code clazz}.
     *
     * @param clazz         The Class with the type parameters.
     * @param parameterName The name of the type parameter.
     * @return The ordinal of the type parameter.
     */
    private int getTypeParameterOrdinal(Class<?> clazz, String parameterName) {
        TypeVariable[] classTypeParameters = clazz.getTypeParameters();

        for (int index = 0; index < classTypeParameters.length; ++index) {
            if (classTypeParameters[index].getName().equals(parameterName)) {
                return index;
            }
        }

        return -1;
    }

    /**
     * Returns true if the class is generic.
     *
     * @param clazz The Class to examine.
     * @return True if the Class is generic.
     */
    private boolean isGeneric(Class<?> clazz) {
        if (clazz != null) {
            return clazz.getTypeParameters().length != 0;
        }

        return false;
    }

    /**
     * Given a Class, returns the type def. for when the Class stands without type arguments, meaning it
     * is a raw type. Determines the generic types by looking at the upper bounds of it's generic parameters.
     *
     * @param context            Synthetic parameter for recursion, pass {@code null}.
     * @param clazzWithDefBounds The raw Class type.
     * @return The type def. of the raw Class.
     */
    private JavaTypeDefinition getDefaultUpperBounds(JavaTypeDefinition context, Class<?> clazzWithDefBounds) {
        JavaTypeDefinitionBuilder typeDef = JavaTypeDefinition.builder(clazzWithDefBounds);

        // helps avoid infinite recursion with Something<.... E extends Something (<- same raw type)... >
        if (classToDefaultUpperBounds.containsKey(clazzWithDefBounds)) {
            return classToDefaultUpperBounds.get(clazzWithDefBounds);
        } else {
            classToDefaultUpperBounds.put(clazzWithDefBounds, typeDef.build());
        }

        if (isGeneric(clazzWithDefBounds)) {
            // Recursion, outer call should pass in null.
            // Recursive calls will get the first JavaTypeDefinition to be able to resolve cases like
            // ... < T extends Something ... E extends Other<T> ... >
            if (context == null) {
                context = typeDef.build();
            }

            for (TypeVariable parameter : clazzWithDefBounds.getTypeParameters()) {
                // TODO: fix self reference "< ... E extends Something<E> ... >"
                JavaTypeDefinition typeDefOfParameter = getNextTypeDefinition(context, parameter.getBounds()[0],
                                                                              typeDef);

                // if it isn't 0, then it has already been added
                if (typeDefOfParameter.getGenericArgs().size() == 0) {
                    typeDef.addTypeArg(getNextTypeDefinition(context, parameter.getBounds()[0]));
                }
            }
        }

        return typeDef.build();
    }

    /**
     * Given a class, the modifiers of on of it's member and the class that is trying to access that member,
     * returns true is the member is accessible from the accessingClass Class.
     *
     * @param classWithMember The Class with the member.
     * @param modifiers       The modifiers of that member.
     * @param accessingClass  The Class trying to access the member.
     * @return True if the member is visible from the accessingClass Class.
     */
    private boolean isMemberVisibleFromClass(Class<?> classWithMember, int modifiers, Class<?> accessingClass) {
        if (accessingClass == null) {
            return false;
        }

        // public members
        if (Modifier.isPublic(modifiers)) {
            return true;
        }

        boolean areInTheSamePackage;
        if (accessingClass.getPackage() != null) {
            areInTheSamePackage = accessingClass.getPackage().getName().startsWith(
                    classWithMember.getPackage().getName());
        } else {
            return false; // if the package information is null, we can't do nothin'
        }

        // protected members
        if (Modifier.isProtected(modifiers)) {
            if (areInTheSamePackage || classWithMember.isAssignableFrom(accessingClass)) {
                return true;
            }
            // private members
        } else if (Modifier.isPrivate(modifiers)) {
            if (classWithMember.equals(accessingClass)) {
                return true;
            }
            // package private members
        } else if (areInTheSamePackage) {
            return true;
        }

        return false;
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node == null || node.getNameDeclaration() == null) {
            return super.visit(node, data);
        }
        String name = node.getNameDeclaration().getTypeImage();
        if (name != null) {
            populateType(node, name);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTType node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTReferenceType node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        populateType(node, node.getImage());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        super.visit(node, data);
        if (node.isTernary()) {
            // TODO Rules for Ternary are complex
        } else {
            rollupTypeUnary(node);
        }
        return data;
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        super.visit(node, data);
        rollupTypeBinaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        super.visit(node, data);
        rollupTypeBinaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTAndExpression node, Object data) {
        super.visit(node, data);
        rollupTypeBinaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTShiftExpression node, Object data) {
        super.visit(node, data);
        // Unary promotion on LHS is type of a shift operation
        rollupTypeUnaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        super.visit(node, data);
        rollupTypeBinaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        super.visit(node, data);
        rollupTypeBinaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnaryNumericPromotion(node);
        return data;
    }

    @Override
    public Object visit(ASTPreIncrementExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTPreDecrementExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        super.visit(node, data);
        if ("!".equals(node.getImage())) {
            populateType(node, "boolean");
        } else {
            rollupTypeUnary(node);
        }
        return data;
    }

    @Override
    public Object visit(ASTPostfixExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTCastExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }


    @Override
    public Object visit(ASTPrimaryExpression primaryNode, Object data) {
        super.visit(primaryNode, data);

        JavaTypeDefinition primaryNodeType = null;
        AbstractJavaTypeNode previousChild = null;
        Class<?> accessingClass = getEnclosingTypeDeclaration(primaryNode);

        for (int childIndex = 0; childIndex < primaryNode.jjtGetNumChildren(); ++childIndex) {
            AbstractJavaTypeNode currentChild = (AbstractJavaTypeNode) primaryNode.jjtGetChild(childIndex);

            // skip children which already have their type assigned
            if (currentChild.getType() == null) {
                // Last token, because if 'this' is a Suffix, it'll have tokens '.' and 'this'
                if (currentChild.jjtGetLastToken().toString().equals("this")) {

                    if (previousChild != null) { // Qualified 'this' expression
                        currentChild.setType(previousChild.getType());
                    } else { // simple 'this' expression
                        ASTClassOrInterfaceDeclaration typeDeclaration
                                = currentChild.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);

                        if (typeDeclaration != null) {
                            currentChild.setTypeDefinition(typeDeclaration.getTypeDefinition());
                        }
                    }

                    // Last token, because if 'super' is a Suffix, it'll have tokens '.' and 'super'
                } else if (currentChild.jjtGetLastToken().toString().equals("super")) {

                    if (previousChild != null) { // Qualified 'super' expression
                        // anonymous classes can't have qualified super expression, thus
                        // getSuperClassTypeDefinition's second argumet isn't null, but we are not
                        // looking for enclosing super types
                        currentChild.setTypeDefinition(
                                getSuperClassTypeDefinition(currentChild, previousChild.getType()));
                    } else { // simple 'super' expression
                        currentChild.setTypeDefinition(getSuperClassTypeDefinition(currentChild, null));
                    }

                } else if (previousChild != null && previousChild.getType() != null
                        && currentChild.getImage() != null) {

                    currentChild.setTypeDefinition(getFieldType(previousChild.getTypeDefinition(),
                                                                currentChild.getImage(),
                                                                accessingClass));
                }
            }


            if (currentChild.getType() != null) {
                primaryNodeType = currentChild.getTypeDefinition();
            } else {
                // avoid falsely passing tests
                primaryNodeType = null;
                break;
            }

            previousChild = currentChild;
        }

        primaryNode.setTypeDefinition(primaryNodeType);

        return data;
    }

    /**
     * Returns the type def. of the first Class declaration around the node. Looks for Class declarations
     * and if the second argument is null, then for anonymous classes as well.
     *
     * @param node The node with the enclosing Class declaration.
     * @return The JavaTypeDefinition of the enclosing Class declaration.
     */
    private Class<?> getEnclosingTypeDeclaration(Node node) {
        Node previousNode = null;
        while (node != null) {
            if (node instanceof ASTClassOrInterfaceDeclaration) {
                return ((TypeNode) node).getType();
                // anonymous class declaration
            } else if (node instanceof ASTAllocationExpression // is anonymous class declaration
                    && node.getFirstChildOfType(ASTArrayDimsAndInits.class) == null // array cant anonymous
                    && !(previousNode instanceof ASTArguments)) { // we might come out of the constructor
                ASTClassOrInterfaceType typeDecl = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
                if (typeDecl != null) {
                    return typeDecl.getType();
                }
            }

            previousNode = node;
            node = node.jjtGetParent();
        }

        return null;
    }

    /**
     * Get the type def. of the super class of the enclosing type declaration which has the same class
     * as the second argument, or if the second argument is null, then anonymous classes are considered
     * as well and the first enclosing scope's super class is returned.
     *
     * @param node  The node from which to start searching.
     * @param clazz The type of the enclosing class.
     * @return The TypeDefinition of the superclass.
     */
    private JavaTypeDefinition getSuperClassTypeDefinition(Node node, Class<?> clazz) {
        Node previousNode = null;
        for (; node != null; previousNode = node, node = node.jjtGetParent()) {
            if (node instanceof ASTClassOrInterfaceDeclaration // class declaration
                    // is the class we are looking for or caller requested first class
                    && (((TypeNode) node).getType() == clazz || clazz == null)) {

                ASTExtendsList extendsList = node.getFirstChildOfType(ASTExtendsList.class);

                if (extendsList != null) {
                    return ((TypeNode) extendsList.jjtGetChild(0)).getTypeDefinition();
                } else {
                    return JavaTypeDefinition.build(Object.class);
                }
                // anonymous class declaration

            } else if (clazz == null // callers requested any class scope
                    && node instanceof ASTAllocationExpression // is anonymous class decl
                    && node.getFirstChildOfType(ASTArrayDimsAndInits.class) == null // arrays can't be anonymous
                    && !(previousNode instanceof ASTArguments)) { // we might come out of the constructor
                return node.getFirstChildOfType(ASTClassOrInterfaceType.class).getTypeDefinition();
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);

        return data;
    }

    @Override
    public Object visit(ASTTypeArgument node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);

        if (node.getType() == null) {
            // ? extends Something
            if (node.jjtGetFirstToken() instanceof Token
                    && ((Token) node.jjtGetFirstToken()).next.image.equals("extends")) {

                populateType(node, node.jjtGetLastToken().toString());

            } else {  // ? or ? super Something
                node.setType(Object.class);
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeParameters node, Object data) {
        super.visit(node, data);

        if (node.jjtGetParent() instanceof ASTClassOrInterfaceDeclaration) {
            TypeNode parent = (TypeNode) node.jjtGetParent();

            JavaTypeDefinitionBuilder builder = JavaTypeDefinition.builder(parent.getType());

            for (int childIndex = 0; childIndex < node.jjtGetNumChildren(); ++childIndex) {
                builder.addTypeArg(((TypeNode) node.jjtGetChild(childIndex)).getTypeDefinition());
            }

            parent.setTypeDefinition(builder.build());
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeParameter node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);

        if (node.getType() == null) {
            node.setType(Object.class);
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeBound node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        // No explicit type
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        populateType(node, "boolean");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        super.visit(node, data);
        if (node.jjtGetNumChildren() != 0) {
            rollupTypeUnary(node);
        } else {
            if (node.isIntLiteral()) {
                populateType(node, "int");
            } else if (node.isLongLiteral()) {
                populateType(node, "long");
            } else if (node.isFloatLiteral()) {
                populateType(node, "float");
            } else if (node.isDoubleLiteral()) {
                populateType(node, "double");
            } else if (node.isCharLiteral()) {
                populateType(node, "char");
            } else if (node.isStringLiteral()) {
                populateType(node, "java.lang.String");
            } else {
                throw new IllegalStateException("PMD error, unknown literal type!");
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        super.visit(node, data);

        if (node.jjtGetNumChildren() >= 2 && node.jjtGetChild(1) instanceof ASTArrayDimsAndInits
                || node.jjtGetNumChildren() >= 3 && node.jjtGetChild(2) instanceof ASTArrayDimsAndInits) {
            //
            // Classes for Array types cannot be found directly using
            // reflection.
            // As far as I can tell you have to create an array instance of the
            // necessary
            // dimensionality, and then ask for the type from the instance. OMFG
            // that's ugly.
            //

            // TODO Need to create utility method to allow array type creation
            // which will use
            // caching to avoid repeated object creation.
            // TODO Modify Parser to tell us array dimensions count.
            // TODO Parser seems to do some work to handle arrays in certain
            // case already.
            // Examine those to figure out what's going on, make sure _all_
            // array scenarios
            // are ultimately covered. Appears to use a Dimensionable interface
            // to handle
            // only a part of the APIs (not bump), but is implemented several
            // times, so
            // look at refactoring to eliminate duplication. Dimensionable is
            // also used
            // on AccessNodes for some scenarios, need to account for that.
            // Might be
            // missing some TypeNode candidates we can add to the AST and have
            // to deal
            // with here (e.g. FormalParameter)? Plus some existing usages may
            // be
            // incorrect.
        } else {
            rollupTypeUnary(node);
        }
        return data;
    }

    @Override
    public Object visit(ASTStatementExpression node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTNormalAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTMarkerAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTSingleMemberAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    // Roll up the type based on type of the first child node.
    private void rollupTypeUnary(TypeNode typeNode) {
        Node node = typeNode;
        if (node.jjtGetNumChildren() >= 1) {
            Node child = node.jjtGetChild(0);
            if (child instanceof TypeNode) {
                typeNode.setTypeDefinition(((TypeNode) child).getTypeDefinition());
            }
        }
    }

    // Roll up the type based on type of the first child node using Unary
    // Numeric Promotion per JLS 5.6.1
    private void rollupTypeUnaryNumericPromotion(TypeNode typeNode) {
        Node node = typeNode;
        if (node.jjtGetNumChildren() >= 1) {
            Node child = node.jjtGetChild(0);
            if (child instanceof TypeNode) {
                Class<?> type = ((TypeNode) child).getType();
                if (type != null) {
                    if ("byte".equals(type.getName()) || "short".equals(type.getName())
                            || "char".equals(type.getName())) {
                        populateType(typeNode, "int");
                    } else {
                        typeNode.setType(((TypeNode) child).getType());
                    }
                }
            }
        }
    }

    // Roll up the type based on type of the first and second child nodes using
    // Binary Numeric Promotion per JLS 5.6.2
    private void rollupTypeBinaryNumericPromotion(TypeNode typeNode) {
        Node node = typeNode;
        if (node.jjtGetNumChildren() >= 2) {
            Node child1 = node.jjtGetChild(0);
            Node child2 = node.jjtGetChild(1);
            if (child1 instanceof TypeNode && child2 instanceof TypeNode) {
                Class<?> type1 = ((TypeNode) child1).getType();
                Class<?> type2 = ((TypeNode) child2).getType();
                if (type1 != null && type2 != null) {
                    // Yeah, String is not numeric, but easiest place to handle
                    // it, only affects ASTAdditiveExpression
                    if ("java.lang.String".equals(type1.getName()) || "java.lang.String".equals(type2.getName())) {
                        populateType(typeNode, "java.lang.String");
                    } else if ("boolean".equals(type1.getName()) || "boolean".equals(type2.getName())) {
                        populateType(typeNode, "boolean");
                    } else if ("double".equals(type1.getName()) || "double".equals(type2.getName())) {
                        populateType(typeNode, "double");
                    } else if ("float".equals(type1.getName()) || "float".equals(type2.getName())) {
                        populateType(typeNode, "float");
                    } else if ("long".equals(type1.getName()) || "long".equals(type2.getName())) {
                        populateType(typeNode, "long");
                    } else {
                        populateType(typeNode, "int");
                    }
                } else if (type1 != null || type2 != null) {
                    // If one side is known to be a String, then the result is a
                    // String
                    // Yeah, String is not numeric, but easiest place to handle
                    // it, only affects ASTAdditiveExpression
                    if (type1 != null && "java.lang.String".equals(type1.getName())
                            || type2 != null && "java.lang.String".equals(type2.getName())) {
                        populateType(typeNode, "java.lang.String");
                    }
                }
            }
        }
    }

    private void populateType(TypeNode node, String className) {

        String qualifiedName = className;
        Class<?> myType = PRIMITIVE_TYPES.get(className);
        if (myType == null && importedClasses != null) {
            if (importedClasses.containsKey(className)) {
                qualifiedName = importedClasses.get(className);
            } else if (importedClasses.containsValue(className)) {
                qualifiedName = className;
            }
            if (qualifiedName != null) {
                try {
                    /*
                     * TODO - the map right now contains just class names. if we
                     * use a map of classname/class then we don't have to hit
                     * the class loader for every type - much faster
                     */
                    myType = pmdClassLoader.loadClass(qualifiedName);
                } catch (ClassNotFoundException e) {
                    myType = processOnDemand(qualifiedName);
                } catch (NoClassDefFoundError e) {
                    myType = processOnDemand(qualifiedName);
                } catch (LinkageError e) {
                    myType = processOnDemand(qualifiedName);
                }
            }
        }
        if (myType == null && qualifiedName != null && qualifiedName.contains(".")) {
            // try if the last part defines a inner class
            String qualifiedNameInner = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')) + "$"
                    + qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
            try {
                myType = pmdClassLoader.loadClass(qualifiedNameInner);
            } catch (Exception e) {
                // ignored
            }
        }
        if (myType == null && qualifiedName != null && !qualifiedName.contains(".")) {
            // try again with java.lang....
            try {
                myType = pmdClassLoader.loadClass("java.lang." + qualifiedName);
            } catch (Exception e) {
                // ignored
            }
        }

        // try generics
        // TODO: generic declarations can shadow type declarations ... :(
        if (myType == null) {
            ASTTypeParameter parameter = getTypeParameterDeclaration(node, className);
            if (parameter != null) {
                node.setTypeDefinition(parameter.getTypeDefinition());
            }
        } else {
            node.setType(myType);
        }
    }

    private ASTTypeParameter getTypeParameterDeclaration(Node startNode, String image) {
        for (Node parent = startNode.jjtGetParent(); parent != null; parent = parent.jjtGetParent()) {
            ASTTypeParameters typeParameters = null;

            if (parent instanceof ASTTypeParameters) { // if type parameter defined in the same < >
                typeParameters = (ASTTypeParameters) parent;
            } else if (parent instanceof ASTConstructorDeclaration
                    || parent instanceof ASTMethodDeclaration
                    || parent instanceof ASTClassOrInterfaceDeclaration) {
                typeParameters = parent.getFirstChildOfType(ASTTypeParameters.class);
            }

            if (typeParameters != null) {
                for (int index = 0; index < typeParameters.jjtGetNumChildren(); ++index) {
                    String imageToCompareTo = typeParameters.jjtGetChild(index).getImage();
                    if (imageToCompareTo != null && imageToCompareTo.equals(image)) {
                        return (ASTTypeParameter) typeParameters.jjtGetChild(index);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check whether the supplied class name exists.
     */
    public boolean classNameExists(String fullyQualifiedClassName) {
        try {
            pmdClassLoader.loadClass(fullyQualifiedClassName);
            return true; // Class found
        } catch (ClassNotFoundException e) {
            return false;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public Class<?> loadClass(String fullyQualifiedClassName) {
        try {
            return pmdClassLoader.loadClass(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Class<?> processOnDemand(String qualifiedName) {
        for (String entry : importedOnDemand) {
            try {
                return pmdClassLoader.loadClass(entry + "." + qualifiedName);
            } catch (Throwable e) {
            }
        }
        return null;
    }

    private String getClassName(ASTCompilationUnit node) {
        ASTClassOrInterfaceDeclaration classDecl = node.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        if (classDecl == null) {
            // Happens if this compilation unit only contains an enum
            return null;
        }
        if (node.declarationsAreInDefaultPackage()) {
            return classDecl.getImage();
        }
        ASTPackageDeclaration pkgDecl = node.getPackageDeclaration();
        importedOnDemand.add(pkgDecl.getPackageNameImage());
        return pkgDecl.getPackageNameImage() + "." + classDecl.getImage();
    }

    /**
     * If the outer class wasn't found then we'll get in here
     *
     * @param node
     */
    private void populateImports(ASTCompilationUnit node) {
        List<ASTImportDeclaration> theImportDeclarations = node.findChildrenOfType(ASTImportDeclaration.class);

        importedClasses.putAll(JAVA_LANG);

        // go through the imports
        for (ASTImportDeclaration anImportDeclaration : theImportDeclarations) {
            String strPackage = anImportDeclaration.getPackageName();
            if (anImportDeclaration.isImportOnDemand()) {
                importedOnDemand.add(strPackage);
            } else if (!anImportDeclaration.isImportOnDemand()) {
                String strName = anImportDeclaration.getImportedName();
                importedClasses.put(strName, strName);
                importedClasses.put(strName.substring(strPackage.length() + 1), strName);
            }
        }
    }

    private void populateClassName(ASTCompilationUnit node, String className) throws ClassNotFoundException {
        node.setType(pmdClassLoader.loadClass(className));
        importedClasses.putAll(pmdClassLoader.getImportedClasses(className));
    }

}
