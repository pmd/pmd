/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 *
 * @author Brian Remedios
 *
 * @param <T>
 * @deprecated Is internal API
 */
@InternalApi
@Deprecated
public class ColumnDescriptor<T extends Object> {

    public final String id;
    public final String title;
    public final Accessor<T> accessor;

    public interface Accessor<T extends Object> {
        String get(int idx, T violation, String lineSeparator);
    }

    public ColumnDescriptor(String theId, String theTitle, Accessor<T> theAccessor) {
        id = theId;
        title = theTitle;
        accessor = theAccessor;
    }
}
