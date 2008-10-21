package net.sourceforge.pmd.jdeveloper;

import java.util.Iterator;
import java.util.Properties;

import oracle.ide.Ide;


public class IDEStorage implements SettingsStorage {

    public void save(final Properties props) throws SettingsException {
        for (final Iterator i = props.keySet().iterator(); i.hasNext(); ) {
            final String key = (String)i.next();
            final String value = props.getProperty(key);
            Ide.setProperty(key, value);
        }
    }

    public String load(final String key) throws SettingsException {
        return Ide.getProperty(key);
    }
}
