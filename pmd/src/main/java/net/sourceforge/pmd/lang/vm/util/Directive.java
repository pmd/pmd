package net.sourceforge.pmd.lang.vm.util;

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

import org.apache.velocity.runtime.RuntimeServices;

import org.apache.velocity.runtime.directive.DirectiveConstants;

/**
 * Base class for all directives used in Velocity.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: Directive.java 724825 2008-12-09 18:56:06Z nbubna $
 */
public abstract class Directive implements DirectiveConstants, Cloneable {
    private int line = 0;

    private int column = 0;

    private String templateName;

    /**
     *
     */
    protected RuntimeServices rsvc = null;

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
    public void setLocation(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Allows the template location to be set.
     * 
     * @param line
     * @param column
     */
    public void setLocation(final int line, final int column, final String templateName) {
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
     * @return The template file name this directive was defined in, or null if not defined in a file.
     */
    public String getTemplateName() {
        return templateName;
    }

}
