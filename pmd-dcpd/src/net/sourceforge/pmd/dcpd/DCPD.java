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
    private Job job;
    private TokenSetsWrapper tokenSetWrapper;

    public DCPD(String javaSpaceURL) {
        try {
            System.out.println("Connecting to JavaSpace");
            space = Util.findSpace(javaSpaceURL);

            System.out.println("Tokenizing");
            job = new Job("java_lang", new Integer(1));
            tokenSetWrapper = new TokenSetsWrapper(loadTokens("C:\\j2sdk1.4.0_01\\src\\java\\lang\\ref\\", true), job.id);

            System.out.println("Writing the TokenSetsWrapper to the space");
            space.write(tokenSetWrapper, null, Lease.FOREVER);

            System.out.println("Crunching");
            DGST dgst = new DGST(space, job, tokenSetWrapper.tokenSets, 30);
            Results results = dgst.crunch(new CPDListenerImpl());

            System.out.println("Cleaning up");
            space.take(tokenSetWrapper, null, 200);
            space.take(job, null, 200);

            System.out.println(render(results));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't connect to the space on " + javaSpaceURL);
        }
    }

    public String getImage(Tile tile, Results results) {
        try {
            Iterator i = results.getOccurrences(tile);
            TokenEntry firstToken = (TokenEntry)i.next();
            TokenList tl = tokenSetWrapper.tokenSets.getTokenList(firstToken);
            int endLine = firstToken.getBeginLine()+ results.getTileLineCount(tile, tokenSetWrapper.tokenSets);
            return tl.getSlice(firstToken.getBeginLine()-1, endLine-1);
        } catch (Exception ex) {ex.printStackTrace(); }
        return "";
    }

    protected String EOL = System.getProperty("line.separator", "\n");

    private String render(Results results) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append("=============================================================");
            sb.append(EOL);
            sb.append("A " + results.getTileLineCount(tile, tokenSetWrapper.tokenSets) + " line (" + tile.getTokenCount() + " tokens) duplication:");
            sb.append(EOL);
            for (Iterator j = results.getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                sb.append("Starting at line " + tok.getBeginLine() + " in " + tok.getTokenSrcID());
                sb.append(EOL);
            }
            sb.append(getImage(tile, results));
            sb.append(EOL);
        }
        return sb.toString();
    }

    private TokenSets loadTokens(String dir, boolean recurse) throws IOException {
        TokenSets tokenSets = new TokenSets();
        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        for (Iterator i = files.iterator(); i.hasNext();) {
            tokenSets.add(tokenizeFile(files.size(), (File)i.next()));
        }
        return tokenSets;
    }

    private TokenList tokenizeFile(int fileCount, File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        return ts;
    }

    public static void main(String[] args) {
        new DCPD(Util.SPACE_SERVER);
    }
}
