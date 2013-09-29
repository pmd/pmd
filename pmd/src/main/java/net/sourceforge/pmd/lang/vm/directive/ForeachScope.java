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
 * This represents scoping and metadata for #foreach,
 * adding index, count, hasNext, isFirst and isLast info.
 *
 * @author Nathan Bubna
 * @version $Id$
 */
public class ForeachScope extends Scope
{
    protected int index = -1;
    protected boolean hasNext = false;

    public ForeachScope(Object owner, Object replaces)
    {
        super(owner, replaces);
    }

    public int getIndex()
    {
        return index;
    }

    public int getCount()
    {
        return index + 1;
    }

    public boolean hasNext()
    {
        return getHasNext();
    }

    public boolean getHasNext()
    {
        return hasNext;
    }

    public boolean isFirst()
    {
        return index < 1;
    }

    public boolean getFirst()
    {
        return isFirst();
    }

    public boolean isLast()
    {
        return !hasNext;
    }

    public boolean getLast()
    {
        return isLast();
    }

}
