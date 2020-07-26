/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.text.MessageFormat;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

/**
 * TODO pull that up to PMD core, use for {@link LanguageLevelChecker}
 * as well
 */
public interface SemanticChecksLogger {

    // TODO how strict do we need to be here?
    //   many rules don't absolutely need correctness to work
    //   maybe we need to identify separate "levels" of the tree
    //   eg level 0: lexable (CPD)
    //      level 1: parsable (many syntax-only rules, eg UnnecessaryParentheses)
    //      level 2: type-resolved (more complicated rules)

    /**
     * Warning, classpath is misconfigured (or not configured).
     */
    String CANNOT_RESOLVE_SYMBOL = "Cannot resolve symbol {0}";


    /**
     * We had resolved a prefix, and a suffix is not resolved. This may
     * mean that the classpath is out-of-date, or the code is incorrect.
     * Eg {@code System.oute}: {@code System} is resolved, {@code oute}
     * is not a member of that type.
     *
     * <p>This differs from {@link #CANNOT_RESOLVE_SYMBOL} in that for
     * the latter, it's more probable that the classpath is incorrect.
     * It is emitted eg when we see an import, so a fully qualified
     * name, and yet we can't resolve it.
     * TODO whether it's incorrect code or incorrect classpath is only
     *  a guess, probably we need an option to differentiate the two
     *  (eg in an IDE plugin, it may well be incorrect code, but in a
     *  CLI run, it's more likely to be incorrect classpath).
     */
    String CANNOT_RESOLVE_MEMBER = "Cannot resolve ''{0}'' in {1}, treating it as {2}"; // javac gives a simple "cannot resolve symbol {0}"

    /**
     * TODO Should be an error.
     */
    String MALFORMED_GENERIC_TYPE = "Maformed generic type: expected {0} type arguments, got {1}";

    // this is an error
    String EXPECTED_ANNOTATION_TYPE = "Expected an annotation type";

    /**
     * An ambiguous name is completely ambiguous. We don't have info
     * about it at all, classpath is incomplete or code is incorrect.
     * Eg {@code package.that.doesnt.exist.Type}
     */
    String CANNOT_RESOLVE_AMBIGUOUS_NAME = "Cannot resolve ambiguous name {0}, treating it as a {1}";

    String AMBIGUOUS_NAME_REFERENCE = "Reference ''{0}'' is ambiguous, both {1} and {2} match";

    /*
        TODO Checks that are essential for typeres/symtable
         - no self-reference in initializer
           - else infinite loop
         - class or type param doesn't extend/implement itself
           - else infinite loop
         - types are well-formed, ie if they are parameterized, then the number of type arguments is the number of formal type params
           - note that this will be tricky when dealing with unresolved types:
           for now we give them zero type params, but we must infer that from
           looking through the file.
           - else failure when doing type substitution
         - method reference is well formed, ie a constructor reference
           has a type as LHS (currently the parser throws, it would need
           to be more lenient)
     */


    /**
     * Log a warning at the given location.
     *
     * @param location Node owning the warning
     * @param message  Message, possibly formatted (see {@link MessageFormat})
     * @param args     Arguments for the template
     */
    void warning(JavaNode location, String message, Object... args);


    void error(JavaNode location, String message, Object... args);
}
