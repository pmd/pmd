package net.sourceforge.pmd.jdeveloper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;


public class FileStorage implements SettingsStorage {

    private final transient File file;

    public FileStorage(final File file) {
        this.file = file;
    }

    public void save(final Properties newProperties) throws SettingsException {
        try {
            final Properties savedProperties = new Properties();

            if (file.exists()) {
                final FileInputStream fis = new FileInputStream(file);
                savedProperties.load(fis);
                fis.close();
            }

            for (final Iterator i = newProperties.keySet().iterator(); i.hasNext(); 
            ) {
                final String key = (String)i.next();
                final String value = newProperties.getProperty(key);
                savedProperties.setProperty(key, value);
            }

            final FileOutputStream fos = new FileOutputStream(file);
            savedProperties.store(fos, 
                                  "PMD-JDeveloper rule selections " + new Date());
            fos.close();
        } catch (FileNotFoundException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, Plugin.PMD_TITLE);
        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, Plugin.PMD_TITLE);
        }
    }

    public String load(final String key) throws SettingsException {
        try {
            if (file.exists()) {
                final Properties properties = new Properties();
                final FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
                return properties.getProperty(key);
            }
        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, Plugin.PMD_TITLE);
        }
        return "false";
    }

}
