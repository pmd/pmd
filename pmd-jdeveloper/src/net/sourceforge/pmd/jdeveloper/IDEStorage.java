package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Ide;

import java.util.Properties;
import java.util.Iterator;

public class IDEStorage implements SettingsStorage {

    public void save(Properties props)  throws SettingsException {
        for (Iterator i = props.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            String value = props.getProperty(key);
            Ide.setProperty(key, value);
        }
    }

    public String load(String key) throws SettingsException  {
        return Ide.getProperty(key);
    }
}
