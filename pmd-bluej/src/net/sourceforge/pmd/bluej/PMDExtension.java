package net.sourceforge.pmd.bluej;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;

import java.net.URL;

public class PMDExtension extends Extension {

    public void startup (BlueJ bluej) {

        bluej.setMenuGenerator(new MenuBuilder(bluej.getCurrentFrame()));
/*
        Preferences myPreferences = new Preferences(bluej);
        bluej.setPreferenceGenerator(myPreferences);
*/
    }

    public boolean isCompatible () {
        return true; 
    }

    public String  getVersion () {
        return ("1.0");
    }

    public String  getName () {
        return ("PMD");
    }

    public String getDescription () {
        return "PMD extension - finds unused code, empty blocks, and more!";
    }

    public URL getURL () {
        try {
            return new URL("http://pmd.sf.net/");
        } catch ( Exception e ) {
            System.out.println ("Can't get PMD extension URL: "+e.getMessage());
            return null;
        }
    }
}


