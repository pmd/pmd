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
 *     you should not couple your code to them.
 * </ul>
 *
 */
package net.sourceforge.pmd.lang.java.ast;
