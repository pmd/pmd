/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 5:25:40 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;

import java.rmi.RemoteException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

public class Util {

    private static Util singleton;
    public static Util getInstance() {
        if (singleton == null) {
            singleton = new Util();
        }
        return singleton;
    }
    public String getSpaceServer() {
        try {
            Properties props = new Properties();
            InputStream is = (getClass().getClassLoader().getResourceAsStream("net/sourceforge/pmd/dcpd/dcpd.properties"));
            if (is == null) {
                throw new IOException();
            }
            props.load(is);
            return props.getProperty("dcpd.spaceserver");
        } catch (IOException e) {
            throw new RuntimeException("Unable to load dcpd.properties!");
        }
    }

    public JavaSpace findSpace(String serverName) throws ClassNotFoundException, MalformedURLException, IOException, RemoteException {
        ServiceRegistrar registrar = (new LookupLocator("jini://" + serverName)).getRegistrar();
        ServiceMatches sm = registrar.lookup(new ServiceTemplate(null, new Class[] {JavaSpace.class}, new Entry[] {}),  1);
        return (JavaSpace)sm.items[0].service;
    }

    public static void main(String[] args) {
        try {
            int objectCount = 0;
            if (args[0].equals("clear")) {
                JavaSpace space = Util.getInstance().findSpace(Util.getInstance().getSpaceServer());
                Entry e = null;
                while ((e = space.take(null, null, 100)) != null) {
                    objectCount++;
                    if (objectCount % 100 == 0) {
                        System.out.println(objectCount + " objects taken so far");
                    }
                    //System.out.println("took " + e);
                }
            } else {
                System.out.println("Usage: clear");
            }
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
