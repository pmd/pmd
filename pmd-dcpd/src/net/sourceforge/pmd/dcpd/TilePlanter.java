/*
 * User: tom
 * Date: Sep 6, 2002
 * Time: 4:11:42 PM
 */
package net.sourceforge.pmd.dcpd;

import net.sourceforge.pmd.cpd.Occurrences;
import net.sourceforge.pmd.cpd.Tile;
import net.jini.core.transaction.TransactionException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class TilePlanter {

    private Job job;
    private JavaSpace space;

    public TilePlanter(JavaSpace space, Job job) {
        this.job = job;
        this.space = space;
    }

    public void plant(List batches) throws TransactionException, RemoteException {
        for (Iterator i = batches.iterator(); i.hasNext();) {
            Batch batch = (Batch)i.next();
            space.write(batch, null, Lease.FOREVER);
            if (batch.sequenceID.intValue()+1 % 10 == 0) {
                System.out.println("Planted " + batch.sequenceID.intValue() + " batches so far");
            }
        }

    }
}
