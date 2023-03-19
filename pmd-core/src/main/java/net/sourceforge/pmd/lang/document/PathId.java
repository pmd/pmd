/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A virtual path for a {@link TextFile}.
 *
 * @author Clément Fournier
 */
public interface PathId extends Comparable<PathId> {

    /**
     * The name used for a file that has no name. This is mostly only
     * relevant for unit tests.
     */
    PathId UNKNOWN = fromPathLikeString("(unknown file)");

    String toUriString();

    String getFileName();
    String getNiceFileName();

    String toAbsolutePath();


    @Override
    boolean equals(Object o);


    @Override
    default int compareTo(PathId o) {
        return this.toUriString().compareTo(o.toUriString());
    }

    PathId STDIN = new PathId() {
        @Override
        public String toAbsolutePath() {
            return "stdin";
        }

        @Override
        public String toUriString() {
            return "stdin";
        }

        @Override
        public String getFileName() {
            return "stdin";
        }

        @Override
        public String getNiceFileName() {
            return "stdin";
        }
    };

    static PathId fromPathLikeString(String str) {
        String[] segments = str.split("[/\\\\]");
        if (segments.length == 0) {
            throw new IllegalArgumentException("Invalid path id: '" + str + "'");
        }
        String fname = segments[segments.length - 1];
        return new PathId() {
            @Override
            public String toAbsolutePath() {
                return Paths.get(str).toAbsolutePath().toString();
            }

            @Override
            public String toUriString() {
                return str;
            }

            @Override
            public String getFileName() {
                return fname;
            }

            @Override
            public String getNiceFileName() {
                return str;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof PathId
                    && ((PathId) obj).toUriString().equals(this.toUriString());
            }
        };
    }

    static PathId fromPath(Path path) {
        return new PathId() {
            @Override
            public String toAbsolutePath() {
                return path.normalize().toAbsolutePath().toString();
            }

            @Override
            public String toUriString() {
                return path.normalize().toUri().toString();
            }

            @Override
            public String getFileName() {
                return path.getFileName().toString();
            }

            @Override
            public String getNiceFileName() {
                return path.toString();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof PathId
                    && ((PathId) obj).toUriString().equals(this.toUriString());
            }
        };
    }
}
