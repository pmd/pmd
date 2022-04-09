/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

/**
 * @author Brian Remedios
 * @deprecated Is internal API
 */
final class ColumnDescriptor<T> {

    public final String id;
    public final String title;
    public final Accessor<T> accessor;

    public interface Accessor<T> {

        String get(int idx, T violation, String lineSeparator);
    }

    public ColumnDescriptor(String theId, String theTitle, Accessor<T> theAccessor) {
        id = theId;
        title = theTitle;
        accessor = theAccessor;
    }
}
