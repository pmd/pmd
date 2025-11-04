/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;
import net.sourceforge.pmd.util.AssertionUtil;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * @author Clément Fournier
 */
class TokenHashMap {

    private final Long2ObjectMap<Object> markGroups;
    //    private final Map<Integer, Object> markGroups;
    private final List<List<SmallTokenEntry>> listSink;
    // this is shared to save memory on the lambda allocation, which is significant
    private final BiFunction<Object, Object, Object> mergeFunction = this::mergeEntries;

    TokenHashMap(int size) {
        markGroups = new Long2ObjectOpenHashMap<>(size);
        //        markGroups = new HashMap<>(size);
        listSink = new ArrayList<>();
    }

    public List<List<SmallTokenEntry>> getFinalMatches() {
        listSink.removeIf(it -> it.size() < 2);
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
    void addTokenToHashTable(long hash, SmallTokenEntry thisEntry) {
        markGroups.merge(hash, thisEntry, this.mergeFunction);
    }

    private Object mergeEntries(Object curEntry, Object thisEntry) {
        SmallTokenEntry newEntry = (SmallTokenEntry) thisEntry;
        if (curEntry instanceof SmallTokenEntry) {
            SmallTokenEntry curTok = (SmallTokenEntry) curEntry;
            if (curTok.hasSamePrevToken(newEntry)) {
                // part of a larger match, yeet them out
                return null;
            }
            List<SmallTokenEntry> arr = new ArrayList<>(2);
            arr.add(curTok);
            arr.add(newEntry);
            listSink.add(arr);
            return arr;
        } else if (curEntry instanceof List) {
            @SuppressWarnings("unchecked")
            List<SmallTokenEntry> list = (List<SmallTokenEntry>) curEntry;
            boolean hadMatch = list.removeIf(newEntry::hasSamePrevToken);
            if (!hadMatch) {
                list.add(newEntry);
            } else if (list.isEmpty()) {
                return null;
            }
            return list;
        }
        throw AssertionUtil.shouldNotReachHere("Unexpected token entry type: " + curEntry.getClass());
    }

}
