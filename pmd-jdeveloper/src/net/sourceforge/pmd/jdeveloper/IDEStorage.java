package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Ide;

public class IDEStorage implements SettingsStorage {
    public void save(String key, String value)  throws SettingsException {
        Ide.setProperty(key, value);
    }

    public String load(String key) throws SettingsException  {
        return Ide.getProperty(key);
    }
}
