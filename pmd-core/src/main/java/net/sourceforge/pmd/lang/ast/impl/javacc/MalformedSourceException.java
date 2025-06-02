/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * A {@link FileAnalysisException} thrown when the source format is invalid,
 * for example if some unicode escapes cannot be translated.
 */
public class MalformedSourceException extends FileAnalysisException {

    private final FileLocation location;

    public MalformedSourceException(String message, Throwable cause, FileLocation fileLocation) {
        super(message, cause);
        this.location = Objects.requireNonNull(fileLocation);
        setFileId(fileLocation.getFileId());
    }

    @Override
    protected @NonNull FileLocation location() {
        return location;
    }

    @Override
    protected String errorKind() {
        return "Source format error";
    }
}
