/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import static net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution.getApplicableMethods;
import static net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution.getBestMethodReturnType;
import static net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution.getMethodExplicitTypeArugments;
import static net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution.isMemberVisibleFromClass;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.LOWER_WILDCARD;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.UPPER_BOUND;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.UPPER_WILDCARD;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
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
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSingleMemberAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledRule;
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
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardBounds;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;


//
// Helpful reading:
// http://www.janeg.ca/scjp/oper/promotions.html
// http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html
//

@Deprecated
@InternalApi
public class ClassTypeResolver extends JavaParserVisitorAdapter {

    private static final Logger LOG = Logger.getLogger(ClassTypeResolver.class.getName());

    private static final Map<String, Class<?>> PRIMITIVE_TYPES;
    private static final Map<String, String> JAVA_LANG;

    private Map<String, JavaTypeDefinition> staticFieldImageToTypeDef;
    private Map<String, List<JavaTypeDefinition>> staticNamesToClasses;
    private List<JavaTypeDefinition> importOnDemandStaticClasses;
    private ASTCompilationUnit currentAcu;

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
            currentAcu = node;
            importedOnDemand = new ArrayList<>();
            importedClasses = new HashMap<>();
            staticFieldImageToTypeDef = new HashMap<>();
            staticNamesToClasses = new HashMap<>();
            importOnDemandStaticClasses = new ArrayList<>();

            // TODO: this fails to account for multiple classes in the same file
            // later classes (in the ACU) won't have their Nested classes registered
            className = getClassName(node);
            if (className != null) {
                populateClassName(node, className);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
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
        ASTName importedType = (ASTName) node.getChild(0);

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
            QualifiableNode parent = node.getFirstParentOfAnyType(ASTAllocationExpression.class, ASTEnumConstant.class);

            if (parent != null) {
                typeName = parent.getQualifiedName().toString();
            }
        }

        // FIXME, we should discard the array depth on this node, it should only be known to ASTReferenceType (#910)
        populateType(node, typeName, node.getArrayDepth());

        ASTTypeArguments typeArguments = node.getFirstChildOfType(ASTTypeArguments.class);

        if (typeArguments != null) {
            final JavaTypeDefinition[] boundGenerics = new JavaTypeDefinition[typeArguments.getNumChildren()];
            for (int i = 0; i < typeArguments.getNumChildren(); ++i) {
                boundGenerics[i] = ((TypeNode) typeArguments.getChild(i)).getTypeDefinition();
            }

            node.setTypeDefinition(JavaTypeDefinition.forClass(node.getType(), boundGenerics));
        }

