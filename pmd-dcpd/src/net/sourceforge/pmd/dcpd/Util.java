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

import java.rmi.RemoteException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Util {

    public static JavaSpace findSpace(String serverName) throws ClassNotFoundException, MalformedURLException, IOException, RemoteException {
        ServiceRegistrar registrar = (new LookupLocator("jini://" + serverName)).getRegistrar();
        ServiceMatches sm = registrar.lookup(new ServiceTemplate(null, new Class[] {JavaSpace.class}, new Entry[] {}),  1);
        return (JavaSpace)sm.items[0].service;
    }
}
