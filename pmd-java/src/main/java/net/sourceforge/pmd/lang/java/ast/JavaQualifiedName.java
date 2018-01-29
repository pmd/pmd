/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.QualifiedName;


/**
 * Unambiguous identifier for a java method or class. This implementation
 * approaches the qualified name format found in stack traces for example,
 * using a custom format specification (see {@link #ofString(String)}).
 *
 * <p>Instances of this class are immutable. They can be obtained from the
 * factory methods of this class, or from {@link JavaQualifiableNode#getQualifiedName()}
 * on AST nodes that support it.
 *
 * <p>Class qualified names follow the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-13.html#jls-13.1">binary name spec</a>.
 *
 * <p>Method qualified names don't follow a specification but allow to
 * distinguish overloads of the same method, using parameter types and order.
 */
public final class JavaQualifiedName implements QualifiedName {

    /**
     * Pattern specifying the format.
     *
     * <pre>
     *     ((\w+\.)*)               # packages
     *     (                        # classes
     *       (\w+)                  # primary class
     *       (
     *         \$                   # separator
     *         \d*+                 # optional local/anonymous class index
     *         (\D\w*+)?            # regular class name, absent for anonymous class
     *       )*
     *     )
     *     (                        # optional operation suffix
     *       \#
     *       (\w+)                  # method name
     *       \(
     *       (                      # parameters
     *         (\w+)
     *         (,\040\w+)*          # \040 is a space
     *       )?
     *       \)
     *     )?
     * </pre>
     */
    private static final Pattern FORMAT = Pattern.compile("((\\w+\\.)*)                        # packages\n"
                                                                  + "(                         # classes\n"
                                                                  + "  (\\w+)                  # primary class\n"
                                                                  + "  ("
                                                                  + "    \\$                   # separator\n"
                                                                  + "    \\d*+                 # optional local/anonymous class index\n"
                                                                  + "    (\\D\\w*+)?           # regular class name, absent for anonymous class\n"
                                                                  + "  )*"
                                                                  + ")"
                                                                  + "(                         # optional operation suffix\n"
                                                                  + "  \\#"
                                                                  + "  (\\w+)                  # method name\n"
                                                                  + "  \\("
                                                                  + "    (                     # parameters\n"
                                                                  + "      (\\w+)"
                                                                  + "      (,\\040\\w+)*       # \040 is a space\n"
                                                                  + "    )?"
                                                                  + "  \\)"
                                                                  + ")?", Pattern.COMMENTS);
    // indices of interesting groups in the regex
    private static final int PACKAGES_GROUP_INDEX = 1;
    private static final int CLASSES_GROUP_INDEX = 3;
    private static final int OPERATION_GROUP_INDEX = 7;
    private static final int PARAMETERS_GROUP_INDEX = 9;


    /** Local index value for when the class is not local. */
    private static final int NOTLOCAL_PLACEHOLDER = -1;

    // maps class names to the names of their local classes, to the count of local classes with the same name
    private static final Map<JavaQualifiedName, Map<String, Integer>> LOCAL_INDICES = new WeakHashMap<>();
    // maps class names to the current count of anonymous classes
    private static final Map<JavaQualifiedName, Integer> ANONYMOUS_INDICES = new WeakHashMap<>();
    // maps nodes of anonymous classes to their qualified name
    private static final Map<Node, JavaQualifiedName> ANONYMOUS_QNAMES = new WeakHashMap<>();

    private static final Pattern LOCAL_INDEX_PATTERN = Pattern.compile("(\\d+)(\\D\\w+)");

    private String[] packages = null; // unnamed package
    private String[] classes = new String[1];
    private String operation = null;
    // toString cache
    private String toString;


    /**
     * Local indices of the parents and of this class, in order.
     * They can be zipped with the {@link #classes} array.
     *
     * <p>If a class is not local, its local index is {@link #NOTLOCAL_PLACEHOLDER}.
     */
    private int[] localIndices = new int[1];


