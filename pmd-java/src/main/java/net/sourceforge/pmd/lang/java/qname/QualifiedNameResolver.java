/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import static net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName.NOTLOCAL_PLACEHOLDER;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiableNode;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.qname.ImmutableList.ListFactory;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Populates {@link JavaQualifiableNode} instances with their qualified names.
 *
 * @author Clément Fournier
 * @since 6.1.0
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public class QualifiedNameResolver extends JavaParserVisitorReducedAdapter {

    // Package names to package representation.
    // Allows reusing the same list instance for the same packages.
    // Package prefixes are also shared.
    private static final Map<String, ImmutableList<String>> FOUND_PACKAGES = new ConcurrentHashMap<>(128);

    // The following stacks stack some counter of the
    // visited classes. A new entry is pushed when
    // we enter a new class, and popped when we get
    // out

    /**
     * The top of the stack is the map of local class names
     * to the count of local classes with that name declared
     * in the current class.
     */
    private final Stack<Map<String, Integer>> currentLocalIndices = new Stack<>();

    /**
     * The top of the stack is the current count of
     * anonymous classes of the currently visited class.
     */
    private final Stack<MutableInt> anonymousCounters = new Stack<>();

    private final Stack<MutableInt> lambdaCounters = new Stack<>();

    private final Stack<JavaTypeQualifiedName> innermostEnclosingTypeName = new Stack<>();

    /**
     * Package list of the current file.
     * The qualified names of classes and methods declared
     * in this compilation unit share this list, because they're
     * declared in the same package.
     *
     * The head of this list is the name of the
     * innermost package.
     */
    private ImmutableList<String> packages;

    /**
     * Local indices of the currently visited class.
     *
     * The head of this list is the local index of the
     * innermost class.
     */
    private ImmutableList<Integer> localIndices;

    /**
     * Class names of the currently visited class.
     * For outer classes, this list has one name.
     * For a nested class, names are prepended to
     * this list from outermost to innermost.
     *
     * The head of this list is the name of the
     * innermost class.
     */
    private ImmutableList<String> classNames;

    /**
     * The classloader that must be used to load classes for resolving types,
     * e.g. for qualified names.
     * This is the auxclasspath.
     */
    private ClassLoader classLoader;

    /**
     * Initialises the visitor and starts it.
     *
     * @param classLoader The classloader that will be used by type qualified names
     *                    to load their type.
     * @param rootNode    The root hierarchy
     */
    public void initializeWith(ClassLoader classLoader, ASTCompilationUnit rootNode) {
        this.classLoader = PMDASMClassLoader.getInstance(classLoader);
        rootNode.jjtAccept(this, null);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {

        // update the package list
        packages = getPackageList(node.getFirstChildOfType(ASTPackageDeclaration.class));

        // reset other variables
        localIndices = ListFactory.emptyList();
        classNames = ListFactory.emptyList();
        anonymousCounters.clear();
        currentLocalIndices.clear();

        return super.visit(node, data);
    }


    /**
     * Returns the immutable list representation of this package name.
     * The list's tail is shared with all packages that start with
     * the same prefix.
     *
     * @param pack The package declaration, may be null
     */
    private ImmutableList<String> getPackageList(ASTPackageDeclaration pack) {
        if (pack == null) {
            return ListFactory.emptyList();
        }

        final String image = pack.getPackageNameImage();
        ImmutableList<String> fullExisting = FOUND_PACKAGES.get(image);

        if (fullExisting != null) {
            return fullExisting;
        }

        // else we'll have to look for the longest prefix currently known
        // and complete it with remaining packages

        final String[] allPacks = image.split("\\.");
        ImmutableList<String> longestPrefix = getLongestPackagePrefix(image, allPacks.length);
        StringBuilder prefixImage = new StringBuilder();
        for (String p : longestPrefix) {
            prefixImage.append(p);
        }

        for (int i = longestPrefix.size(); i < allPacks.length; i++) {
            longestPrefix = longestPrefix.prepend(allPacks[i]);
            prefixImage.append(allPacks[i]);
            FOUND_PACKAGES.put(prefixImage.toString(), longestPrefix);
        }

        return longestPrefix;
    }


    /**
     * Returns the longest list of package names contained in the cache
     * that is a prefix of the given string. This method proceeds recursively,
     * trimming one package name at each iteration and checking if the remaining
     * prefix is already cached.
     *
     * @param acc Accumulator, initially the full package name
     * @param i   Index indicating the remaining number of packages, initially
     *            the total number of packages in the package name
     */
    private ImmutableList<String> getLongestPackagePrefix(String acc, int i) {
        ImmutableList<String> prefix = FOUND_PACKAGES.get(acc);
        if (prefix != null) {
            return prefix;
        }

        if (i == 1) { // no prefix known, return early because there's no more '.' in acc
            return ListFactory.emptyList();
        }

        return getLongestPackagePrefix(acc.substring(0, acc.lastIndexOf('.')), i - 1);
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        int localIndex = NOTLOCAL_PLACEHOLDER;
        if (node instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) node).isLocal()) {

            localIndex = getNextIndexFromHistogram(currentLocalIndices.peek(), node.getSimpleName(), 1);
        }

        updateClassContext(node.getSimpleName(), localIndex);

        ((AbstractAnyTypeDeclaration) node).setQualifiedName(contextClassQName());

        super.visit(node, data);

        // go back to previous context
        rollbackClassContext();

        return data;
    }


    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (!node.isAnonymousClass()) {
            return super.visit(node, data);
        }

        updateContextForAnonymousClass();
        node.setQualifiedName(contextClassQName());

        super.visit(node, data);
        rollbackClassContext();

        return data;
    }


    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        if (!node.isAnonymousClass()) {
            return super.visit(node, data);
        }

        updateContextForAnonymousClass();
        node.setQualifiedName(contextClassQName());

        super.visit(node, data);
        rollbackClassContext();

        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String opname = getOperationName(node.getName(), node.getFirstDescendantOfType(ASTFormalParameters.class));
        node.setQualifiedName(contextOperationQName(opname, false));
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        String opname = getOperationName(classNames.head(), node.getFirstDescendantOfType(ASTFormalParameters.class));
        node.setQualifiedName(contextOperationQName(opname, false));
        return super.visit(node, data);
    }

    // @formatter:off
    /**
     * Populates the qualified name of a lambda expression. The
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
     * them. Doing that could allow us to retrieve the Method instance associated
     * with the lambda. TODO
     *
     * <p>See <a href="https://stackoverflow.com/a/34655312/6245827">
     * this stackoverflow answer</a> for more info about how lambdas are compiled.
     *
     * @param node Lambda expression node
     */
    // @formatter:on
    @Override
    public Object visit(ASTLambdaExpression node, Object data) {

        String opname = "lambda$" + findLambdaScopeNameSegment(node)
                + "$" + lambdaCounters.peek().getAndIncrement();

        node.setQualifiedName(contextOperationQName(opname, true));
        return super.visit(node, data);
    }


    private void updateContextForAnonymousClass() {
        updateClassContext("" + anonymousCounters.peek().incrementAndGet(), NOTLOCAL_PLACEHOLDER);
    }


    /** Pushes a new context for an inner class. */
    private void updateClassContext(String className, int localIndex) {
        localIndices = localIndices.prepend(localIndex);
        classNames = classNames.prepend(className);
        anonymousCounters.push(new MutableInt(0));
        lambdaCounters.push(new MutableInt(0));
        currentLocalIndices.push(new HashMap<String, Integer>());
        innermostEnclosingTypeName.push(contextClassQName());
    }


    /** Rollback the context to the state of the enclosing class. */
    private void rollbackClassContext() {
        localIndices = localIndices.tail();
        classNames = classNames.tail();
        anonymousCounters.pop();
        lambdaCounters.pop();
        currentLocalIndices.pop();
        innermostEnclosingTypeName.pop();
    }

    /** Creates a new class qname from the current context (fields). */
    private JavaTypeQualifiedName contextClassQName() {
        return new JavaTypeQualifiedName(packages, classNames, localIndices, classLoader);
    }


    /** Creates a new operation qname, using the current context for the class part. */
    private JavaOperationQualifiedName contextOperationQName(String op, boolean isLambda) {
        return new JavaOperationQualifiedName(innermostEnclosingTypeName.peek(), op, isLambda);
    }


    private String findLambdaScopeNameSegment(ASTLambdaExpression node) {
        Node parent = node.getParent();
        while (parent != null
                && !(parent instanceof ASTFieldDeclaration)
                && !(parent instanceof ASTEnumConstant)
                && !(parent instanceof ASTInitializer)
                && !(parent instanceof MethodLikeNode)) {
            parent = parent.getParent();
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
        } else if (parent instanceof ASTEnumConstant) {
            return "static";
        } else if (parent instanceof ASTFieldDeclaration) {
            ASTFieldDeclaration field = (ASTFieldDeclaration) parent;
            if (field.isStatic() || field.isInterfaceMember()) {
                return "static";
            }
            if (innermostEnclosingTypeName.peek().isAnonymousClass()) {
                return "";
            } else if (innermostEnclosingTypeName.peek().isLocalClass()) {
                return classNames.head();
            } else { // other type
                return "new";
            }
        } else { // ASTMethodDeclaration
            return ((ASTMethodDeclaration) parent).getName();
        }
    }


    /** Returns a normalized method name (not Java-canonical!). */
    private static String getOperationName(String methodName, ASTFormalParameters params) {
        return PrettyPrintingUtil.displaySignature(methodName, params);
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

}
