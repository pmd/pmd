
package net.sourceforge.pmd.lang.vm.directive;

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
 * Base class for all directives used in Velocity.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author Nathan Bubna
 * @version $Id: Directive.java 778045 2009-05-23 22:17:46Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public abstract class Directive implements Cloneable {
    /** Block directive indicator */
    public static final int BLOCK = 1;

    /** Line directive indicator */
    public static final int LINE = 2;

    private int line = 0;
    private int column = 0;
    private boolean provideScope = false;
    private String templateName;

    /**
     * Return the name of this directive.
     *
     * @return The name of this directive.
     */
    public abstract String getName();

    /**
     * Get the directive type BLOCK/LINE.
     *
     * @return The directive type BLOCK/LINE.
     */
    public abstract int getType();

    /**
     * Allows the template location to be set.
     *
     * @param line
     * @param column
     */
    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Allows the template location to be set.
     *
     * @param line
     * @param column
     */
    public void setLocation(int line, int column, String templateName) {
        setLocation(line, column);
        this.templateName = templateName;
    }

    /**
     * for log msg purposes
     *
     * @return The current line for log msg purposes.
     */
    public int getLine() {
        return line;
    }

    /**
     * for log msg purposes
     *
     * @return The current column for log msg purposes.
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return The template file name this directive was defined in, or null if
     *         not defined in a file.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @return the name to be used when a scope control is provided for this
     *          directive.
     */
    public String getScopeName() {
        return getName();
    }

    /**
     * @return true if there will be a scope control injected into the context
     *         when rendering this directive.
     */
    public boolean isScopeProvided() {
        return provideScope;
    }

}
