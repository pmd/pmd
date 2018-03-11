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

import net.sourceforge.pmd.lang.ast.QualifiedName;


/**
 * Represents Qualified Names for use within the java metrics framework.
 */
public final class JavaQualifiedName implements QualifiedName {

    /**
     * Pattern specifying the format.
     *
     * <pre>
     *    ((\w+\.)+|\.)             # packages
     *    (                         # classes
     *       (\w+)                  # primary class
     *       (
     *         \$                   # separator
     *         (\d+)?               # optional local class index
     *         \w+
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
    private static final Pattern FORMAT = Pattern.compile("((\\w+\\.)+|\\.)              # packages\n"
                                                                  + "(                         # classes\n"
                                                                  + "  (\\w+)                  # primary class\n"
                                                                  + "  ("
                                                                  + "    \\$                   # separator\n"
                                                                  + "    (\\d+)?               # optional local class index\n"
                                                                  + "    \\w+"
                                                                  + "  )*"
                                                                  + ")"
                                                                  + "(                         # optional operation suffix\n"
                                                                  + "  \\#"
                                                                  + "  (\\w+)                  # method name\n"
                                                                  + "  \\("
                                                                  + "    (                     # parameters\n"
                                                                  + "      (\\w+)"
                                                                  + "      (,\\040\\w+)*        # \040 is a space\n"
                                                                  + "    )?"
                                                                  + "  \\)"
                                                                  + ")?", Pattern.COMMENTS);
    // indices of interesting groups in the regex
    private static final int PACKAGES_GROUP_INDEX = 1;
    private static final int CLASSES_GROUP_INDEX = 3;
    private static final int OPERATION_GROUP_INDEX = 7;


    /** Local index value for when the class is not local. */
    private static final int NOTLOCAL_PLACEHOLDER = -1;

    // maps class names to the names of their local classes, to the count of local classes with the same name
    private static final Map<JavaQualifiedName, Map<String, Integer>> LOCAL_INDICES = new WeakHashMap<>();
    private static final Pattern LOCAL_INDEX_PATTERN = Pattern.compile("(\\d+)(\\w+)");

    private String[] packages = null; // unnamed package
    private String[] classes = new String[1];
    private String operation = null;


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


    /* default, test only */ static void resetLocalIndicesCounter() {
        LOCAL_INDICES.clear();
    }


    /**
     * Builds the qualified name of a method declaration.
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    /* default */ static JavaQualifiedName ofOperation(ASTMethodDeclaration node) {
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
    /* default */ static JavaQualifiedName ofOperation(ASTConstructorDeclaration node) {
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
     * Builds the qualified name of a nested class using the qualified name of its immediate parent.
     *
     * @param parent    The qname of the immediate parent
     * @param className The name of the class
     *
     * @return The qualified name of the nested class
     */
    /* default */ static JavaQualifiedName ofNestedClass(JavaQualifiedName parent, String className) {
        return nestedOrLocalClassQualifiedNameHelper(parent, className, NOTLOCAL_PLACEHOLDER);
    }


    /**
     * Builds the qualified name of a local class using the qualified name of its immediate parent.
     * Local classes use a random suffix to prevent qualified name collisions.
     *
     * @param parent    The qname of the immediate parent
     * @param className The name of the class
     *
     * @return The qualified name of the local class
     */
    /* default */ static JavaQualifiedName ofLocalClass(JavaQualifiedName parent, String className) {
        return nestedOrLocalClassQualifiedNameHelper(parent, className, addLocal(parent, className));
    }


    // works from the parent class qualified name to create a nested or local class name
    // use NOTLOCAL_PLACEHOLDER if the class is not local
    private static JavaQualifiedName nestedOrLocalClassQualifiedNameHelper(JavaQualifiedName parent, String className, int localIndex) {
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
    /* default */ static JavaQualifiedName ofOuterClass(ASTAnyTypeDeclaration node) {
        ASTPackageDeclaration pkg = node.getFirstParentOfType(ASTCompilationUnit.class)
                                        .getFirstChildOfType(ASTPackageDeclaration.class);

        JavaQualifiedName qname = new JavaQualifiedName();
        qname.packages = pkg == null ? null : pkg.getPackageNameImage().split("\\.");
        qname.classes[0] = node.getImage();

        return qname;
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
     * <p>{@code .MyClass$Nested}
     * <ul>
     * <li> A class in the unnamed package is preceded by a single full stop.
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

        qname.packages = ".".equals(matcher.group(PACKAGES_GROUP_INDEX)) ? null : matcher.group(PACKAGES_GROUP_INDEX).split("\\.");
        qname.operation = matcher.group(OPERATION_GROUP_INDEX) == null ? null : matcher.group(OPERATION_GROUP_INDEX).substring(1);

        qname.classes = matcher.group(CLASSES_GROUP_INDEX).split("\\$");
        qname.localIndices = new int[qname.classes.length];

        for (int i = 0; i < qname.classes.length; i++) {
            Matcher localIndexMatcher = LOCAL_INDEX_PATTERN.matcher(qname.classes[i]);
            if (localIndexMatcher.matches()) {
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
     * Returns the packages. This is specific to Java's package structure.
     *
     * @return The packages.
     */
    public String[] getPackages() {
        return packages == null ? null : Arrays.copyOf(packages, packages.length);
    }


    /**
     * Returns the class specific part of the name. It identifies a class in the namespace it's declared in. If the
     * class is nested inside another, then the array returned contains all enclosing classes in order.
     *
     * @return The class names array.
     */
    public String[] getClasses() {
        return Arrays.copyOf(classes, classes.length);
    }


    /**
     * Returns the operation specific part of the name. It identifies an operation in its namespace.
     *
     * @return The operation string.
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (packages != null) {
            int last = packages.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(packages[i]).append('.');
            }

            sb.append(packages[last]);
        }
        sb.append('.'); // this dot is there even if package is null

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

        return sb.toString();
    }
}