        return data;
    }

    /**
     * Set's the node's type to the found Class in the node's name (if there is a class to be found).
     *
     * @param node
     *
     * @return The index in the array produced by splitting the node's name by '.', which is not part of the
     * class name found. Example: com.package.SomeClass.staticField.otherField, return would be 3
     */
    private int searchNodeNameForClass(TypeNode node) {
        // this is the index from which field/method names start in the dotSplitImage array
        int startIndex = node.getImage().split("\\.").length;

        // tries to find a class in the node's image by omitting the parts after each '.', example:
        // First try: com.package.SomeClass.staticField.otherField
        // Second try: com.package.SomeClass.staticField
        // Third try: com.package.SomeClass <- found a class!
        for (String reducedImage = node.getImage();;) {
            populateType(node, reducedImage);
            if (node.getType() != null) {
                break; // we found a class!
            }

            // update the start index, so that code below knows where to start in the dotSplitImage array
            --startIndex;

            int lastDotIndex = reducedImage.lastIndexOf('.');

            if (lastDotIndex != -1) {
                reducedImage = reducedImage.substring(0, lastDotIndex);
            } else {
                break; // there is no class
            }
        }

        return startIndex;
    }

    private ASTArgumentList getArgumentList(ASTArguments args) {
        if (args != null) {
            return args.getFirstChildOfType(ASTArgumentList.class);
        }

        return null;
    }

    private int getArgumentListArity(ASTArgumentList argList) {
        if (argList != null) {
            return argList.getNumChildren();
        }

        return 0;
    }

    @Override
    public Object visit(ASTName node, Object data) {
        Class<?> accessingClass = getEnclosingTypeDeclarationClass(node);
        String[] dotSplitImage = node.getImage().split("\\.");

        int startIndex = searchNodeNameForClass(node);

        ASTArguments astArguments = getSuffixMethodArgs(node);
        ASTArgumentList astArgumentList = getArgumentList(astArguments);
        int methodArgsArity = getArgumentListArity(astArgumentList);

        JavaTypeDefinition previousType;

        if (node.getType() != null) { // static field or method
            // node.getType() has been set by the call to searchNodeNameForClass above
            // node.getType() will have the value equal to the Class found by that method
            previousType = node.getTypeDefinition();
        } else { // non-static field or method
            if (dotSplitImage.length == 1 && astArguments != null) { // method
                List<MethodType> methods = getLocalApplicableMethods(node, dotSplitImage[0],
                                                                     Collections.<JavaTypeDefinition>emptyList(),
                                                                     methodArgsArity, accessingClass);

                TypeNode enclosingType = getEnclosingTypeDeclaration(node);
                if (enclosingType == null) {
                    return data; // we can't proceed, probably uncompiled sources
                }

                previousType = getBestMethodReturnType(enclosingType.getTypeDefinition(),
                                                       methods, astArgumentList);
            } else { // field
                previousType = getTypeDefinitionOfVariableFromScope(node.getScope(), dotSplitImage[0],
                                                                    accessingClass);
            }
            startIndex = 1; // first element's type in dotSplitImage has already been resolved
        }

        // TODO: remove this if branch, it's only purpose is to make JUnitAssertionsShouldIncludeMessage's tests pass
        //       as the code is not compiled there and symbol table works on uncompiled code
        if (node.getNameDeclaration() != null
                && previousType == null // if it's not null, then let other code handle things
                && node.getNameDeclaration().getNode() instanceof TypeNode) {
            // Carry over the type (including generics) from the declaration
            JavaTypeDefinition nodeType = ((TypeNode) node.getNameDeclaration().getNode()).getTypeDefinition();
            if (nodeType != null) {
                node.setTypeDefinition(nodeType);
                return super.visit(node, data);
            }
        }

        for (int i = startIndex; i < dotSplitImage.length; ++i) {
            if (previousType == null) {
                break;
            }

            if (i == dotSplitImage.length - 1 && astArguments != null) { // method
                List<MethodType> methods = getApplicableMethods(previousType, dotSplitImage[i],
                                                                Collections.<JavaTypeDefinition>emptyList(),
                                                                methodArgsArity, accessingClass);

                previousType = getBestMethodReturnType(previousType, methods, astArgumentList);
            } else { // field
                previousType = getFieldType(previousType, dotSplitImage[i], accessingClass);
            }
        }

        if (previousType != null) {
            node.setTypeDefinition(previousType);
        }

        return super.visit(node, data);
    }

    /**
     * This method looks for method invocations be simple name.
     * It searches outwards class declarations and their supertypes and in the end, static method imports.
     * Compiles a list of potentially applicable methods.
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.12.1
     */
    private List<MethodType> getLocalApplicableMethods(TypeNode node, String methodName,
                                                       List<JavaTypeDefinition> typeArguments,
                                                       int argArity,
                                                       Class<?> accessingClass) {
        List<MethodType> foundMethods = new ArrayList<>();

        if (accessingClass == null) {
            return foundMethods;
        }

        // we search each enclosing type declaration, looking at their supertypes as well
        for (node = getEnclosingTypeDeclaration(node); node != null;
             node = getEnclosingTypeDeclaration(node.getParent())) {

            foundMethods.addAll(getApplicableMethods(node.getTypeDefinition(), methodName, typeArguments,
                                                     argArity, accessingClass));
        }

        foundMethods.addAll(searchImportedStaticMethods(methodName, typeArguments, argArity, accessingClass));

        return foundMethods;
    }

    private List<MethodType> searchImportedStaticMethods(String methodName,
                                                         List<JavaTypeDefinition> typeArguments,
                                                         int argArity,
                                                         Class<?> accessingClass) {
        List<MethodType> foundMethods = new ArrayList<>();

        // TODO: member methods must not be looked at in the code below
        // TODO: add support for properly dealing with shadowing
        List<JavaTypeDefinition> explicitImports = staticNamesToClasses.get(methodName);

        if (explicitImports != null) {
            for (JavaTypeDefinition anImport : explicitImports) {
                foundMethods.addAll(getApplicableMethods(anImport, methodName, typeArguments, argArity,
                                                         accessingClass));
            }
        }

        if (!foundMethods.isEmpty()) {
            // if we found an method by explicit imports, on deamand imports mustn't be searched, because
            // explicit imports shadow them by name, regardless of method parameters
            return foundMethods;
        }

        for (JavaTypeDefinition anOnDemandImport : importOnDemandStaticClasses) {
            foundMethods.addAll(getApplicableMethods(anOnDemandImport, methodName, typeArguments, argArity,
                                                     accessingClass));
        }

        return foundMethods;
    }


    /**
     * This method can be called on a prefix
     */
    private ASTArguments getSuffixMethodArgs(Node node) {
        Node prefix = node.getParent();

        if (prefix instanceof ASTPrimaryPrefix
                && prefix.getParent().getNumChildren() >= 2) {
            return prefix.getParent().getChild(1).getFirstChildOfType(ASTArguments.class);
        }

        return null;
    }

    /**
     * Searches a JavaTypeDefinition and it's superclasses until a field with name {@code fieldImage} that
     * is visible from the {@code accessingClass} class. Once it's found, it's possibly generic type is
     * resolved with the help of {@code typeToSearch} TypeDefinition.
     *
     * @param typeToSearch   The type def. to search the field in.
     * @param fieldImage     The simple name of the field.
     * @param accessingClass The class that is trying to access the field, some Class declared in the current ACU.
     *
     * @return JavaTypeDefinition of the resolved field or null if it could not be found.
     */
    private JavaTypeDefinition getFieldType(JavaTypeDefinition typeToSearch, String fieldImage, Class<?>
            accessingClass) {
        while (typeToSearch != null && typeToSearch.getType() != Object.class) {
            try {
                final Field field = typeToSearch.getType().getDeclaredField(fieldImage);
                if (isMemberVisibleFromClass(typeToSearch.getType(), field.getModifiers(), accessingClass)) {
                    return typeToSearch.resolveTypeDefinition(field.getGenericType());
                }
            } catch (final NoSuchFieldException ignored) {
                // swallow
            } catch (final LinkageError e) {
                if (LOG.isLoggable(Level.WARNING)) {
                    String message = "Error during type resolution of field '" + fieldImage + "' in "
                            + typeToSearch.getType() + " due to: " + e;
                    LOG.log(Level.WARNING, message);
                }
                // TODO : report a missing class once we start doing that...
                return null;
            }

            // transform the type into it's supertype
            typeToSearch = typeToSearch.resolveTypeDefinition(typeToSearch.getType().getGenericSuperclass());
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
     *
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
                        // TODO : Type is inferred, ie, this is a lambda such as (var) -> var.equals(other) or a local var
                        return null;
                    }

                    if (typeNode.getChild(0) instanceof ASTReferenceType) {
                        return ((TypeNode) typeNode.getChild(0)).getTypeDefinition();
                    } else { // primitive type
                        return JavaTypeDefinition.forClass(typeNode.getType());
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
                } catch (ClassCastException ignored) {
                    // if there is an anonymous class, getClassDeclaration().getType() will throw
                    // TODO: maybe there is a better way to handle this, maybe this hides bugs
                }
            }
        }

        return searchImportedStaticFields(image); // will return null if not found
    }

    private JavaTypeDefinition searchImportedStaticFields(String fieldName) {
        if (staticFieldImageToTypeDef.containsKey(fieldName)) {
            return staticFieldImageToTypeDef.get(fieldName);
        }

        for (JavaTypeDefinition anOnDemandImport : importOnDemandStaticClasses) {
            JavaTypeDefinition typeDef = getFieldType(anOnDemandImport, fieldName, currentAcu.getType());
            if (typeDef != null) {
                staticFieldImageToTypeDef.put(fieldName, typeDef);
                return typeDef;
            }
        }

        return null;
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
        if (node == null || node.isTypeInferred()) {
            return super.visit(node, data);
        }

        // Type common to all declarations in the same statement
        JavaTypeDefinition baseType = node.getTypeNode().getTypeDefinition();

        if (baseType != null) {
            // add the dimensions specific to the declarator id
            node.setTypeDefinition(baseType.withDimensions(node.getArrayDepth()));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTType node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    private void populateVariableDeclaratorFromType(ASTLocalVariableDeclaration node, JavaTypeDefinition typeDefinition) {
        // assign this type to VariableDeclarator and VariableDeclaratorId
        TypeNode var = node.getFirstChildOfType(ASTVariableDeclarator.class);
        if (var != null) {
            var.setTypeDefinition(typeDefinition);
            var = var.getFirstChildOfType(ASTVariableDeclaratorId.class);
        }
        if (var != null) {
            var.setTypeDefinition(typeDefinition);
        }
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        super.visit(node, data);
        // resolve "var" types: Upward projection of the type of the initializer expression
        ASTType type = node.getTypeNode();
        if (type == null) {
            // no type node -> type is inferred
            ASTVariableInitializer initializer = node.getFirstDescendantOfType(ASTVariableInitializer.class);
            if (initializer != null && initializer.getChild(0) instanceof ASTExpression) {
                // only Expression is allowed, ArrayInitializer is not allowed in combination with "var".
                ASTExpression expression = (ASTExpression) initializer.getChild(0);
                populateVariableDeclaratorFromType(node, expression.getTypeDefinition());
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        super.visit(node, data);
        // resolve potential "var" type
        if (node.getChild(0) instanceof ASTLocalVariableDeclaration) {
            ASTLocalVariableDeclaration localVariableDeclaration = (ASTLocalVariableDeclaration) node.getChild(0);
            ASTType type = localVariableDeclaration.getTypeNode();
            if (type == null) {
                // no type node -> type is inferred
                ASTExpression expression = node.getFirstChildOfType(ASTExpression.class);
                if (expression != null && expression.getTypeDefinition() != null) {
                    // see https://docs.oracle.com/javase/specs/jls/se10/html/jls-14.html#jls-14.14.2
                    // if the type is an array, then take the component type
                    // if the type is Iterable<X>, then take X as type
                    // if the type is Iterable, take Object as type
                    JavaTypeDefinition typeDefinitionIterable = expression.getTypeDefinition();
                    JavaTypeDefinition typeDefinition = null;
                    if (typeDefinitionIterable.isArrayType()) {
                        typeDefinition = typeDefinitionIterable.getComponentType();
                    } else if (typeDefinitionIterable.isGeneric() && typeDefinitionIterable.getGenericType(0) != null) {
                        typeDefinition = typeDefinitionIterable.getGenericType(0);
                    } else {
                        typeDefinition = JavaTypeDefinition.forClass(Object.class);
                    }
                    populateVariableDeclaratorFromType(localVariableDeclaration, typeDefinition);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTResource node, Object data) {
        super.visit(node, data);
        // resolve "var" types: the type of the initializer expression
        ASTType type = node.getTypeNode();
        if (type == null) {
            // no type node -> type is inferred
            ASTExpression initializer = node.getFirstChildOfType(ASTExpression.class);

            if (node.getVariableDeclaratorId() != null) {
                node.getVariableDeclaratorId().setTypeDefinition(initializer.getTypeDefinition());
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTReferenceType node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);

        JavaTypeDefinition elementTypeDef = node.getTypeDefinition();
        if (elementTypeDef != null) {
            // FIXME when ClassOrInterfaceType resolves type without dimensions, remove the test here
            if (!elementTypeDef.isArrayType()) {
                node.setTypeDefinition(elementTypeDef.withDimensions(node.getArrayDepth()));
            }
        }
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

        // TODO Rules for Ternary are complex

        rollupTypeUnary(node);

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
        // visit method arguments in reverse
        for (int i = primaryNode.getNumChildren() - 1; i >= 0; --i) {
            ((JavaNode) primaryNode.getChild(i)).jjtAccept(this, data);
        }

        JavaTypeDefinition primaryNodeType = null;
        AbstractJavaTypeNode previousChild = null;
        AbstractJavaTypeNode nextChild;
        Class<?> accessingClass = getEnclosingTypeDeclarationClass(primaryNode);

        for (int childIndex = 0; childIndex < primaryNode.getNumChildren(); ++childIndex) {
            AbstractJavaTypeNode currentChild = (AbstractJavaTypeNode) primaryNode.getChild(childIndex);
            nextChild = childIndex + 1 < primaryNode.getNumChildren()
                    ? (AbstractJavaTypeNode) primaryNode.getChild(childIndex + 1) : null;

            // skip children which already have their type assigned
            if (currentChild.getType() == null) {
                // Last token, because if 'this' is a Suffix, it'll have tokens '.' and 'this'
                if (currentChild.jjtGetLastToken().toString().equals("this")) {

                    if (previousChild != null) { // Qualified 'this' expression
                        currentChild.setTypeDefinition(previousChild.getTypeDefinition());
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

                } else if (currentChild.getFirstChildOfType(ASTArguments.class) != null) {
                    currentChild.setTypeDefinition(previousChild.getTypeDefinition());
                } else if (previousChild != null && previousChild.getType() != null) {
                    String currentChildImage = currentChild.getImage();
                    if (currentChildImage == null) {
                        // this.<Something>foo(); <Something>foo would be in a Suffix and would have a null image
                        currentChildImage = currentChild.jjtGetLastToken().toString();
                    }

                    ASTArguments astArguments = nextChild != null
                            ? nextChild.getFirstChildOfType(ASTArguments.class) : null;

                    if (astArguments != null) { // method
                        ASTArgumentList astArgumentList = getArgumentList(astArguments);
                        int methodArgsArity = getArgumentListArity(astArgumentList);
                        List<JavaTypeDefinition> typeArguments = getMethodExplicitTypeArugments(currentChild);

                        List<MethodType> methods = getApplicableMethods(previousChild.getTypeDefinition(),
                                                                        currentChildImage,
                                                                        typeArguments, methodArgsArity, accessingClass);

                        currentChild.setTypeDefinition(getBestMethodReturnType(previousChild.getTypeDefinition(),
                                                                               methods, astArgumentList));
                    } else { // field
                        currentChild.setTypeDefinition(getFieldType(previousChild.getTypeDefinition(),
                                                                    currentChildImage, accessingClass));
                    }
                }
            }


            if (currentChild.getType() != null) {
                // rollup type from the child: PrimaryPrefix/PrimarySuffx -> PrimaryExpression
                if (primaryNodeType == null || !primaryNodeType.isArrayType()) {
                    primaryNodeType = currentChild.getTypeDefinition();
                }

                // if this expression is a method call, then make sure, PrimaryPrefix has the type
                // on which the method is executed (type of the target reference)
                if (currentChild.getFirstChildOfType(ASTArguments.class) != null && previousChild.getFirstChildOfType(ASTName.class) != null) {
                    // restore type of the name and search again
                    ASTName name = previousChild.getFirstChildOfType(ASTName.class);
                    name.setTypeDefinition(null);
                    searchNodeNameForClass(name);
                    if (name.getTypeDefinition() != null) {
                        // rollup from Name -> PrimaryPrefix
                        previousChild.setTypeDefinition(name.getTypeDefinition());
                    } else if (name.getTypeDefinition() == null) {
                        // if there is no better type, use the type of the expression
                        name.setTypeDefinition(primaryNodeType);
                    }
                }

                // maybe array access?
                if (primaryNodeType != null && primaryNodeType.isArrayType()) {
                    if (currentChild instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) currentChild).isArrayDereference()) {
                        primaryNodeType = JavaTypeDefinition.forClass(primaryNodeType.getType().getComponentType());
                    }
                }
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
     * Returns the the first Class declaration around the node.
     *
     * @param node The node with the enclosing Class declaration.
     *
     * @return The JavaTypeDefinition of the enclosing Class declaration.
     */
    private TypeNode getEnclosingTypeDeclaration(Node node) {
        Node previousNode = null;

        while (node != null) {
            if (node instanceof ASTClassOrInterfaceDeclaration) {
                return (TypeNode) node;
                // anonymous class declaration
            } else if (node instanceof ASTAllocationExpression // is anonymous class declaration
                    && node.getFirstChildOfType(ASTArrayDimsAndInits.class) == null // array cant be anonymous
                    && !(previousNode instanceof ASTArguments)) { // we might come out of the constructor
                return (TypeNode) node;
            }

            previousNode = node;
            node = node.getParent();
        }

        return null;
    }

    private Class<?> getEnclosingTypeDeclarationClass(Node node) {
        TypeNode typeDecl = getEnclosingTypeDeclaration(node);

        if (typeDecl == null) {
            return null;
        } else {
            return typeDecl.getType();
        }
    }


    /**
     * Get the type def. of the super class of the enclosing type declaration which has the same class
     * as the second argument, or if the second argument is null, then anonymous classes are considered
     * as well and the first enclosing scope's super class is returned.
     *
     * @param node  The node from which to start searching.
     * @param clazz The type of the enclosing class.
     *
     * @return The TypeDefinition of the superclass.
     */
    private JavaTypeDefinition getSuperClassTypeDefinition(Node node, Class<?> clazz) {
        Node previousNode = null;
        for (; node != null; previousNode = node, node = node.getParent()) {
            if (node instanceof ASTClassOrInterfaceDeclaration // class declaration
                    // is the class we are looking for or caller requested first class
                    && (((TypeNode) node).getType() == clazz || clazz == null)) {

                ASTExtendsList extendsList = node.getFirstChildOfType(ASTExtendsList.class);

                if (extendsList != null) {
                    return ((TypeNode) extendsList.getChild(0)).getTypeDefinition();
                } else {
                    return JavaTypeDefinition.forClass(Object.class);
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
    public Object visit(ASTPrimarySuffix node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);

        return data;
    }

    @Override
    public Object visit(ASTTypeArgument node, Object data) {
        if (node.getNumChildren() == 0) { // if type argument is '?'
            node.setTypeDefinition(JavaTypeDefinition.forClass(UPPER_WILDCARD, Object.class));
        } else {
            super.visit(node, data);
            rollupTypeUnary(node);
        }

        return data;
    }

    @Override
    public Object visit(ASTWildcardBounds node, Object data) {
        super.visit(node, data);

        JavaTypeDefinition childType = node.getTypeBoundNode().getTypeDefinition();

        if (node.isLowerBound()) {
            node.setTypeDefinition(JavaTypeDefinition.forClass(LOWER_WILDCARD, childType));
        } else { // upper bound
            node.setTypeDefinition(JavaTypeDefinition.forClass(UPPER_WILDCARD, childType));
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeParameters node, Object data) {
        super.visit(node, data);

        if (node.getParent() instanceof ASTClassOrInterfaceDeclaration) {
            TypeNode parent = (TypeNode) node.getParent();

            final JavaTypeDefinition[] boundGenerics = new JavaTypeDefinition[node.getNumChildren()];
            int i = 0;
            for (ASTTypeParameter arg : node) {
                boundGenerics[i++] = arg.getTypeDefinition();
            }

            parent.setTypeDefinition(JavaTypeDefinition.forClass(parent.getType(), boundGenerics));
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeParameter node, Object data) {
        if (!node.hasTypeBound()) { // type parameter doesn't have declared upper bounds
            node.setTypeDefinition(JavaTypeDefinition.forClass(UPPER_BOUND, Object.class));
        } else {
            super.visit(node, data);
            rollupTypeUnary(node);
        }

        return data;
    }

    @Override
    public Object visit(ASTTypeBound node, Object data) {
        super.visit(node, data);

        List<ASTClassOrInterfaceType> typeNodes = node.getBoundTypeNodes();

        // TypeBound will have at least one child, but maybe more
        JavaTypeDefinition[] bounds = new JavaTypeDefinition[typeNodes.size()];
        int i = 0;
        for (ASTClassOrInterfaceType bound : typeNodes) {
            bounds[i++] = bound.getTypeDefinition();
        }

        node.setTypeDefinition(JavaTypeDefinition.forClass(UPPER_BOUND, bounds));

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
        if (node.getNumChildren() != 0) {
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

        final ASTArrayDimsAndInits dims = node.getFirstChildOfType(ASTArrayDimsAndInits.class);
        if (dims != null) {
            final JavaTypeDefinition elementType = ((TypeNode) node.getChild(0)).getTypeDefinition();
            if (elementType != null) {
                node.setTypeDefinition(elementType.withDimensions(dims.getArrayDepth()));
            }
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
    public Object visit(ASTSwitchExpression node, Object data) {
        super.visit(node, data);

        JavaTypeDefinition type = null;
        // first try to determine the type based on the first expression/break/yield of a switch rule
        List<ASTSwitchLabeledRule> rules = node.findChildrenOfType(ASTSwitchLabeledRule.class);
        for (ASTSwitchLabeledRule rule : rules) {
            Node body = rule.getChild(1); // second child is either Expression, Block, ThrowStatement
            if (body instanceof ASTExpression) {
                type = ((ASTExpression) body).getTypeDefinition();
                break;
            } else if (body instanceof ASTBlock) {
                List<ASTBreakStatement> breaks = body.findDescendantsOfType(ASTBreakStatement.class);
                if (!breaks.isEmpty()) {
                    ASTExpression expression = breaks.get(0).getFirstChildOfType(ASTExpression.class);
                    if (expression != null) {
                        type = expression.getTypeDefinition();
                        break;
                    }
                }
                List<ASTYieldStatement> yields = body.findDescendantsOfType(ASTYieldStatement.class);
                if (!yields.isEmpty()) {
                    ASTExpression expression = yields.get(0).getFirstChildOfType(ASTExpression.class);
                    if (expression != null) {
                        type = expression.getTypeDefinition();
                        break;
                    }
                }
            }
        }
        if (type == null) {
            // now check the labels and their expressions of break/yield statements
            for (int i = 0; i < node.getNumChildren(); i++) {
                Node child = node.getChild(i);
                if (child instanceof ASTBlockStatement) {
                    List<ASTBreakStatement> breaks = child.findDescendantsOfType(ASTBreakStatement.class);
                    if (!breaks.isEmpty()) {
                        ASTExpression expression = breaks.get(0).getFirstChildOfType(ASTExpression.class);
                        if (expression != null) {
                            type = expression.getTypeDefinition();
                            break;
                        }
                    }
                    List<ASTYieldStatement> yields = child.findDescendantsOfType(ASTYieldStatement.class);
                    if (!yields.isEmpty()) {
                        ASTExpression expression = yields.get(0).getFirstChildOfType(ASTExpression.class);
                        if (expression != null && expression.getTypeDefinition() != null) {
                            type = expression.getTypeDefinition();
                            break;
                        }
                    }
                }
            }
        }

        node.setTypeDefinition(type);
        return data;
    }


    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        super.visit(node, data);
        JavaTypeDefinition varType = node.getVariableDeclaratorId().getTypeDefinition();

        if (varType != null) {
            if (node.isVarargs()) {
                // The type of the formal parameter is defined in terms of the type
                // of the declarator ID
                node.getVariableDeclaratorId().setTypeDefinition(varType.withDimensions(1));
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTAnnotation node, Object data) {
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

    @Override
    public Object visit(ASTYieldStatement node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    // Roll up the type based on type of the first child node.
    private void rollupTypeUnary(TypeNode typeNode) {
        if (typeNode.getNumChildren() >= 1) {
            Node child = typeNode.getChild(0);
            if (child instanceof TypeNode) {
                typeNode.setTypeDefinition(((TypeNode) child).getTypeDefinition());
            }
        }
    }

    // Roll up the type based on type of the first child node using Unary
    // Numeric Promotion per JLS 5.6.1
    private void rollupTypeUnaryNumericPromotion(TypeNode typeNode) {
        Node node = typeNode;
        if (node.getNumChildren() >= 1) {
            Node child = node.getChild(0);
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
        if (node.getNumChildren() >= 2) {
            Node child1 = node.getChild(0);
            Node child2 = node.getChild(1);
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
        populateType(node, className, 0);
    }

    private void populateType(TypeNode node, String className, int arrayDimens) {

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
                } catch (LinkageError e) {
                    // we found the class, but there is a problem with it (see https://github.com/pmd/pmd/issues/1131)
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "Tried to load class " + qualifiedName + " from on demand import, "
                                + "with an incomplete classpath.", e);
                    }
                    return;
                }
            }
        }
        if (myType == null && qualifiedName != null && qualifiedName.contains(".")) {
            // try if the last part defines a inner class
            String qualifiedNameInner = qualifiedName.substring(0, qualifiedName.lastIndexOf('.')) + "$"
                    + qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
            try {
                myType = pmdClassLoader.loadClass(qualifiedNameInner);
            } catch (ClassNotFoundException ignored) {
                // ignored, we'll try again with a different package name/fqcn
            } catch (LinkageError e) {
                // we found the class, but there is a problem with it (see https://github.com/pmd/pmd/issues/1131)
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Tried to load class " + qualifiedNameInner + " from on demand import, "
                            + "with an incomplete classpath.", e);
                }
                return;
            }
        }
        if (myType == null && qualifiedName != null && !qualifiedName.contains(".")) {
            // try again with java.lang....
            try {
                myType = pmdClassLoader.loadClass("java.lang." + qualifiedName);
            } catch (Exception ignored) {
                // ignored, we'll try again with generics
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
            JavaTypeDefinition def = JavaTypeDefinition.forClass(myType);
            if (def != null) {
                node.setTypeDefinition(def.withDimensions(arrayDimens));
            }
        }
    }

    private ASTTypeParameter getTypeParameterDeclaration(Node startNode, String image) {
        for (Node parent = startNode.getParent(); parent != null; parent = parent.getParent()) {
            ASTTypeParameters typeParameters = null;

            if (parent instanceof ASTTypeParameters) { // if type parameter defined in the same < >
                typeParameters = (ASTTypeParameters) parent;
            } else if (parent instanceof ASTConstructorDeclaration
                    || parent instanceof ASTMethodDeclaration
                    || parent instanceof ASTClassOrInterfaceDeclaration) {
                typeParameters = parent.getFirstChildOfType(ASTTypeParameters.class);
            }

            if (typeParameters != null) {
                for (int index = 0; index < typeParameters.getNumChildren(); ++index) {
                    String imageToCompareTo = typeParameters.getChild(index).getImage();
                    if (imageToCompareTo != null && imageToCompareTo.equals(image)) {
                        return (ASTTypeParameter) typeParameters.getChild(index);
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
        } catch (LinkageError e2) {
            // Class exists, but may be invalid (see https://github.com/pmd/pmd/issues/1131)
            return true;
        }
    }

    public Class<?> loadClass(String fullyQualifiedClassName) {
        try {
            return pmdClassLoader.loadClass(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (LinkageError e2) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Tried to load class " + fullyQualifiedClassName + " from on demand import, "
                        + "with an incomplete classpath.", e2);
            }
            return null;
        }
    }

    private Class<?> processOnDemand(String qualifiedName) {
        for (String entry : importedOnDemand) {
            String fullClassName = entry + "." + qualifiedName;
            try {
                return pmdClassLoader.loadClass(fullClassName);
            } catch (ClassNotFoundException ignored) {
                // ignored
            } catch (LinkageError e) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Tried to load class " + fullClassName + " from on demand import, "
                            + "with an incomplete classpath.", e);
                }
            }
        }
        return null;
    }

    private String getClassName(ASTCompilationUnit node) {
        ASTAnyTypeDeclaration classDecl = node.getFirstDescendantOfType(ASTAnyTypeDeclaration.class);
        if (classDecl == null) {
            // package-info.java?
            return null;
        }


        if (node.getPackageName().isEmpty()) {
            return classDecl.getSimpleName();
        }
        importedOnDemand.add(node.getPackageName());
        return classDecl.getBinaryName();
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
            if (anImportDeclaration.isStatic()) {
                if (anImportDeclaration.isImportOnDemand()) {
                    importOnDemandStaticClasses.add(JavaTypeDefinition.forClass(loadClass(strPackage)));
                } else { // not import on-demand
                    String strName = anImportDeclaration.getImportedName();
                    String fieldName = strName.substring(strName.lastIndexOf('.') + 1);

                    Class<?> staticClassWithField = loadClass(strPackage);
                    if (staticClassWithField != null) {
                        JavaTypeDefinition typeDef = getFieldType(JavaTypeDefinition.forClass(staticClassWithField),
                                                                  fieldName, currentAcu.getType());
                        staticFieldImageToTypeDef.put(fieldName, typeDef);
                    }

                    List<JavaTypeDefinition> typeList = staticNamesToClasses.get(fieldName);

                    if (typeList == null) {
                        typeList = new ArrayList<>();
                    }

                    typeList.add(JavaTypeDefinition.forClass(staticClassWithField));

                    staticNamesToClasses.put(fieldName, typeList);
                }
            } else { // non-static
                if (anImportDeclaration.isImportOnDemand()) {
                    importedOnDemand.add(strPackage);
                } else { // not import on-demand
                    String strName = anImportDeclaration.getImportedName();
                    importedClasses.put(strName, strName);
                    importedClasses.put(strName.substring(strPackage.length() + 1), strName);
                }
            }
        }
    }


    private void populateClassName(ASTCompilationUnit node, String className) throws ClassNotFoundException {
        node.setType(pmdClassLoader.loadClass(className));
        importedClasses.putAll(pmdClassLoader.getImportedClasses(className));
    }
}
