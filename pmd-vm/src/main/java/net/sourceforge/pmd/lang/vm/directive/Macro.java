
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
 * Macro implements the macro definition directive of VTL.
 *
 * example :
 *
 * #macro( isnull $i ) #if( $i ) $i #end #end
 *
 * This object is used at parse time to mainly process and register the macro.
 * It is used inline in the parser when processing a directive.
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: Macro.java 746438 2009-02-21 05:41:24Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Macro extends Directive {
    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "macro";
    }

    /**
     * Return type of this directive.
     *
     * @return The type of this directive.
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    /**
     * Since this class does no processing of content, there is never a need for
     * an internal scope.
     */
    @Override
    public boolean isScopeProvided() {
        return false;
    }
}
