/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * An identifier for a {@link TextFile}. This is not a path, but provides
 * several methods to be rendered into path-like strings in different formats
 * (for use mostly by {@link Renderer} instances). File IDs are used to
 * identify files e.g. in {@link RuleViolation}, {@link FileLocation}, {@link TextFile}.
 *
 * <p>Note that the addressed file may not be an actual file on a file system.
 * For instance, you can create file ids from strings ({@link #fromPathLikeString(String)}),
 * or use {@link #STDIN} to address standard input. The rendering methods
 * of this interface (like {@link #toAbsolutePath()}) do not have to return
 * actual paths for those exotic files, and operate on a best-effort basis.
 *
 * @author Clément Fournier
 */
public interface FileId extends Comparable<FileId> {

    /**
     * The name used for an unknown file. This is mostly only
     * relevant for unit tests.
     */
    FileId UNKNOWN = new FileId() {
        @Override
        public String getFileName() {
            return "(unknown)";
        }

        @Override
        public String getOriginalPath() {
            return "(unknown)";
        }

        @Override
        public String toAbsolutePath() {
            return getOriginalPath();
        }

        @Override
        public String toUriString() {
            return "file://" + getOriginalPath();
        }

        @Override
        public @Nullable FileId getParentFsPath() {
            return null;
        }
    };

    /** The virtual file ID for standard input. */
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


    /**
     * Return the simple file name, like {@link Path#getFileName()}.
     * This includes the extension.
     */
    String getFileName();

    /**
     * Return the path as it was input by the user. This may be a
     * relative or absolute path.
     */
    String getOriginalPath();

    /**
     * Return an absolute path to this file in its containing file system.
     * If the file is in a zip file, then this returns a path from the
     * zip root, and does not include the path of the zip itself.
     */
    String toAbsolutePath();

    /**
     * Return a string that looks like a URI pointing to this file.
     * TODO what does this do for a zip file?
     */
    String toUriString();

    /**
     * If this file is in a nested filesystem (eg a zip file), return
     * the file ID of the container in the outer file system. Return
     * null if this is in the root file system.
     */
    @Nullable FileId getParentFsPath();


    /**
     * Two file IDs are equal if they have the same {@link #toUriString()}.
     *
     * @param o Object
     */
    @Override
    boolean equals(Object o);


    @Override
    default int compareTo(FileId o) {
        return this.toAbsolutePath().compareTo(o.toAbsolutePath());
    }

    /**
     * This method is intentionally only meant for debugging, and its output
     * is unspecified. Code that needs a string representation should use one
     * of the named string conversion methods.
     */
    @Override
    String toString();

    // todo doc

    static FileId fromPathLikeString(String str) {
        String[] segments = str.split("[/\\\\]");
        String fname;
        if (segments.length == 0) {
            fname = ""; // this must be "/" or "\\"
        } else {
            fname = segments[segments.length - 1];
        }
        return new FileId() {
            final String absPath = Paths.get(str).toAbsolutePath().toString();
            @Override
            public String toAbsolutePath() {
                return absPath;
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
            // Compute these beforehand as that will fail if the path
            // is invalid (better now than later).
            // Also, not hitting the filesystem every time we want to
            // do a compareTo is good for performance.
            final String absPath = path.normalize().toAbsolutePath().toString();
            final String uriString = path.normalize().toUri().toString();
            final String fileName = path.getFileName().toString();
            final String origPath = path.toString();

            @Override
            public String toAbsolutePath() {
                return absPath;
            }

            @Override
            public String toUriString() {
                return uriString;
            }

            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public String getOriginalPath() {
                return origPath;
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
