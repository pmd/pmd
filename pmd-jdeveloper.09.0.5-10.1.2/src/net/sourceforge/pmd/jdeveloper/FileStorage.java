package net.sourceforge.pmd.jdeveloper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class FileStorage implements SettingsStorage {

    private File file;

    public FileStorage(File file) {
        this.file = file;
    }

    public void save(Properties newProperties) throws SettingsException {
        try {
            Properties savedProperties = new Properties();

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                savedProperties.load(fis);
                fis.close();
            }

            for (Iterator i = newProperties.keySet().iterator(); i.hasNext(); 
            ) {
                String key = (String)i.next();
                String value = newProperties.getProperty(key);
                savedProperties.setProperty(key, value);
            }

            FileOutputStream fos = new FileOutputStream(file);
            savedProperties.store(fos, 
                                  "PMD-JDeveloper rule selections " + new Date());
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
