/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Contains the classes and interfaces modelling the Java AST.
 *
 * <p>Note: from 6.16.0 on, the following usages have been deprecated:
 * <ul>
 *     <li>Manual instantiation of nodes. Constructors of node classes are
 *     deprecated and marked {@link net.sourceforge.pmd.annotation.InternalApi}.
 *     Nodes should only be obtained from the parser, which for rules,
 *     means that never need to instantiate node themselves. Those
 *     constructors will be made package private with 7.0.0.
 *     <li>Subclassing of base node classes, or usage of their type.
 *     Version 7.0.0 will bring a new set of abstractions that will
 *     be public API, but the base classes are and will stay internal.
 *     You should not couple your code to them.
 *     <p>In the meantime you should use interfaces like {@link net.sourceforge.pmd.lang.java.ast.JavaNode}
 *     or {@link net.sourceforge.pmd.lang.ast.Node}, or the other published
 *     interfaces in this package, to refer to nodes generically.
 *     </li>
 *     <li>Setters found in any node class or interface. Rules should consider
 *     the AST immutable. We will make those setters package private
 *     with 7.0.0.
 *     </li>
 * </ul>
 *
 */
package net.sourceforge.pmd.lang.java.ast;
