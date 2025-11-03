/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Stores the integer IDs of string images. Every string that we found
 * during lexing is assigned a unique integer ID that identifies the
 * token.
 *
 * <p>When running with multiple threads, this map is a place of contention
 * between threads. Therefore, we use some caching mechanism to reduce
 * contention on the main map.
 */
final class TokenImageMap {

    /** Central "source of truth" containing IDs for all the images encountered so far. */
    private final Map<String, Integer> images;

    /**
     * Common images that are allocated in a separate map to avoid contending on
     * the main concurrent hashmap. This includes the images of common tokens like
     * "public", "static", etc. Is populated during initialization and then shared
     * between all threads as read-only, without contention.
     */
    private final Object2IntMap<String> preallocatedImages = new Object2IntOpenHashMap<>();

    // The first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;

    // This is shared to save memory on the lambda allocation, which is significant
    private final Function<String, Integer> getNextImage = k -> curImageId++;

    TokenImageMap(int numThreads) {
        if (numThreads <= 1) {
            images = new HashMap<>();
        } else {
            images = new ConcurrentHashMap<>();
        }
    }

    /**
     * Preallocate IDs for images. Must be called before we start building,
     * before we start parallel processing.
     *
     * @param constantImages Set of images for which to preallocate an ID
     */
    void preallocImages(Set<String> constantImages) {
        for (String image : constantImages) {
            int id = curImageId++;
            preallocatedImages.put(image, id);
            images.put(image, id);
        }
    }

    int getImageId(String newImage) {
        int prealloc = preallocatedImages.getOrDefault(newImage, -1);
        if (prealloc >= 0) {
            // Avoid contending on the concurrent map. This is a common path,
            // in Java programs, 2/3 of the tokens have a known image.
            return prealloc;
        }
        return images.computeIfAbsent(newImage, this.getNextImage);
    }

    // This is a rare operation so it can be inefficient.
    String imageFromId(int i) {
        return images.entrySet().stream()
                     .filter(it -> it.getValue() == i)
                     .findFirst().map(Map.Entry::getKey).orElse(null);
    }

}
