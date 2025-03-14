/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

final class Bug1333 {
    private static final Logger LOG = LoggerFactory.getLogger(Foo.class);

    public void deleteDirectoriesByNamePattern() {
        delete(path -> deleteDirectory(path));
    }

    private void delete(Consumer<? super String> consumer) {
        LOG.debug(consumer.toString());
    }

    private void deleteDirectory(String path) {
        LOG.debug(path);
    }
}
