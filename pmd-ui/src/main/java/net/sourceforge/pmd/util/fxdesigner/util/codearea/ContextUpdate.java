/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fxmisc.richtext.model.StyleSpans;


/**
 * Represents an update on the code area. Updates are queued within a code area until they are
 * accepted by {@link StyleContext#executeUpdate(ContextUpdate)}.
 */
class ContextUpdate {

    private static final ContextUpdate UNIT = new ContextUpdate(Collections.emptyMap());
    private final Map<String, LayerUpdate> spansById = new HashMap<>();
    ContextUpdate(String layerId, LayerUpdate idsUpdates) {
        spansById.put(layerId, idsUpdates);
    }


    private ContextUpdate(Map<String, LayerUpdate> updateMap) {
        spansById.putAll(updateMap);
    }


    public Map<String, LayerUpdate> getSpansById() {
        return spansById;
    }


    private void accumulate(String layerId, LayerUpdate update) {
        LayerUpdate previous = spansById.putIfAbsent(layerId, update);
        if (previous != null) {
            spansById.put(layerId, LayerUpdate.reduce(previous, update));
        }
    }


    public static ContextUpdate unit() {
        return UNIT;
    }


    public static ContextUpdate resetUpdate(String layerId) {
        return new ContextUpdate(layerId, new LayerUpdate(true, Collections.emptySet()));
    }


    public static ContextUpdate resetUpdate(String layerId, StyleSpans<Collection<String>> spans) {
        return new ContextUpdate(layerId, new LayerUpdate(true, Collections.singleton(spans)));
    }


    static ContextUpdate reduce(ContextUpdate update, ContextUpdate update2) {
        Map<String, LayerUpdate> newMap = new HashMap<>(update.spansById);

        ContextUpdate result = new ContextUpdate(newMap);

        for (Entry<String, LayerUpdate> entry : update2.spansById.entrySet()) {
            result.accumulate(entry.getKey(), entry.getValue());
        }
        return result;
    }


    static class LayerUpdate {
        private final boolean reset;
        private final Collection<StyleSpans<Collection<String>>> updates;


        LayerUpdate(boolean reset, Collection<StyleSpans<Collection<String>>> updates) {
            this.reset = reset;
            this.updates = updates;
        }


        public boolean isReset() {
            return reset;
        }


        public Collection<StyleSpans<Collection<String>>> getUpdates() {
            return updates;
        }


        /** u2 is the most recent. */
        static LayerUpdate reduce(LayerUpdate u1, LayerUpdate u2) {
            if (u2.reset) {
                return u2;
            }

            Set<StyleSpans<Collection<String>>> merged = new HashSet<>(u1.updates);
            merged.addAll(u2.updates);

            return new LayerUpdate(u1.reset, merged);
        }

    }
}
