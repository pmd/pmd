/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

/**
 * Public API for class defining AST nodes.
 */
public interface ModelicaClassSpecifierNode extends ModelicaNode {
    String getSimpleClassName();
}
