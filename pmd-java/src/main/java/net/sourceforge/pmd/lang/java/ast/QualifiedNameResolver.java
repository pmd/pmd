/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ImmutableList.ListFactory;


/**
 * Populates {@link JavaQualifiableNode} instances with their qualified names.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class QualifiedNameResolver extends JavaParserVisitorReducedAdapter {

    // Package names to package representation.
    // Allows reusing the same list instance for the same packages.
    // Package prefixes are also shared.
    private final Map<String, ImmutableList<String>> FOUND_PACKAGES = new HashMap<>(128);

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


    public void initializeWith(ASTCompilationUnit rootNode) {
        rootNode.jjtAccept(this, null);
    }


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
        int localIndex = JavaQualifiedName.NOTLOCAL_PLACEHOLDER;
        if (node instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) node).isLocal()) {

            localIndex = getNextIndexFromHistogram(currentLocalIndices.peek(), node.getImage(), 1);
        }

        updateClassContext(node.getImage(), localIndex);

        node.setQualifiedName(contextClassQName());

        super.visit(node, data);

        // go back to previous context
        rollbackClassContext();

        return data;
    }


    /** Pushes a new context for an inner class. */
    private void updateClassContext(String className, int localIndex) {
        localIndices = localIndices.prepend(localIndex);
        classNames = classNames.prepend(className);
        anonymousCounters.push(new MutableInt(0));
        currentLocalIndices.push(new HashMap<String, Integer>());
    }


    /** Rollback the context to the state of the enclosing class. */
    private void rollbackClassContext() {
        localIndices = localIndices.tail();
        classNames = classNames.tail();
        anonymousCounters.pop();
        currentLocalIndices.pop();
    }

    /** Creates a new class qname from the current context (fields). */
    private JavaQualifiedName contextClassQName() {
        return new JavaQualifiedName(packages, classNames, localIndices, null, false);
    }


    /** Creates a new operation qname, using the current context for the class part. */
    private JavaQualifiedName contextOperationQName(String op, boolean isLambda) {
        return new JavaQualifiedName(packages, classNames, localIndices, op, isLambda);
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
