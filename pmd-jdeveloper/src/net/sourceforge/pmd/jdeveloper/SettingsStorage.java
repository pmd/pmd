package net.sourceforge.pmd.jdeveloper;

public interface SettingsStorage {
    void save(String key, String value) throws SettingsException;
    String load(String key) throws SettingsException ;
}
