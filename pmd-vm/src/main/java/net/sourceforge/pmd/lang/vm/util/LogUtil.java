
package net.sourceforge.pmd.lang.vm.util;

import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.directive.Directive;

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

/**
 * Convenient wrapper for LogChute functions. This implements the RuntimeLogger
 * methods (and then some). It is hoped that use of this will fully replace use
 * of the RuntimeLogger.
 *
 * @author <a href="mailto:nbubna@apache.org">Nathan Bubna</a>
 * @version $Id: Log.java 724825 2008-12-09 18:56:06Z nbubna $
 * @since 1.5
 */
public final class LogUtil {
    private LogUtil() { }

    /**
     * Creates a string that formats the template filename with line number and
     * column of the given Directive. We use this routine to provide a cosistent
     * format for displaying file errors.
     */
    public static String formatFileString(final Directive directive) {
        return formatFileString(directive.getTemplateName(), directive.getLine(), directive.getColumn());
    }

    /**
     * Creates a string that formats the template filename with line number and
     * column of the given Node. We use this routine to provide a cosistent
     * format for displaying file errors.
     */
    public static String formatFileString(final AbstractVmNode node) {
        return formatFileString(node.getTemplateName(), node.getLine(), node.getColumn());
    }

    /**
     * Simply creates a string that formats the template filename with line
     * number and column. We use this routine to provide a cosistent format for
     * displaying file errors.
     *
     * @param template
     *            File name of template, can be null
     * @param linenum
     *            Line number within the file
     * @param colnum
     *            Column number withing the file at linenum
     */
    public static String formatFileString(String template, final int linenum, final int colnum) {
        if (template == null || "".equals(template)) {
            template = "<unknown template>";
        }
        return template + "[line " + linenum + ", column " + colnum + "]";
    }
}
