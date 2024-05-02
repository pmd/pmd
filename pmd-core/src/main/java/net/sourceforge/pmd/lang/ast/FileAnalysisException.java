/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * An exception that occurs while processing a file. Subtypes include
 * <ul>
 * <li>{@link MalformedSourceException}: error in source format, eg invalid character escapes (in case that happens before lexing)
 * <li>{@link LexException}: lexical syntax errors
 * <li>{@link ParseException}: syntax errors
 * <li>{@link SemanticException}: exceptions occurring after the parsing
 * phase, because the source code is semantically invalid
 * </ul>
 */
public class FileAnalysisException extends RuntimeException {

    private FileId fileId = FileId.UNKNOWN;

    public FileAnalysisException() {
        super();
    }

    public FileAnalysisException(String message) {
        super(message);
    }

    public FileAnalysisException(Throwable cause) {
        super(cause);
    }

    public FileAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAnalysisException setFileId(FileId fileId) {
        this.fileId = Objects.requireNonNull(fileId);
        return this;
    }

    protected boolean hasFileName() {
        return !FileId.UNKNOWN.equals(fileId);
    }

    /**
     * The name of the file in which the error occurred.
     */
    public @NonNull FileId getFileId() {
        return fileId;
    }

    @Override
    public final String getMessage() {
        return errorKind() + StringUtils.uncapitalize(positionToString()) + ": " + super.getMessage();
    }

    protected String errorKind() {
        return "Error";
    }

    protected @Nullable FileLocation location() {
        return null;
    }

    private String positionToString() {
        String result = "";
        if (hasFileName()) {
            result += " in file '" + getFileId().getOriginalPath() + "'";
        }
        FileLocation loc = location();
        if (loc != null) {
            result += " at " + loc.startPosToString();
        }
        return result;
    }


    /**
     * Wraps the cause into an analysis exception. If it is itself an analysis
     * exception, just returns it after setting the filename for context.
     *
     * @param fileId Filename
     * @param message  Context message, if the cause is not a {@link FileAnalysisException}
     * @param cause    Exception to wrap
     *
     * @return An exception
     */
    public static FileAnalysisException wrap(@NonNull FileId fileId, @NonNull String message, @NonNull Throwable cause) {
        if (cause instanceof FileAnalysisException) {
            return ((FileAnalysisException) cause).setFileId(fileId);
        }

        String fullMessage = "In file '" + fileId.getAbsolutePath() + "': " + message;

        return new FileAnalysisException(fullMessage, cause).setFileId(fileId);
    }
}
