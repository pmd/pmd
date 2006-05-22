package net.sourceforge.pmd.ui.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * Provides Functions to save the State of a View during a Session, even when the view is cloes and re-opened Saves the State in a
 * XML-File in the Plugins-Path (Standard: .metadata in the workspace)
 * 
 * @author SebastianRaffel ( 24.05.2005 )
 */
public class ViewMemento {

    private IPath path;
    private File file;
    private XMLMemento memento;

    protected final static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    protected final static String LIST_SEPARATOR = ":";
    protected final static String MEMENTO_PREFIX = "memento";
    protected final static String ATTRIBUTE_PREFIX = "attribute";
    protected final static String ATTR_NAME = "name";
    protected final static String ATTR_VALUE = "value";

    /**
     * Constructor Searches for the XML-File, where the Memento should be saved and creates it if there is none
     * 
     * @param type, a String identifying the View, used for the File's Name
     */
    public ViewMemento(String type) {
        path = PMDUiPlugin.getDefault().getStateLocation();
        file = new File(path.toString(), type);

        // we check for an existing XML-File
        // and create one, if needed
        if ((!file.exists()) || (checkForXMLFile(file) == false))
            createNewFile(file);

        // then we create a ReadRoot for the Memento
        memento = null;
        try {
            FileReader reader = new FileReader(file);
            memento = XMLMemento.createReadRoot(reader);
            reader.close();
        } catch (WorkbenchException wbe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION + this.toString(), wbe);
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + path.toString() + "/" + type + " in " + this.toString(), fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }
    }

    /**
     * Creates a new XML-Structure in a given File
     * 
     * @param file
     */
    protected void createNewFile(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(XML_PREFIX + "\n" + "<" + MEMENTO_PREFIX + "/>");
            writer.close();
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }
    }

    /**
     * Checks for an XML-Structure in a File
     * 
     * @param file
     * @return true, if the File is a XML-File we can use, false otherwise
     */
    protected boolean checkForXMLFile(File file) {
        try {
            BufferedReader contentReader = new BufferedReader(new FileReader(file));

            while (contentReader.ready()) {
                String line = contentReader.readLine();
                if (line.length() == 0)
                    continue;

                // the first Line of Text has to be the XML-Prefix
                if (line.equalsIgnoreCase(XML_PREFIX))
                    return true;
                else
                    return false;
            }
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + file.toString() + " in " + this.toString(),
                    fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }

        return false;
    }

    /**
     * Saves the Memento into the File
     * 
     * @param type
     */
    public void save(String type) {
        if (memento != null) {
            try {
                FileWriter writer = new FileWriter(file);
                memento.save(writer);
                writer.close();
            } catch (IOException ioe) {
                PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
            }
        }
    }

    /**
     * Returns a Mewmento with the given Attribute
     * 
     * @param name
     * @return a Memento
     */
    private IMemento getAttribute(String name) {
        IMemento[] mementos = memento.getChildren(ATTRIBUTE_PREFIX);
        IMemento mem = null;

        for (int k = 0; k < mementos.length; k++) {
            String attrName = mementos[k].getString(ATTR_NAME);
            if (!name.equalsIgnoreCase(attrName))
                continue;
            mem = mementos[k];
        }

        if (mem == null) {
            mem = memento.createChild(ATTRIBUTE_PREFIX);
            mem.putString(ATTR_NAME, name);
        }

        return mem;
    }

    /**
     * Puts a String into a Memento
     * 
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        IMemento mem = getAttribute(key);
        mem.putString(ATTR_VALUE, value);
    }

    /**
     * Puts an Integer into a Memento
     * 
     * @param key
     * @param value
     */
    public void putInteger(String key, int value) {
        IMemento mem = getAttribute(key);
        mem.putInteger(ATTR_VALUE, value);
    }

    /**
     * Puts a Float into a Memento
     * 
     * @param key
     * @param value
     */
    public void putFloat(String key, float value) {
        IMemento mem = getAttribute(key);
        mem.putFloat(ATTR_VALUE, value);
    }

    /**
     * puts an ArrayList into a Memento, the List is changed into a seperated String
     * 
     * @param key
     * @param valueList
     */
    public void putArrayList(String key, ArrayList valueList) {
        String valueString = "";
        for (int k = 0; k < valueList.size(); k++) {
            if (k > 0)
                valueString += LIST_SEPARATOR;
            valueString += valueList.get(k).toString();
        }
        putString(key, valueString);
    }

    /**
     * Gets a String from a Memento
     * 
     * @param key
     * @return a String with the Value
     */
    public String getString(String key) {
        IMemento mem = getAttribute(key);
        return mem.getString(ATTR_VALUE);
    }

    /**
     * Gets an Integer From a Memento
     * 
     * @param key
     * @return an Integer with the Value
     */
    public Integer getInteger(String key) {
        IMemento mem = getAttribute(key);
        return mem.getInteger(ATTR_VALUE);
    }

    /**
     * Returns a Float from a Memento
     * 
     * @param key
     * @return a Float with the Value
     */
    public Float getFloat(String key) {
        IMemento mem = getAttribute(key);
        return mem.getFloat(ATTR_VALUE);
    }

    /**
     * Returns an ArrayList of Integers from a Memento
     * 
     * @param key
     * @return ArrayList of Integer-Values
     */
    public ArrayList getIntegerList(String key) {
        String valueString = getString(key);
        if (valueString == null)
            return null;

        ArrayList valuelist = new ArrayList();
        String[] objects = valueString.split(LIST_SEPARATOR);
        for (int k = 0; k < objects.length; k++) {
            valuelist.add(new Integer(objects[k]));
        }
        return valuelist;
    }

    /**
     * Returns an ArrayList of Strings from a Memento
     * 
     * @param key
     * @return a ArrayList of String values
     */
    public ArrayList getStringList(String key) {
        String valueString = getString(key);
        if (valueString == null)
            return null;

        ArrayList valuelist = new ArrayList(Arrays.asList(valueString.split(LIST_SEPARATOR)));
        return valuelist;
    }
}
