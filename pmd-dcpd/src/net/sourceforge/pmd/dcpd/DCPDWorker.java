/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 5:13:14 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.sourceforge.pmd.cpd.*;

import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.util.*;

public class DCPDWorker {

    private Job currentJob;
    private TokenSetsWrapper tsw;
    private JavaSpace space;

    public DCPDWorker() {
        try {
            space = Util.findSpace(Util.SPACE_SERVER);
            // register for future jobs
            space.notify(new Job(), null, new JobAddedListener(space, this), Lease.FOREVER, null);
            // get a job if there are any out there
            Job job = (Job)space.readIfExists(new Job(), null, 200);
            if (job != null) {
                jobAdded(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jobAdded(Job job) {
        try {
            currentJob = job;
            System.out.println("Received a job " + job.name + " , id is " + job.id.intValue());

            tsw = (TokenSetsWrapper)space.read(new TokenSetsWrapper(null, job.id), null, 100);
            System.out.println("Read a TokenSetsWrapper with " + tsw.tokenSets.size() + " token lists");

            doExpansion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doExpansion() throws RemoteException, UnusableEntryException, TransactionException, InterruptedException{
        Entry twQuery = space.snapshot(new TileWrapper(null, null, currentJob.id, TileWrapper.NOT_DONE, null, null, null));

        TileWrapper tileWrapper = null;
        while ((tileWrapper = (TileWrapper)space.take(twQuery, null, 100)) != null) {
            Occurrences results = expand(tileWrapper);
            for (Iterator i = results.getTiles();i.hasNext();) {
                Tile tile = (Tile)i.next();
                List theseOccurrences = marshal(results.getOccurrences(tile));
                for (int j=0; j<=theseOccurrences.size(); j++) {
                    TileWrapper newTW = new TileWrapper(tile, theseOccurrences, currentJob.id, TileWrapper.DONE, tileWrapper.originalTilePosition, new Integer(j), new Integer(theseOccurrences.size()));
                    space.write(newTW, null, Lease.FOREVER);
                }
            }
        }
    }

    private List marshal(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

    private Occurrences expand(TileWrapper tileWrapper) {
        Occurrences newOcc = new Occurrences(new CPDNullListener());
        for (Iterator i = tileWrapper.occurrences.iterator(); i.hasNext();) {
            TokenEntry tok = (TokenEntry)i.next();
            TokenList tokenSet = tsw.tokenSets.getTokenList(tok);
            if (tokenSet.hasTokenAfter(tileWrapper.tile, tok)) {
                TokenEntry token = (TokenEntry)tokenSet.get(tok.getIndex() + tileWrapper.tile.getTokenCount());
                // make sure the next token hasn't already been used in an occurrence
                if (!newOcc.contains(token)) {
                    Tile newTile = tileWrapper.tile.copy();
                    newTile.add(token);
                    newOcc.addTile(newTile, tok);
                }
            }
        }
        return newOcc;
    }

    public static void main(String[] args) {
        new DCPDWorker();
    }
}
