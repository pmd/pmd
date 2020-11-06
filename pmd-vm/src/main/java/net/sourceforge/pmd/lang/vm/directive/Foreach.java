
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
 * Foreach directive used for moving through arrays, or objects that provide an
 * Iterator.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author Daniel Rall
 * @version $Id: Foreach.java 945927 2010-05-18 22:21:41Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Foreach extends Directive {
    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "foreach";
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

}
