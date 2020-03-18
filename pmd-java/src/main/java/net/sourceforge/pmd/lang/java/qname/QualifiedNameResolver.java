/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.qname.ImmutableList.ListFactory;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ast.AstSymFactory;


/**
 *
 * <p>In fact, populates symbols on declaration nodes.
 * TODO in the near future we'll get rid of qualified names, and can
 * reuse this class just to build symbols (moving it to symbols.impl.ast).
 *
 * @author Clément Fournier
 * @since 6.1.0
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public class QualifiedNameResolver extends JavaParserVisitorAdapter {

    /** Local index value for when the class is not local. */
    static final int NOTLOCAL_PLACEHOLDER = -1;

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

    private final Stack<String> innermostEnclosingTypeName = new Stack<>();

    private final Deque<JTypeParameterOwnerSymbol> enclosingSymbols = new ArrayDeque<>();
    private final AstSymFactory symFactory;

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

    public QualifiedNameResolver(AstSymFactory symFactory, ClassLoader classLoader) {
        this.symFactory = symFactory;
        this.classLoader = classLoader;
    }

    /**
     * Traverse the compilation unit.
     */
    public void traverse(ASTCompilationUnit root) {
        root.jjtAccept(this, null);
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
    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (isTrueLocalVar(node)) {
            symFactory.setLocalVarSymbol(node);
        } else {
            // in the other cases, building the method/ctor/class symbols already set the symbols
            assert node.getSymbol() != null : "Symbol was null for " + node;
        }

        return super.visit(node, data);
    }

    private boolean isTrueLocalVar(ASTVariableDeclaratorId node) {
        return !(node.isField() || node.isEnumConstant() || node.getParent() instanceof ASTFormalParameter);
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        int localIndex = NOTLOCAL_PLACEHOLDER;
        if (node.isLocal()) {
            localIndex = getNextIndexFromHistogram(currentLocalIndices.peek(), node.getSimpleName(), 1);
        }

        updateClassContext(node.getSimpleName(), localIndex);

        return recurseOnClass(node);
    }

    @Override
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        updateContextForAnonymousClass();
        return recurseOnClass(node);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return recurseOnExecutable(node);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return recurseOnExecutable(node);
    }


    public Object recurseOnExecutable(ASTMethodOrConstructorDeclaration node) {
        JExecutableSymbol sym = node.getSymbol();
        enclosingSymbols.addLast(sym);

        super.visit(node, null);

        enclosingSymbols.removeLast();
        return null;
    }

    public Object recurseOnClass(ASTAnyTypeDeclaration node) {
        InternalApiBridge.setQname(node, contextClassQName());

        JClassSymbol sym = symFactory.setClassSymbol(enclosingSymbols.peekLast(), node);
        enclosingSymbols.addLast(sym);

        super.visit(node, null);

        rollbackClassContext();
        enclosingSymbols.removeLast();
        return null;
    }


    private void updateContextForAnonymousClass() {
        updateClassContext("" + anonymousCounters.peek().incrementAndGet(), NOTLOCAL_PLACEHOLDER);
    }


    /** Pushes a new context for an inner class. */
    private void updateClassContext(String className, int localIndex) {
        localIndices = localIndices.prepend(localIndex);
        classNames = classNames.prepend(className);
        anonymousCounters.push(new MutableInt(0));
        currentLocalIndices.push(new HashMap<String, Integer>());
        innermostEnclosingTypeName.push(contextClassQName());
    }


    /** Rollback the context to the state of the enclosing class. */
    private void rollbackClassContext() {
        localIndices = localIndices.tail();
        classNames = classNames.tail();
        anonymousCounters.pop();
        currentLocalIndices.pop();
        innermostEnclosingTypeName.pop();
    }

    /** Creates a new class qname from the current context (fields). */
    private String contextClassQName() {
        StringBuilder sb = new StringBuilder();

        for (String aPackage : packages.reverse()) {
            sb.append(aPackage).append('.');
        }

        // this in the normal order
        ImmutableList<String> reversed = classNames.reverse();
        sb.append(reversed.head());
        for (Entry<String, Integer> classAndLocalIdx : reversed.tail().zip(localIndices.reverse().tail())) {
            sb.append('$');

            if (classAndLocalIdx.getValue() != NOTLOCAL_PLACEHOLDER) {
                sb.append(classAndLocalIdx.getValue());
            }

            sb.append(classAndLocalIdx.getKey());
        }

        return sb.toString();
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
