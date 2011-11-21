package net.sourceforge.pmd.util.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PmdLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return formatMessage(record);
    }

}
