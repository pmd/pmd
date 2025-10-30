/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Stores the integer IDs of string images. Every string that we found
 * during lexing is assigned a unique integer ID that identifies the
 * token.
 *
 */
abstract class TokenImageMap {
    // The first ID is 1, 0 is the ID of the EOF token.
    private int curImageId = 1;

    // This is shared to save memory on the lambda allocation, which is significant
    protected final Function<String, Integer> getNextImage = k -> curImageId++;

    abstract int getImageId(String image);

    abstract String imageFromId(int id);

    /**
     * Preallocate IDs for images. Must be called before we start building,
     * before we start parallel processing.
     *
     * @param constantImages Set of images for which to preallocate an ID
     */
    abstract void preallocImages(Set<String> constantImages);

    static final class SingleThreadedImageMap extends TokenImageMap {

        private final Object2IntMap<String> images = new Object2IntOpenHashMap<>();

        @Override
        int getImageId(String image) {
            return images.computeIfAbsent(image, super.getNextImage);
        }

        @Override
        String imageFromId(int id) {
            return images.object2IntEntrySet()
                         .stream()
                         .filter(it -> it.getIntValue() == id)
                         .findFirst()
                         .map(Map.Entry::getKey).orElse(null);
        }

        @Override
        void preallocImages(Set<String> constantImages) {
            for (String image : constantImages) {
                int id = super.curImageId++;
                images.put(image, id);
            }
        }
    }

    /**
     * When running with multiple threads, this map is a place of contention
     * between threads. Therefore, we use some caching mechanism to reduce
     * contention on the main map.
     */
    static class MultithreadedImageMap extends TokenImageMap {

        /** Central "source of truth" containing IDs for all the images encountered so far. */
        private final ConcurrentMap<String, Integer> images = new ConcurrentHashMap<>();

        /**
         * Common images that are allocated in a separate map to avoid contending on
         * the main concurrent hashmap. This includes the images of common tokens like
         * "public", "static", etc. Is populated during initialization and then shared
         * between all threads as read-only, without contention.
         */
        private final Object2IntMap<String> preallocatedImages = new Object2IntOpenHashMap<>();


        @Override
        void preallocImages(Set<String> constantImages) {
            for (String image : constantImages) {
                int id = super.curImageId++;
                preallocatedImages.put(image, id);
                images.put(image, id);
            }
        }

        @Override
        int getImageId(String newImage) {
            int prealloc = preallocatedImages.getOrDefault(newImage, -1);
            if (prealloc >= 0) {
                // Avoid contending on the concurrent map. This is a common path,
                // in Java programs, 2/3 of the tokens have a known image.
                return prealloc;
            }
            return computeImageIdConcurrent(newImage);
        }

        protected int computeImageIdConcurrent(String newImage) {
            return images.computeIfAbsent(newImage, this.getNextImage);
        }

        // This is a rare operation so it can be inefficient.
        @Override
        String imageFromId(int i) {
            return images.entrySet().stream()
                         .filter(it -> it.getValue() == i)
                         .findFirst().map(Map.Entry::getKey).orElse(null);
        }
    }

    /**
     * Adds a thread-local cache. Note this may at worse duplicate
     * the central map once per thread.
     */
    static final class MultiThreadedImageMapWithThreadLocalCache extends MultithreadedImageMap {

        private final ThreadLocal<Object2IntMap<String>> threadLocalCache
            = ThreadLocal.withInitial(Object2IntOpenHashMap::new);

        private final Object2IntFunction<String> lambda =
            k -> super.computeImageIdConcurrent((String) k);

        @Override
        protected int computeImageIdConcurrent(String newImage) {
            Object2IntMap<String> map = threadLocalCache.get();
            return map.computeIfAbsent(newImage, lambda);
        }
    }
}
