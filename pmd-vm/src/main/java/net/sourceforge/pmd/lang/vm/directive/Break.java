
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
 * Break directive used for interrupting scopes.
 *
 * @author <a href="mailto:wyla@removethis.sci.fi">Jarkko Viinamaki</a>
 * @author Nathan Bubna
 * @version $Id$
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Break extends Directive {

    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "break";
    }

    /**
     * Return type of this directive.
     *
     * @return The type of this directive.
     */
    @Override
    public int getType() {
        return LINE;
    }

    /**
     * Since there is no processing of content, there is never a need for an
     * internal scope.
     */
    @Override
    public boolean isScopeProvided() {
        return false;
    }

}
