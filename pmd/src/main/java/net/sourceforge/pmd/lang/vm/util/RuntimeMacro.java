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

/**
 * This class acts as a proxy for potential macros. When the AST is built this class is inserted as a placeholder for
 * the macro (whether or not the macro is actually defined). At render time we check whether there is a implementation
 * for the macro call. If an implementation cannot be found the literal text is rendered.
 * 
 * @since 1.6
 */
public class RuntimeMacro extends Directive {
    /**
     * Name of the macro
     */
    private final String macroName;

    /**
     * source template name
     */
    private final String sourceTemplate;

    /**
     * Indicates if we are running in strict reference mode.
     */
    protected boolean strictRef = false;

    /**
     * Create a RuntimeMacro instance. Macro name and source template stored for later use.
     * 
     * @param macroName name of the macro
     * @param sourceTemplate template where macro call is made
     */
    public RuntimeMacro(final String macroName, final String sourceTemplate) {
        if (macroName == null || sourceTemplate == null) {
            throw new IllegalArgumentException("Null arguments");
        }

        this.macroName = macroName;
        this.sourceTemplate = sourceTemplate;
    }

    /**
     * Return name of this Velocimacro.
     * 
     * @return The name of this Velocimacro.
     */
    @Override
    public String getName() {
        return macroName;
    }

    /**
     * Velocimacros are always LINE type directives.
     * 
     * @return The type of this directive.
     */
    @Override
    public int getType() {
        return LINE;
    }

}
