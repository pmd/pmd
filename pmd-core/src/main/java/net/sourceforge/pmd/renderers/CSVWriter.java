/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * A generic writer that formats input items into rows and columns per the provided column descriptors.
 *
 * @author Brian Remedios
 * @param <T>
 */
public class CSVWriter<T extends Object>  {

	private final String					separator;			// e.g., the comma
	private final String					lineSeparator;		// cr
	private final List<ColumnDescriptor<T>> columns;

    public CSVWriter(List<ColumnDescriptor<T>> theColumns, String theSeparator, String theLineSeparator) {
    	columns = theColumns;
    	separator = theSeparator;
    	lineSeparator = theLineSeparator;
    }

    public void writeTitles(Writer writer) throws IOException {
    	StringBuilder buf = new StringBuilder(300);
    	for (int i=0; i<columns.size()-1; i++) {
    		quoteAndCommify(buf, columns.get(i).title);
    	}

    	quote(buf, columns.get(columns.size()-1).title);

		buf.append(lineSeparator);
		writer.write(buf.toString());
    }

    public void writeData(Writer writer, Iterator<T> items) throws IOException {

        int count = 1;

    	StringBuilder buf = new StringBuilder(300);

		T rv;
		final int lastColumnIdx = columns.size()-1;

		while (items.hasNext()) {
		    buf.setLength(0);
		    rv = items.next();

		    for (int i=0; i<lastColumnIdx; i++) {
		    	quoteAndCommify(buf, columns.get(i).accessor.get(count, rv, separator));
		    }

		    quote(buf, columns.get(lastColumnIdx).accessor.get(count, rv, separator));

		    buf.append(lineSeparator);
		    writer.write(buf.toString());
		    count++;
		}
    }

    private void quote(StringBuilder buffer, String s) {
    	if (s == null) {
    	    return;
    	}
    	buffer.append('"').append(s).append('"');
    }

    private void quoteAndCommify(StringBuilder buffer, String s) {
		quote(buffer, s);
		buffer.append(separator);
    }
}
