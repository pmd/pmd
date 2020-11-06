/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public class PmdLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return formatMessage(record);
    }

}
