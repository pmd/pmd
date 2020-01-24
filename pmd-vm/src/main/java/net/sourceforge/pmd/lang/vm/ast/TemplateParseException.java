
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
 * This is an extension of the ParseException, which also takes a template name.
 *
 * <p>see also the original <code>org.apache.velocity.runtime.parser.ParseException</code></p>
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: TemplateParseException.java 703544 2008-10-10 18:15:53Z nbubna
 *          $
 * @since 1.5
 */
@Deprecated
public class TemplateParseException extends ParseException {
    private static final long serialVersionUID = -3146323135623083918L;

    /**
     * This is the name of the template which contains the parsing error, or
     * null if not defined.
     */
    private final String templateName;

    private boolean specialConstructor = false;

    /**
     * This constructor is used to add a template name to info cribbed from a
     * ParseException generated in the parser.
     *
     * @param currentTokenVal
     * @param expectedTokenSequencesVal
     * @param tokenImageVal
     * @param templateNameVal
     */
    public TemplateParseException(final Token currentTokenVal, final int[][] expectedTokenSequencesVal,
            final String[] tokenImageVal, final String templateNameVal) {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        this.templateName = templateNameVal;
        this.specialConstructor = true;
    }

    /**
     * This constructor is used by the method "generateParseException" in the
     * generated parser. Calling this constructor generates a new object of this
     * type with the fields "currentToken", "expectedTokenSequences", and
     * "tokenImage" set. The boolean flag "specialConstructor" is also set to
     * true to indicate that this constructor was used to create this object.
     * This constructor calls its super class with the empty string to force the
     * "toString" method of parent class "Throwable" to print the error message
     * in the form: ParseException: &lt;result of getMessage&gt;
     *
     * @param currentTokenVal
     * @param expectedTokenSequencesVal
     * @param tokenImageVal
     */
    public TemplateParseException(final Token currentTokenVal, final int[][] expectedTokenSequencesVal,
            final String[] tokenImageVal) {
        super(currentTokenVal, expectedTokenSequencesVal, tokenImageVal);
        templateName = "*unset*";
        this.specialConstructor = true;
    }

    /**
     * The following constructors are for use by you for whatever purpose you
     * can think of. Constructing the exception in this manner makes the
     * exception behave in the normal way - i.e., as documented in the class
     * "Throwable". The fields "errorToken", "expectedTokenSequences", and
     * "tokenImage" do not contain relevant information. The JavaCC generated
     * code does not use these constructors.
     */
    public TemplateParseException() {
        super();
        templateName = "*unset*";
    }

    /**
     * Creates a new TemplateParseException object.
     *
     * @param message
     *            TODO: DOCUMENT ME!
     */
    public TemplateParseException(final String message) {
        super(message);
        templateName = "*unset*";
    }

    /**
     * returns the Template name where this exception occured.
     *
     * @return The Template name where this exception occured.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * returns the line number where this exception occured.
     *
     * @return The line number where this exception occured.
     */
    public int getLineNumber() {
        if (currentToken != null && currentToken.next != null) {
            return currentToken.next.beginLine;
        } else {
            return -1;
        }
    }

    /**
     * returns the column number where this exception occured.
     *
     * @return The column number where this exception occured.
     */
    public int getColumnNumber() {
        if (currentToken != null && currentToken.next != null) {
            return currentToken.next.beginColumn;
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
     * @return The error message.
     */
    @Override
    public String getMessage() {
        if (!specialConstructor) {
            final StringBuffer sb = new StringBuffer(super.getMessage());
            appendTemplateInfo(sb);
            return sb.toString();
        }

        int maxSize = 0;

        final StringBuffer expected = new StringBuffer();

        for (int i = 0; i < expectedTokenSequences.length; i++) {
            if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
            }

            for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
            }

            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
            }

            expected.append(eol).append("    ");
        }

        final StringBuffer retval = new StringBuffer("Encountered \"");
        Token tok = currentToken.next;

        for (int i = 0; i < maxSize; i++) {
            if (i != 0) {
                retval.append(' ');
            }

            if (tok.kind == 0) {
                retval.append(tokenImage[0]);
                break;
            }

            retval.append(add_escapes(tok.image));
            tok = tok.next;
        }

        retval.append("\" at ");
        appendTemplateInfo(retval);

        if (expectedTokenSequences.length == 1) {
            retval.append("Was expecting:").append(eol).append("    ");
        } else {
            retval.append("Was expecting one of:").append(eol).append("    ");
        }

        // avoid JDK 1.3 StringBuffer.append(Object o) vs 1.4
        // StringBuffer.append(StringBuffer sb) gotcha.
        retval.append(expected.toString());
        return retval.toString();
    }

    /**
     * @param sb
     */
    protected void appendTemplateInfo(final StringBuffer sb) {
        sb.append(LogUtil.formatFileString(getTemplateName(), getLineNumber(), getColumnNumber()));
        sb.append(eol);
    }
}
