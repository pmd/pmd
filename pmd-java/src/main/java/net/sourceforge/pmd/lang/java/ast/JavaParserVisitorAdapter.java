/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An adapter for {@link JavaParserVisitor}. Since 7.0.0 we use default methods
 * on the interface, which removes code duplication. However, if a visitor directly
 * implements the interface, then the syntax {@code super.visit(...)} is illegal and
 * doesn't refer to the default method. Instead, one would have to qualify the super,
 * like {@code JavaParserVisitor.super.visit}.
 *
 * <p>This restriction doesn't apply when the interface is not a direct super interface,
 * i.e. when there's an intermediary class like this one in the type hierarchy, or
 * another interface. That's extending this class is preferred to implementing the visitor
 * directly.
 */
public class JavaParserVisitorAdapter implements JavaParserVisitor {

}
