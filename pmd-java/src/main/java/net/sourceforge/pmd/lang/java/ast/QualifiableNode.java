/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 */
public interface QualifiableNode {

    char CLASS_DELIMITER = '$';
    char METHOD_DELIMITER = '#';
    char NESTED_CLASS_DELIMITER = ':';
    char LEFT_PARAM_DELIMITER = '(';
    char RIGHT_PARAM_DELIMITER = ')';
    char PARAMLIST_DELIMITER = ',';

    String getQualifiedName();

}
