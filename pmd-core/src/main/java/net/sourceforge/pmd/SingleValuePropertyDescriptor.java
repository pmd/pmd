/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * @author Clément Fournier
 */
public interface SingleValuePropertyDescriptor<T> extends PropertyDescriptor<T> {

    @Override
    Class<T> type();
}
