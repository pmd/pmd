/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Contains types to model text files and handle operations on text.
 * Parser implementations build upon this framework. This package is
 * built around the type {@link net.sourceforge.pmd.lang.document.TextFile},
 * which represents a source file and allows reading and writing. The
 * class {@link net.sourceforge.pmd.lang.document.TextDocument} models
 * an in-memory snapshot of the state of a TextFile, and exposes information
 * like line/offset mapping.
 *
 * @see net.sourceforge.pmd.lang.document.TextFile
 * @see net.sourceforge.pmd.lang.document.TextDocument
 * @see net.sourceforge.pmd.reporting.Reportable
 */
package net.sourceforge.pmd.lang.document;
