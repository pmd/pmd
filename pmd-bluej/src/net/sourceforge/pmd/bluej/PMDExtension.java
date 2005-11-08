package net.sourceforge.pmd.bluej;

import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.ExtensionException;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

import java.net.URL;

public class PMDExtension extends Extension implements PackageListener {

    public void startup (BlueJ bluej) {
/*
        // Register a generator for menu items
        bluej.setMenuGenerator(new MenuBuilder());

        // Register a "preferences" panel generator
        Preferences myPreferences = new Preferences(bluej);
        bluej.setPreferenceGenerator(myPreferences);
*/

        // Listen for BlueJ events at the "package" level
        bluej.addPackageListener(this);
    }

    public void packageOpened ( PackageEvent ev ) {
        try {
            System.out.println ("Project " + ev.getPackage().getProject().getName() + " opened.");
        } catch (ExtensionException e) {
            System.out.println("Project closed by BlueJ");
        }
    }  
  
    public void packageClosing ( PackageEvent ev ) {
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

    public void terminate() {
        System.out.println ("PMD extension terminates");
    }
    
    public String getDescription () {
        return "PMD extension - finds unused code, empty blocks, and more!";
    }

    public URL getURL () {
        try {
            return new URL("http://pmd.sf.net/");
            //return new URL("http://pmd.sf.net/bluejextension.html");
        } catch ( Exception e ) {
            System.out.println ("Can't get PMD extension URL: "+e.getMessage());
            return null;
        }
    }
}


