/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 */
public interface QualifiableNode {

    char METHOD_DELIMITER = '$';
    char NESTED_CLASS_DELIMITER = '.';

    String getQualifiedName();

}
