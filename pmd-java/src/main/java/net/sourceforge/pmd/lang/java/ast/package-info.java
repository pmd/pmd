/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Contains the classes and interfaces modelling the Java AST.
 *
 * <p>Note: from 6.16.0 on, the following usages have been deprecated:
 * <ul>
 *     <p>Manual instantiation of nodes. Constructors of node classes are
 *     deprecated and marked {@link net.sourceforge.pmd.annotation.InternalApi}.
 *     Nodes should only be obtained from the parser, which for rules,
 *     means that never need to instantiate node themselves. Those
 *     constructors will be made package private with 7.0.0.
 * </ul>
 *
 */
package net.sourceforge.pmd.lang.java.ast;
