package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Ide;

public class IDEStorage implements SettingsStorage {
    public void save(String key, String value) {
        Ide.setProperty(key, value);
    }

    public String load(String key) {
        return Ide.getProperty(key);
    }
}
