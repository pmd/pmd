/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 4:56:10 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.io.IOException;

public class DCPD {

    private JavaSpace space;

    public DCPD(String javaSpaceServerName) {
        try {
            space = getSpace(javaSpaceServerName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't connect to the space on " + javaSpaceServerName);
        }
    }

    private JavaSpace getSpace(String javaSpaceServerName) throws ClassNotFoundException, MalformedURLException, IOException, RemoteException {
        ServiceRegistrar registrar = (new LookupLocator(javaSpaceServerName)).getRegistrar();
        ServiceMatches sm = registrar.lookup(new ServiceTemplate(null, new Class[] {JavaSpace.class}, new Entry[] {}),  1);
        return (JavaSpace)sm.items[0].service;
    }


    public static void main(String[] args) {
        new DCPD("jini://mordor");
    }
}
