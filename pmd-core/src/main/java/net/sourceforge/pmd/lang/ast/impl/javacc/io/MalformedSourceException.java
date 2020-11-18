/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.util.document.FileLocation;

/**
 * A {@link FileAnalysisException} thrown when the source format is invalid,
 * for example if some unicode escapes cannot be translated.
 */
public class MalformedSourceException extends FileAnalysisException {

    private final FileLocation location;

    public MalformedSourceException(String message, Throwable cause, FileLocation fileLocation) {
        super(message, cause);
        this.location = Objects.requireNonNull(fileLocation);
        setFileName(fileLocation.getFileName());
    }

    @Override
    protected String positionToString() {
        return super.positionToString() + " at " + location.startPosToString();
    }

    @Override
    protected String errorKind() {
        return "Source format error";
    }
}
