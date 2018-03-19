/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ImmutableList.ListFactory;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode.MethodLikeKind;


/**
 * Static factory methods for JavaQualifiedName.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public final class QualifiedNameFactory {

    /** Operation part of a lambda. */
    private static final String LAMBDA_PATTERN = "lambda\\$(\\w++)?\\$\\d++";
    private static final Pattern COMPILED_LAMBDA_PATTERN = Pattern.compile(LAMBDA_PATTERN);


    /**
     * Pattern specifying the format.
     *
     * <pre>
     *     ((\w++\.)*)              # packages
     *     (                        # classes
     *       (\w++)                 # primary class
     *       (
     *         \$                   # separator
     *         \d*+                 # optional local/anonymous class index
     *         (\D\w*+)?            # regular class name, absent for anonymous class
     *       )*
     *     )
     *     (                        # optional operation suffix
     *       \#
     *       (
     *         lambda\$(\w++)\$\d++ # name of a lambda
     *       |
     *         (\w++)               # method name
     *         \(
     *         (                    # parameters
     *           (\w++)
     *           (,\040\w++)*       # \040 is a space
     *         )?
     *         \)
     *       )
     *     )?
     * </pre>
     */
    private static final Pattern FORMAT = Pattern.compile("((\\w++\\.)*)                       # packages\n"  // don't forget to edit the javadoc upon change
                                                                  + "(                         # classes\n"
                                                                  + "  (\\w++)                 # primary class\n"
                                                                  + "  ("
                                                                  + "    \\$                   # separator\n"
                                                                  + "    \\d*+                 # optional local/anonymous class index\n"
                                                                  + "    ([a-zA-Z]\\w*+)?      # regular class name, absent for anonymous class\n"
                                                                  + "  )*"
                                                                  + ")"
                                                                  + "(                         # optional operation suffix\n"
                                                                  + "  \\#"
                                                                  + "  ("
                                                                  + "   " + LAMBDA_PATTERN + " # name of a lambda\n"
                                                                  + "  |  "
                                                                  + "    (\\w++)               # method name\n"
                                                                  + "    \\("
                                                                  + "    (                     # parameters\n"
                                                                  + "      (\\w++)"
                                                                  + "      (,\\040\\w++)*      # \040 is a space\n"
                                                                  + "    )?"
                                                                  + "    \\)"
                                                                  + "  )"
                                                                  + ")?", Pattern.COMMENTS);
    // indices of interesting groups in the regex
    private static final int PACKAGES_GROUP_INDEX = 1;
    private static final int CLASSES_GROUP_INDEX = 3;
    private static final int OPERATION_GROUP_INDEX = 7;
    // TODO we need a visitor to remove this mess

    // maps class names to the names of their local classes, to the count of local classes with the same name
    private static final Map<JavaQualifiedName, Map<String, Integer>> LOCAL_INDICES = new WeakHashMap<>();

    // maps class names to the current lambda count
    private static final Map<JavaQualifiedName, Integer> LAMBDA_INDICES = new WeakHashMap<>();

    // maps class names to the current count of anonymous classes
    private static final Map<JavaQualifiedName, Integer> ANONYMOUS_INDICES = new WeakHashMap<>();

    // maps nodes of anonymous classes to their qualified name
    private static final Map<Node, JavaQualifiedName> ANONYMOUS_QNAMES = new WeakHashMap<>();

    private static final Pattern LOCAL_INDEX_PATTERN = Pattern.compile("(\\d+)(\\D\\w+)");


    private QualifiedNameFactory() {

    }


    /**
     * Resets global index counters, like anonymous and
     * local class index counters. Important to cleanup
     * after a test, if some test cases classes have the
     * same name.
     */
    /* test only */
    static void resetGlobalIndexCounters() {
        LOCAL_INDICES.clear();
        ANONYMOUS_INDICES.clear();
        LAMBDA_INDICES.clear();
    }


    static JavaQualifiedName ofOperation(MethodLikeNode methodLike) {
        if (methodLike.getKind() == MethodLikeKind.CONSTRUCTOR) {
            return ofOperation((ASTConstructorDeclaration) methodLike);
        } else if (methodLike.getKind() == MethodLikeKind.METHOD) {
            return ofOperation((ASTMethodDeclaration) methodLike);
        } else {
            return ofLambda((ASTLambdaExpression) methodLike);
        }
    }


    /**
     * Builds the qualified name of a method declaration.
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    private static JavaQualifiedName ofOperation(ASTMethodDeclaration node) {
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
    private static JavaQualifiedName ofOperation(ASTConstructorDeclaration node) {
        ASTAnyTypeDeclaration parent = node.getFirstParentOfType(ASTAnyTypeDeclaration.class);

        return ofOperation(parent.getQualifiedName(),
                           parent.getImage(),
                           node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /** Factorises the functionality of ofOperation() */
    private static JavaQualifiedName ofOperation(JavaQualifiedName parent, String opName, ASTFormalParameters params) {
        return JavaQualifiedName.operationName(parent, getOperationName(opName, params), false);
    }

    // @formatter:off
    /**
     * Gets the qualified name of a lambda expression. The
     * qualified name of a lambda is made up:
     * <ul>
     *     <li>Of the qualified name of the innermost enclosing
     *     type (considering anonymous classes too);</li>
     *     <li>The operation string is composed of the following
     *     segments, separated with a dollar ({@literal $}) symbol:
     *     <ul>
     *         <li>The {@code lambda} keyword;</li>
     *         <li>A keyword identifying the scope the lambda
     *         was declared in. It can be:
     *         <ul>
     *             <li>{@code new}, if the lambda is declared in an
     *             instance initializer, or a constructor, or in the
     *             initializer of an instance field of an outer or
     *             nested class</li>
     *             <li>{@code static}, if the lambda is declared in a
     *             static initializer, or in the initializer of a
     *             static field (including interface constants),</li>
     *             <li>{@code null}, if the lambda is declared inside
     *             another lambda,</li>
     *             <li>The innermost enclosing type's simple name, if the
     *             lambda is declared in the field initializer of a local
     *             class,</li>
     *             <li>The innermost enclosing method's name, if the
     *             lambda is declared inside a method,</li>
     *             <li>Nothing (empty string), if the lambda is declared
     *             in the initializer of the field of an anonymous class;</li>
     *         </ul>
     *         </li>
     *         <li>A numeric index, unique for each lambda declared
     *         within the same type declaration.</li>
     *     </ul>
     *     </li>
     * </ul>
     *
     * <p>The operation string of a lambda does not contain any formal parameters.
     *
     * <p>This specification was worked out from stack traces. The precise order in
     * which the numeric index is assigned does not conform to the way javac assigns
     * them, but it could probably be done with a visitor to keep a precise track of
     * the counter. Doing that could allow us to retrieve the Method instance associated
     * with the lambda. TODO
     *
     * <p>See <a href="https://stackoverflow.com/a/34655312/6245827">
     * this stackoverflow answer</a> for more info about how lambdas are compiled.
     *
     * @param node Lambda expression node
     * @return The qualified name of this lambda.
     */
    // @formatter:on
    private static JavaQualifiedName ofLambda(ASTLambdaExpression node) {
        JavaQualifiedName parent = findInnermostEnclosingTypeName(node);

        String operation = "lambda$" + findLambdaScopeNameSegment(node)
                + "$" + getNextIndexFromHistogram(LAMBDA_INDICES, parent, 0);

        return JavaQualifiedName.operationName(parent, operation, true);

    }


    private static String findLambdaScopeNameSegment(ASTLambdaExpression node) {
        Node parent = node.jjtGetParent();
        while (parent != null
                && !(parent instanceof ASTFieldDeclaration)
                && !(parent instanceof ASTInitializer)
                && !(parent instanceof MethodLikeNode)) {
            parent = parent.jjtGetParent();
        }

        if (parent == null) {
            throw new IllegalStateException("The enclosing scope must exist.");
        }

        if (parent instanceof ASTInitializer) {
            return ((ASTInitializer) parent).isStatic() ? "static" : "new";
        } else if (parent instanceof ASTConstructorDeclaration) {
            return "new";
        } else if (parent instanceof ASTLambdaExpression) {
            return "null";
        } else if (parent instanceof ASTFieldDeclaration) {
            ASTFieldDeclaration field = (ASTFieldDeclaration) parent;
            if (field.isStatic() || field.isInterfaceMember()) {
                return "static";
            }
            JavaQualifiedName qname = findInnermostEnclosingTypeName(field);
            if (qname.isAnonymousClass()) {
                return "";
            } else if (qname.isLocalClass()) {
                return qname.getClassList().get(qname.getClassList().size() - 1);
            } else { // other type
                return "new";
            }
        } else { // ASTMethodDeclaration
            return ((ASTMethodDeclaration) parent).getMethodName();
        }
    }


    /**
     * Gets the next available index based on a key and a histogram (map of keys to int counters).
     * If the key doesn't exist, we add a new entry with the startIndex.
     *
     * <p>Used for lambda and anonymous class counters
     *
     * @param histogram  The histogram map
     * @param key        The key to access
     * @param startIndex First index given out when the key doesn't exist
     *
     * @return The next free index
     */
    private static <T> int getNextIndexFromHistogram(Map<T, Integer> histogram, T key, int startIndex) {
        Integer count = histogram.get(key);
        if (count == null) {
            histogram.put(key, startIndex);
            return startIndex;
        } else {
            histogram.put(key, count + 1);
            return count + 1;
        }
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
                                                JavaQualifiedName.NOTLOCAL_PLACEHOLDER);
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
        return JavaQualifiedName.notOuterClassName(parent, className, localIndex);
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

        return new JavaQualifiedName(pkg == null ? ListFactory.<String>emptyList() : ListFactory.split(pkg.getPackageNameImage(), "\\."),
                                     ListFactory.make(node.getImage()),
                                     ListFactory.make(JavaQualifiedName.NOTLOCAL_PLACEHOLDER),
                                     null, false);
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
        return notOuterClassQualifiedNameHelper(parent,
                                                "" + getNextIndexFromHistogram(ANONYMOUS_INDICES, parent, 1),
                                                JavaQualifiedName.NOTLOCAL_PLACEHOLDER);
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
     * is specified by a regex pattern (see {@link #FORMAT}). Examples:
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
        Matcher matcher = FORMAT.matcher(name);

        if (!matcher.matches()) {
            return null;
        }

        ImmutableList<String> packages = StringUtils.isBlank(matcher.group(PACKAGES_GROUP_INDEX))
                ? ListFactory.<String>emptyList()
                : ListFactory.split(matcher.group(PACKAGES_GROUP_INDEX), "\\.");

        String operation = matcher.group(OPERATION_GROUP_INDEX) == null ? null : matcher.group(OPERATION_GROUP_INDEX).substring(1);
        boolean isLambda = operation != null && COMPILED_LAMBDA_PATTERN.matcher(operation).matches();

        ImmutableList<String> indexAndClasses = ListFactory.split(matcher.group(CLASSES_GROUP_INDEX), "\\$");
        ImmutableList<Integer> localIndices = ListFactory.emptyList();
        ImmutableList<String> classes = ListFactory.emptyList();

        // iterates right to left
        for (String clazz : indexAndClasses.reverse()) {
            Matcher localIndexMatcher = LOCAL_INDEX_PATTERN.matcher(clazz);
            if (localIndexMatcher.matches()) { // anonymous classes don't match, because there needs to be at least one non-digit
                localIndices = localIndices.prepend(Integer.parseInt(localIndexMatcher.group(1)));
                classes = classes.prepend(localIndexMatcher.group(2));
            } else {
                localIndices = localIndices.prepend(JavaQualifiedName.NOTLOCAL_PLACEHOLDER);
                classes = classes.prepend(clazz);
            }
        }

        return new JavaQualifiedName(packages, classes, localIndices, operation, isLambda);
    }


    /** Returns a normalized method name (not Java-canonical!). */
    private static String getOperationName(String methodName, ASTFormalParameters params) {

        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');

        boolean first = true;
        for (ASTFormalParameter param : params) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            sb.append(param.getTypeNode().getTypeImage());
            if (param.isVarargs()) {
                sb.append("...");
            }
        }

        sb.append(')');

        return sb.toString();
    }
}
