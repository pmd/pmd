
package net.sourceforge.pmd.lang.vm.ast;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.sourceforge.pmd.lang.vm.util.LogUtil;

/**
 * Exception to indicate problem happened while constructing #macro()
 *
 * For internal use in parser - not to be passed to app level
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: MacroParseException.java 735709 2009-01-19 14:30:03Z byron $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class MacroParseException extends ParseException {
    private final String templateName;

    /**
     * Version Id for serializable
     */
    private static final long serialVersionUID = -4985224672336070689L;

    /**
     * @param msg
     * @param templateName
     * @param currentToken
     */
    public MacroParseException(final String msg, final String templateName, final Token currentToken) {
        super(msg + " at ");
        this.currentToken = currentToken;
        this.templateName = templateName;
    }

    /**
     * returns the Template name where this exception occured.
     *
     * @return The Template name where this exception occured.
     * @since 1.5
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * returns the line number where this exception occured.
     *
     * @return The line number where this exception occured.
     * @since 1.5
     */
    public int getLineNumber() {
        if (currentToken != null && currentToken.next != null) {
            return currentToken.next.beginLine;
        } else if (currentToken != null) {
            return currentToken.beginLine;
        } else {
            return -1;
        }
    }

    /**
     * returns the column number where this exception occured.
     *
     * @return The column number where this exception occured.
     * @since 1.5
     */
    public int getColumnNumber() {
        if (currentToken != null && currentToken.next != null) {
            return currentToken.next.beginColumn;
        } else if (currentToken != null) {
            return currentToken.beginColumn;
        } else {
            return -1;
        }
    }

    /**
     * This method has the standard behavior when this object has been created
     * using the standard constructors. Otherwise, it uses "currentToken" and
     * "expectedTokenSequences" to generate a parse error message and returns
     * it. If this object has been created due to a parse error, and you do not
     * catch it (it gets thrown from the parser), then this method is called
     * during the printing of the final stack trace, and hence the correct error
     * message gets displayed.
     *
     * @return the current message.
     * @since 1.5
     */
    @Override
    public String getMessage() {
        final StringBuffer sb = new StringBuffer(super.getMessage());
        appendTemplateInfo(sb);
        return sb.toString();
    }

    /**
     * @param sb
     * @since 1.5
     */
    protected void appendTemplateInfo(final StringBuffer sb) {
        sb.append(LogUtil.formatFileString(getTemplateName(), getLineNumber(), getColumnNumber()));
        sb.append(eol);
    }
}
