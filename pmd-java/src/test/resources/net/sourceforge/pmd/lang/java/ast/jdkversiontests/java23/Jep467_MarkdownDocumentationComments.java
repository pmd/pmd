/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.Locale;

///
/// @see <a href="https://openjdk.org/jeps/467">JEP 467: Markdown Documentation Comments</a>
///
public class Jep467_MarkdownDocumentationComments {

    /// Returns the name. This is always uppercased using [String#toUpperCase(Locale)].
    ///
    /// Some links:
    /// - a module [java.base/]
    /// - a package [java.util]
    /// - a class [String]
    /// - a field [String#CASE_INSENSITIVE_ORDER]
    /// - a method [String#chars()]
    ///
    /// Some links with alternative text:
    /// - [the `java.base` module][java.base/]
    /// - [the `java.util` package][java.util]
    /// - [a class][String]
    /// - [a field][String#CASE_INSENSITIVE_ORDER]
    /// - [a method][String#chars()]
    ///
    /// @param prefix the prefix
    /// @return the name with the `prefix` and uppercased
    public String name(String prefix) {
        return (prefix + "name").toUpperCase(Locale.ROOT);
    }

    /// {@inheritDoc}
    @Override
    public String toString() {
        return super.toString();
    }
}
