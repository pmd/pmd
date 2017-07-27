/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Basic interface for qualified names
 *
 * @author Clément Fournier
 */
public interface QualifiedName {

    @Override
    String toString();


    String getOperation();


    String[] getClasses();

}
