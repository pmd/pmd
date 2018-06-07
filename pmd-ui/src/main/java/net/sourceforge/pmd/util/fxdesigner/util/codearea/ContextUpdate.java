/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Represents an update on the code area. Updates are queued within a code area until they are
 * accepted by {@link StyleContext#executeUpdate(ContextUpdate)}.
 */
class ContextUpdate {

    private static final ContextUpdate UNIT = new ContextUpdate(Collections.emptyMap());
    private final Map<String, LayerUpdate> spansById = new HashMap<>();


    private ContextUpdate(String layerId, LayerUpdate idsUpdates) {
        spansById.put(layerId, idsUpdates);
    }


    private ContextUpdate(Map<String, LayerUpdate> updateMap) {
        spansById.putAll(updateMap);
    }

    /**
     * Updates the given style context according to the
     * changes specified by this object. If some layer
     * ids don't exist, they're ignored.
     */
    public void apply(StyleContext context) {
        spansById.forEach((id, layerUpdate) -> {
            context.getLayer(id).ifPresent(layerUpdate::apply);
        });
    }


    /** Accumulates the given update into the update already contained in this one if it exists. */
    private void accumulate(String layerId, LayerUpdate update) {
        LayerUpdate previous = spansById.putIfAbsent(layerId, update);
        if (previous != null) {
            spansById.put(layerId, LayerUpdate.reduce(previous, update));
        }
    }


    /**
     * Returns an update that performs nothing.
     */
    public static ContextUpdate unit() {
        return UNIT;
    }


    /**
     * Returns an update that resets the given layer entirely,
     * stripping it of all styling.
     *
     * @param layerId The id of layer to reset
     */
    public static ContextUpdate clearUpdate(String layerId) {
        return new ContextUpdate(layerId, new LayerUpdate(true, UniformStyleCollection.empty()));
    }


    /**
     * Returns an update that replaces the styling contained within
     * the given layer by the style spans parameter.
     */
    public static ContextUpdate resetUpdate(String layerId, UniformStyleCollection spans) {
        return new ContextUpdate(layerId, new LayerUpdate(true, spans));
    }


    /**
     * Returns an update that may or may not reset the updated layer
     * depending on the boolean parameter.
     */
    public static ContextUpdate layerUpdate(String layerId, boolean reset, UniformStyleCollection spans) {
        return new ContextUpdate(layerId, new LayerUpdate(reset, spans));
    }


    /**
     * Merges two context updates into a single one which is
     * equivalent to the previous ones.
     */
    static ContextUpdate reduce(ContextUpdate update, ContextUpdate update2) {
        Map<String, LayerUpdate> newMap = new HashMap<>(update.spansById);

        ContextUpdate result = new ContextUpdate(newMap);

        for (Entry<String, LayerUpdate> entry : update2.spansById.entrySet()) {
            result.accumulate(entry.getKey(), entry.getValue());
        }
        return result;
    }


    /**
     * Represents an update to carry out on a style layer.
     */
    private static class LayerUpdate {
        private final boolean reset;
        private final Collection<UniformStyleCollection> updates;


        LayerUpdate(boolean reset, UniformStyleCollection updates) {
            this.reset = reset;
            this.updates = Collections.singleton(updates);
        }


        LayerUpdate(boolean reset, Collection<UniformStyleCollection> updates) {
            this.reset = reset;
            this.updates = updates;
        }


        void apply(StyleLayer layer) {
            if (reset) {
                layer.clearStyles();
            }
            updates.forEach(layer::styleNodes);
        }


        /** Reduces two updates into an equivalent one. */
        static LayerUpdate reduce(LayerUpdate older, LayerUpdate newer) {
            if (newer == older) {
                return older;
            }

            if (newer.reset) {
                return newer;
            }

            List<UniformStyleCollection> merged = Stream.concat(older.updates.stream(), newer.updates.stream()).collect(Collectors.toList());
            return new LayerUpdate(older.reset, merged);
        }
    }
}
