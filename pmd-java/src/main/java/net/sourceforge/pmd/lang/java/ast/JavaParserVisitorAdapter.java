/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * An adapter for {@link JavaParserVisitor}. Unless visit methods are overridden without
 * calling {@code super.visit}, the visitor performs a full depth-first tree walk.
 *
 * <p>Since 7.0.0 we use default methods
 * on the interface, which removes code duplication. However, if a visitor directly
 * implements the interface, then the syntax {@code super.visit(...)} is illegal and
 * doesn't refer to the default method. Instead, one would have to qualify the super,
 * like {@code JavaParserVisitor.super.visit}.
 *
 * <p>This restriction doesn't apply when the interface is not a direct super interface,
 * i.e. when there's an intermediary class like this one in the type hierarchy, or
 * e.g. {@link AbstractJavaRule}. That's why extending this class is preferred to
 * implementing the visitor directly.
 */
public class JavaParserVisitorAdapter implements JavaParserVisitor {

}
