package net.sourceforge.pmd.jdeveloper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Date;

public class FileStorage implements SettingsStorage {

    private File file;

    public FileStorage(File file) {
        this.file = file;
    }

    public void save(String key, String value) throws SettingsException  {
        try {
            Properties properties = new Properties();

            if (file.exists()) {
                // load old properties
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            }

            // set new property
            properties.setProperty(key, value);

            // save all
            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, "PMD-JDeveloper rule selections " + new Date());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SettingsException(e.getMessage());
        }
    }

    public String load(String key) throws SettingsException {
        try {
            if (file.exists()) {
                Properties properties = new Properties();
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
                return properties.getProperty(key);
            }
            return "false";
        } catch (Exception e) {
            e.printStackTrace();
            throw new SettingsException(e.getMessage());
        }
    }

}
