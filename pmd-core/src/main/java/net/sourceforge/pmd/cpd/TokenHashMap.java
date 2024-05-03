/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

/**
 * @author Clément Fournier
 */
class TokenHashMap {

    private final IntObjectMap<Object> markGroups;
    private final List<List<SmallTokenEntry>> listSink;

    TokenHashMap(int size) {
        markGroups = new IntObjectHashMap<>(size);
        listSink = new ArrayList<>();
    }

    public List<List<SmallTokenEntry>> getFinalMatches() {
        return listSink;
    }

    /**
     * This routine adds a token to the hash table. Tokens that have the same hash are placed together into a list.
     * Those lists are then handed to the match finder to compute the similarity between tokens with the same hash.
     * Reducing the size of those lists is a crucial time optimization, because the match finder is quadratic
     * in the length of the input list. One important optimization that we do is therefore to prune tokens from one
     * of those list if they have the same preceding token. This is because they are then necessarily a suffix of a
     * larger match, so they are useless.
     *
     * @param hash      Hash of the current token
     * @param thisEntry The current token
     */
    void addTokenToHashTable(int hash, SmallTokenEntry thisEntry) {
        int index = markGroups.indexOf(hash);
        if (markGroups.indexExists(index)) {
            Object curEntry = markGroups.indexGet(index);
            if (curEntry instanceof SmallTokenEntry) {
                SmallTokenEntry fstTok = (SmallTokenEntry) curEntry;
                if (fstTok.hasSamePrevToken(thisEntry)) {
                    // part of a larger match, yeet them out
                    markGroups.indexRemove(index);
                    return;
                }
                List<SmallTokenEntry> arr = new ArrayList<>(2);
                arr.add(fstTok);
                arr.add(thisEntry);
                markGroups.indexReplace(index, arr);
                listSink.add(arr);
            } else if (curEntry instanceof List) {
                @SuppressWarnings("unchecked")
                List<SmallTokenEntry> list = (List<SmallTokenEntry>) curEntry;
                boolean hadMatch = list.removeIf(thisEntry::hasSamePrevToken);
                if (!hadMatch) {
                    list.add(thisEntry);
                }
            }
        } else {
            markGroups.indexInsert(index, hash, thisEntry);
        }
    }

}
