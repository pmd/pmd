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
import net.jini.core.lease.Lease;
import net.sourceforge.pmd.cpd.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Iterator;

public class DCPD {

    private JavaSpace space;
    private TokenSets tokenSets = new TokenSets();

    public DCPD(String javaSpaceURL) {
        try {
            space = Util.findSpace("mordor");
            space.write(new Job("test"), null, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't connect to the space on " + javaSpaceURL);
        }

/*
        try {
            add("C:\\j2sdk1.4.0_01\\src\\java\\lang\\", true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Couldn't load the files");
        }
*/
    }

    private void add(String dir, boolean recurse) throws IOException {
        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File)i.next());
        }
    }

    private void add(int fileCount, File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    public static void main(String[] args) {
        new DCPD("jini://mordor");
    }
}
