/*
 * User: tom
 * Date: Aug 21, 2002
 * Time: 4:36:49 PM
 */

import net.jini.core.entry.Entry;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.TokenEntry;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TSSWrapper implements Entry {

    public TokenList tls[] = null;

    public TSSWrapper() {
    }

    public TSSWrapper(TokenSets tss) {
        tls = new TokenList[ tss.size() ];

        int pos = 0;
        for (Iterator i = tss.iterator();i.hasNext();) {
            TokenList tl = (TokenList)i.next();
            tls[pos] = tl;
            pos++;
        }
    }

    public int size() {
        return tls.length;
    }
}
