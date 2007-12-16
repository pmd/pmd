package net.sourceforge.pmd.jdeveloper;

import java.util.Properties;

public interface SettingsStorage {
    void save(Properties props) throws SettingsException;

    String load(String key) throws SettingsException;
}
