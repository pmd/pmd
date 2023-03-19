/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A virtual path for a {@link TextFile}.
 *
 * @author Clément Fournier
 */
public interface FileId extends Comparable<FileId> {

    /**
     * The name used for a file that has no name. This is mostly only
     * relevant for unit tests.
     */
    FileId UNKNOWN = fromPathLikeString("(unknown file)");

    @Nullable FileId getParentFsPath();

    String toUriString();

    String getFileName();

    String getOriginalPath();

    String toAbsolutePath();


    @Override
    boolean equals(Object o);


    @Override
    default int compareTo(FileId o) {
        return this.toUriString().compareTo(o.toUriString());
    }

    FileId STDIN = new FileId() {
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
        public String getOriginalPath() {
            return "stdin";
        }

        @Override
        public @Nullable FileId getParentFsPath() {
            return null;
        }
    };

    static FileId fromPathLikeString(String str) {
        String[] segments = str.split("[/\\\\]");
        if (segments.length == 0) {
            throw new IllegalArgumentException("Invalid path id: '" + str + "'");
        }
        String fname = segments[segments.length - 1];
        return new FileId() {
            @Override
            public String toAbsolutePath() {
                return Paths.get(str).toAbsolutePath().toString();
            }

            @Override
            public String toUriString() {
                // this is mostly just to make sure that renderers do
                // not use that by default.
                return "unknown://" + str;
            }

            @Override
            public String getFileName() {
                return fname;
            }

            @Override
            public String getOriginalPath() {
                return str;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof FileId
                    && ((FileId) obj).toUriString().equals(this.toUriString());
            }

            @Override
            public int hashCode() {
                return toUriString().hashCode();
            }

            @Override
            public @Nullable FileId getParentFsPath() {
                return null;
            }
        };
    }

    static FileId forPath(Path path, @Nullable FileId fsPath) {
        return new FileId() {
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
            public String getOriginalPath() {
                return path.toString();
            }

            @Override
            public @Nullable FileId getParentFsPath() {
                return fsPath;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof FileId
                    && ((FileId) obj).toUriString().equals(this.toUriString());
            }

            @Override
            public int hashCode() {
                return toUriString().hashCode();
            }

            @Override
            public String toString() {
                return "PathId.forPath(" + path + ")";
            }
        };
    }

    static FileId forPath(Path path) {
        return forPath(path, null);
    }

    static FileId asChildOf(FileId self, FileId parentFsPath) {
        return new FileId() {
            @Override
            public @Nullable FileId getParentFsPath() {
                return parentFsPath;
            }

            @Override
            public String toUriString() {
                return self.toUriString();
            }

            @Override
            public String getFileName() {
                return self.getFileName();
            }

            @Override
            public String getOriginalPath() {
                return self.getOriginalPath();
            }

            @Override
            public String toAbsolutePath() {
                return self.toAbsolutePath();
            }
        };
    }
}