    private JavaQualifiedName() {
        localIndices[0] = NOTLOCAL_PLACEHOLDER;
    }


    /**
     * Resets global index counters, like anonymous and local class index counters.
     * Important to cleanup after a test, if some test cases have the same name.
     */
    /* test only */ static void resetGlobalIndexCounters() {
        LOCAL_INDICES.clear();
        ANONYMOUS_INDICES.clear();
    }



    @Override
    public boolean isClass() {
        return classes[0] != null && operation == null;
    }


    @Override
    public boolean isOperation() {
        return operation != null;
    }


    /**
     * Returns true if this qname identifies a local class.
     */
    public boolean isLocalClass() {
        return localIndices[localIndices.length - 1] != NOTLOCAL_PLACEHOLDER;
    }


    /**
     * Returns true if this qname identifies an anonymous class.
     */
    public boolean isAnonymousClass() {
        return !isLocalClass() && StringUtils.isNumeric(getClassSimpleName());
    }


    /**
     * Get the simple name of the class.
     */
    public String getClassSimpleName() {
        return classes[classes.length - 1];
    }


    /**
     * Returns the packages in order. This is specific to
     * Java's package structure. If the outer class is in
     * the unnamed package, returns {@code null}.
     *
     * @return The packages.
     */
    public String[] getPackages() {
        return packages == null ? null : Arrays.copyOf(packages, packages.length);
    }


    /**
     * Returns the class specific part of the name. It
     * identifies a class in the namespace it's declared
     * in. If the class is nested inside another, then
     * the array returned contains all enclosing classes
     * in order.
     *
     * @return The class names array.
     */
    public String[] getClasses() {
        return Arrays.copyOf(classes, classes.length);
    }


    /**
     * Returns the operation specific part of the name. It
     * identifies an operation in its namespace. Returns
     * {@code null} if {@link #isOperation()} returns false.
     *
     * @return The operation string, or {@code null}.
     */
    public String getOperation() {
        return operation;
    }


    @Override
    public JavaQualifiedName getClassName() {
        if (isClass()) {
            return this;
        }

        JavaQualifiedName qname = new JavaQualifiedName();
        qname.classes = this.classes;
        qname.packages = this.packages;
        return qname;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JavaQualifiedName that = (JavaQualifiedName) o;
        return Arrays.equals(packages, that.packages)
                && Arrays.equals(classes, that.classes)
                && Objects.equals(operation, that.operation)
                && Arrays.equals(localIndices, that.localIndices);
    }


    @Override
    public int hashCode() {
        int result = Objects.hash(operation);
        result = 31 * result + Arrays.hashCode(packages);
        result = 31 * result + Arrays.hashCode(classes);
        result = 31 * result + Arrays.hashCode(localIndices);
        return result;
    }


