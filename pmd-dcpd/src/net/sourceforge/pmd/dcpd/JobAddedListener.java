/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 5:19:47 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JobAddedListener extends UnicastRemoteObject implements RemoteEventListener {

    public JavaSpace space;

    public JobAddedListener() throws RemoteException {}

    public JobAddedListener(JavaSpace space)  throws RemoteException  {
        this.space = space;
    }

    public void notify(RemoteEvent event) throws UnknownEventException, RemoteException {
        System.out.println("HOWDY!");
        try {
            if (space == null) {
                System.out.println("SPACE IS NULL");
            } else {
                Job job = (Job)space.take(new Job("test"), null, 1000);
                if (job == null) {
                    System.out.println("No job found");
                } else {
                    System.out.println("job = " + job.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
