/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.List;
import java.util.Iterator;

public class LookupController {

    public void lookup(NameOccurrences occs) {
        NameDeclaration decl = null;
        for (Iterator i = occs.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence)i.next();
            Search search = new Search(occ);
            if (decl == null) {
                // doing the first name lookup
                decl = search.execute();
                if (decl == null) {
                    // we can't find it, so just give up
                    // when we decide searches across compilation units like a compiler would, we'll
                    // force this to either find a symbol or throw a "cannot resolve symbol" Exception
                    break;
                }
            } else {
                // now we've got a scope we're starting with, so work from there
                decl = search.execute(decl.getNode().getScope());
            }
        }
    }
}
