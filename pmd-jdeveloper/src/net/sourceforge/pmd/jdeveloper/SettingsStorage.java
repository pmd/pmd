package net.sourceforge.pmd.jdeveloper;

public interface SettingsStorage {
    void save(String key, String value);
    String load(String key);
}
