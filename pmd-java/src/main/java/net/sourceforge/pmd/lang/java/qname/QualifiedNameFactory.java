/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.qname.ImmutableList.ListFactory;


/**
 * Static factory methods for JavaQualifiedName.
 * These are intended only for tests, even though some deprecated
 * APIs use it. May be moved to an internal package?
 *
 * @author Clément Fournier
 * @since 6.1.0
 *
 * @deprecated Was internal API, will be removed. See {@link JavaQualifiedName}.
 */
@Deprecated
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

    private static final Pattern LOCAL_INDEX_PATTERN = Pattern.compile("(\\d+)(\\D\\w+)");


    private QualifiedNameFactory() {

    }


    /**
     * Gets the qualified name of a class.
     *
     * @param clazz Class object
     *
     * @return The qualified name of the class, or null if the class is null
     */
    public static JavaTypeQualifiedName ofClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        String name = clazz.getName();
        if (name.indexOf('.') < 0) {
            name = '.' + name; // unnamed package, marked by a full stop. See ofString's format below
        }

        // Note: this assumes, that clazz has been loaded through the correct classloader,
        // specifically through the auxclasspath classloader.
        // But this method should only be used in tests anyway
        return (JavaTypeQualifiedName) ofStringWithClassLoader(name, clazz.getClassLoader());
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
        return ofStringWithClassLoader(name, null);
    }

    private static JavaQualifiedName ofStringWithClassLoader(String name, ClassLoader classLoader) {
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
                localIndices = localIndices.prepend(JavaTypeQualifiedName.NOTLOCAL_PLACEHOLDER);
                classes = classes.prepend(clazz);
            }
        }

        JavaTypeQualifiedName parent = new JavaTypeQualifiedName(packages, classes, localIndices, classLoader);
        return operation == null ? parent : new JavaOperationQualifiedName(parent, operation, isLambda);
    }
}