    /**
     * Returns the string representation of this qualified
     * name. The representation follows the format defined
     * for {@link #ofString(String)}.
     */
    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }

        StringBuilder sb = new StringBuilder();

        if (packages != null) {
            for (String aPackage : packages) {
                sb.append(aPackage).append('.');
            }
        }

        sb.append(classes[0]);

        for (int i = 1; i < classes.length; i++) {
            sb.append('$');

            if (localIndices[i] != NOTLOCAL_PLACEHOLDER) {
                sb.append(localIndices[i]);
            }

            sb.append(classes[i]);
        }

        if (operation != null) {
            sb.append('#').append(operation);
        }
        toString = sb.toString();
        return sb.toString();
    }


    //*****************
    // STATIC FACTORIES
    //*****************

    
    /**
     * Builds the qualified name of a method declaration.
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    static JavaQualifiedName ofOperation(ASTMethodDeclaration node) {
        JavaQualifiedName parentQname = node.getFirstParentOfType(ASTAnyTypeDeclaration.class)
                                            .getQualifiedName();

        return ofOperation(parentQname,
                           node.getMethodName(),
                           node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /**
     * Builds the qualified name of a constructor declaration.
     *
     * @param node The constructor declaration node
     *
     * @return The qualified name of the node
     */
    static JavaQualifiedName ofOperation(ASTConstructorDeclaration node) {
        ASTAnyTypeDeclaration parent = node.getFirstParentOfType(ASTAnyTypeDeclaration.class);

        return ofOperation(parent.getQualifiedName(),
                           parent.getImage(),
                           node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /** Factorises the functionality of ofOperation() */
    private static JavaQualifiedName ofOperation(JavaQualifiedName parent, String opName, ASTFormalParameters params) {
        JavaQualifiedName qname = new JavaQualifiedName();

        qname.packages = parent.packages;
        qname.classes = parent.classes;
        qname.operation = getOperationName(opName, params);

        return qname;
    }


    /**
     * Builds the qualified name of a type declaration.
     *
     * @param node The type declaration node.
     *
     * @return The qualified name of the declared type
     */
    static JavaQualifiedName ofClass(ASTAnyTypeDeclaration node) {
        if (node instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node).isLocal()) {
            return ofLocalClass((ASTClassOrInterfaceDeclaration) node);
        }
        return node.isNested() ? ofNestedClass(node) : ofOuterClass(node);
    }


    /**
     * Builds the qualified name of a nested class using
     * the qualified name of its immediate parent.
     *
     * @param node Nested class declaration
     *
     * @return The qualified name of the nested class
     */
    private static JavaQualifiedName ofNestedClass(ASTAnyTypeDeclaration node) {
        ASTAnyTypeDeclaration parent = node.getFirstParentOfType(ASTAnyTypeDeclaration.class);
        return notOuterClassQualifiedNameHelper(parent.getQualifiedName(),
                                                node.getImage(),
                                                NOTLOCAL_PLACEHOLDER);
    }


    /**
     * Builds the qualified name of a local class using
     * the qualified name of its immediate parent. Local
     * classes use a random suffix to prevent qualified
     * name collisions.
     *
     * @param node Local class declaration
     *
     * @return The qualified name of the local class
     */
    private static JavaQualifiedName ofLocalClass(ASTClassOrInterfaceDeclaration node) {
        JavaQualifiedName parent = findInnermostEnclosingTypeName(node);
        return notOuterClassQualifiedNameHelper(parent, node.getImage(), addLocal(parent, node.getImage()));
    }


    // works from the parent class qualified name to create a nested, anonymous or local class name
    // use NOTLOCAL_PLACEHOLDER if the class is not local
    private static JavaQualifiedName notOuterClassQualifiedNameHelper(JavaQualifiedName parent, String className, int localIndex) {
        JavaQualifiedName toBuild = new JavaQualifiedName();

        toBuild.packages = parent.packages;
        toBuild.classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        toBuild.classes[parent.classes.length] = className;

        // copy the local indices of the parents
        toBuild.localIndices = Arrays.copyOf(parent.localIndices, parent.localIndices.length + 1);
        toBuild.localIndices[parent.localIndices.length] = localIndex;

        return toBuild;
    }


    // Registers a class as local to the parent qualified name, and gives back its index.
    // The index identifies the local class in the scope of its parent.
    private static int addLocal(JavaQualifiedName parent, String localClassName) {
        Map<String, Integer> siblings = LOCAL_INDICES.get(parent);
        if (siblings == null) {
            LOCAL_INDICES.put(parent, new HashMap<String, Integer>());
            LOCAL_INDICES.get(parent).put(localClassName, 1);
            return 1;
        } else {
            Integer count = siblings.get(localClassName);
            if (count == null) {
                siblings.put(localClassName, 1);
                return 1;
            } else {
                siblings.put(localClassName, count + 1);
                return count + 1;
            }
        }
    }


    /**
     * Builds the qualified name of an outer (not nested) class.
     *
     * @param node The class node
     *
     * @return The qualified name of the node
     */
    private static JavaQualifiedName ofOuterClass(ASTAnyTypeDeclaration node) {
        ASTPackageDeclaration pkg = node.getFirstParentOfType(ASTCompilationUnit.class)
                                        .getFirstChildOfType(ASTPackageDeclaration.class);

        JavaQualifiedName qname = new JavaQualifiedName();
        qname.packages = pkg == null ? null : pkg.getPackageNameImage().split("\\.");
        qname.classes[0] = node.getImage();

        return qname;
    }


    /**
     * Gets the qualified name of this allocation expression's anonymous class.
     * This implementation gives a numeric identifier to the anonymous class.
     *
     * <p>This implementation only guarantees that
     * {@code (n1 == n2) entails (buildQNameOfAnonymousClass(n1) == buildQNameOfAnonymousClass(n2))}.
     * In particular, we do not guarantee that the number assigned to a class
     * will stay the same from a run to another. We do not provide a way to retrieve
     * the node from the qualified name.
     *
     * @param node Allocation expression declaring an anonymous class
     *
     * @return The qualified name of the class
     */
    public static JavaQualifiedName ofAnonymousClass(ASTAllocationExpression node) {
        return ofAnonymousClass((Node) node);
    }


    /**
     * Gets the qualified name of an enum constant declaring an anonymous class.
     * This implementation gives a numeric identifier to the anonymous class.
     *
     * @param node Enum constant declaring an anonymous class
     *
     * @return The qualified name of the class
     *
     * @see #ofAnonymousClass(ASTAllocationExpression)
     */
    public static JavaQualifiedName ofAnonymousClass(ASTEnumConstant node) {
        return ofAnonymousClass((Node) node);
    }


    /** Factorises factories. They both are kept separate to allow for node type specific documentation. */
    private static JavaQualifiedName ofAnonymousClass(Node node) {
        final JavaQualifiedName cached = ANONYMOUS_QNAMES.get(node);
        if (cached != null) {
            return cached;
        }

        JavaQualifiedName parentQName = node instanceof ASTAllocationExpression
                ? findInnermostEnclosingTypeName(node)
                : node.getFirstParentOfType(ASTEnumDeclaration.class).getQualifiedName();

        JavaQualifiedName newQName = buildQNameOfAnonymousClass(parentQName);
        ANONYMOUS_QNAMES.put(node, newQName); // cache result
        return newQName;
    }


    /**
     * Finds the qualified name of the directly enclosing type definition,
     * even if it's an anonymous class. This method must only be called if
     * such a parent exists.
     *
     * <p>Anonymous classes can only contain local classes, other anonymous
     * classes, and non-static (inner) classes. We therefore only call this
     * method in those cases.
     *
     * @param node Node of the type declaration whose enclosing type must be found.
     */
    private static JavaQualifiedName findInnermostEnclosingTypeName(Node node) {
        Node parent = node.jjtGetParent();
        while (parent != null
                && !(parent instanceof ASTClassOrInterfaceBody)
                && !(parent instanceof ASTEnumBody)) {
            parent = parent.jjtGetParent();
        }

        if (parent == null) {
            throw new IllegalStateException("The enclosing type declaration must exist.");
        }

        parent = parent.jjtGetParent();
        // The parent may now be an ASTEnumConstant, an ASTAllocationExpression,
        // an ASTEnumDeclaration or an ASTClassOrInterfaceDeclaration

        if (parent instanceof ASTAllocationExpression) {
            return ofAnonymousClass((ASTAllocationExpression) parent); // indirect recursive call
        } else if (parent instanceof ASTAnyTypeDeclaration) {
            return ((JavaQualifiableNode) parent).getQualifiedName();
        } else { // ASTEnumConstant
            return ofAnonymousClass((ASTEnumConstant) parent);
        }
    }


    /**
     * Create a new qualified name for an anonymous class using its parent.
     * The result is cached by the caller.
     */
    private static JavaQualifiedName buildQNameOfAnonymousClass(JavaQualifiedName parent) {
        // the class name of an anonymous class is entirely numeric
        return notOuterClassQualifiedNameHelper(parent, "" + getNextAnonymousIndex(parent), NOTLOCAL_PLACEHOLDER);
    }


    private static int getNextAnonymousIndex(JavaQualifiedName qname) {
        Integer count = ANONYMOUS_INDICES.get(qname);
        if (count == null) {
            ANONYMOUS_INDICES.put(qname, 1);
            return 1;
        } else {
            ANONYMOUS_INDICES.put(qname, count + 1);
            return count + 1;
        }
    }


    /**
     * Gets the qualified name of a class.
     *
     * @param clazz Class object
     *
     * @return The qualified name of the class, or null if the class is null
     */
    public static JavaQualifiedName ofClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        String name = clazz.getName();
        if (name.indexOf('.') < 0) {
            name = '.' + name; // unnamed package, marked by a full stop. See ofString's format below
        }

        return ofString(name);
    }


    /**
     * Parses a qualified name given in the format defined for this implementation. The format
     * is specified by a regex pattern (see {@link JavaQualifiedName#FORMAT}). Examples:
     *
     * <p>{@code com.company.MyClass$Nested#myMethod(String, int)}
     * <ul>
     * <li> Packages are separated by full stops;
     * <li> Nested classes are separated by a dollar symbol;
     * <li> The optional method suffix is separated from the class with a hashtag;
     * <li> Method arguments are separated by a comma and a single space.
     * </ul>
     *
     * <p>{@code MyClass$Nested}
     * <ul>
     * <li> The qualified name of a class in the unnamed package starts with the class name.
     * </ul>
     *
     * <p>{@code com.foo.Class$1LocalClass}
     * <ul>
     * <li> A local class' qualified name is assigned an index which identifies it within the scope
     * of its enclosing class. The index is displayed after the separating dollar symbol.
     * </ul>
     *
     * @param name The name to parse.
     *
     * @return A qualified name instance corresponding to the parsed string.
     */
    public static JavaQualifiedName ofString(String name) {
        JavaQualifiedName qname = new JavaQualifiedName();

        Matcher matcher = FORMAT.matcher(name);

        if (!matcher.matches()) {
            return null;
        }

        qname.packages = StringUtils.isBlank(matcher.group(PACKAGES_GROUP_INDEX)) ? null : matcher.group(PACKAGES_GROUP_INDEX).split("\\.");
        qname.operation = matcher.group(OPERATION_GROUP_INDEX) == null ? null : matcher.group(OPERATION_GROUP_INDEX).substring(1);

        qname.classes = matcher.group(CLASSES_GROUP_INDEX).split("\\$");
        qname.localIndices = new int[qname.classes.length];

        for (int i = 0; i < qname.classes.length; i++) {
            Matcher localIndexMatcher = LOCAL_INDEX_PATTERN.matcher(qname.classes[i]);
            if (localIndexMatcher.matches()) { // anonymous classes don't match, because there needs to be at least one non-digit
                qname.localIndices[i] = Integer.parseInt(localIndexMatcher.group(1));
                qname.classes[i] = localIndexMatcher.group(2);
            } else {
                qname.localIndices[i] = NOTLOCAL_PLACEHOLDER;
            }
        }

        return qname;
    }


    /** Returns a normalized method name (not Java-canonical!). */
    private static String getOperationName(String methodName, ASTFormalParameters params) {

        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');

        int last = params.getParameterCount() - 1;
        for (int i = 0; i < last; i++) {
            // append type image of param
            sb.append(params.jjtGetChild(i).getFirstDescendantOfType(ASTType.class).getTypeImage());
            sb.append(", ");
        }

        if (last > -1) {
            sb.append(params.jjtGetChild(last).getFirstDescendantOfType(ASTType.class).getTypeImage());
        }

        sb.append(')');

        return sb.toString();
    }
}
