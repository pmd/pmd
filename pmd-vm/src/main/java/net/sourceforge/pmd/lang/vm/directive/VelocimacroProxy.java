
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
 * VelocimacroProxy.java
 *
 * a proxy Directive-derived object to fit with the current directive system
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: VelocimacroProxy.java 898032 2010-01-11 19:51:03Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class VelocimacroProxy extends Directive {
    private String macroName;
    private int numMacroArgs = 0;

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

    /**
     * sets the directive name of this VM
     *
     * @param name
     */
    public void setName(String name) {
        macroName = name;
    }

    /**
     * sets the array of arguments specified in the macro definition
     *
     * @param arr
     */
    public void setArgArray(String[] arr) {
        /*
         * get the arg count from the arg array. remember that the arg array has
         * the macro name as it's 0th element
         */
        numMacroArgs = arr.length - 1;
    }

    /**
     * returns the number of ars needed for this VM
     *
     * @return The number of ars needed for this VM
     */
    public int getNumArgs() {
        return numMacroArgs;
    }

}
