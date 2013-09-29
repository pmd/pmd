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

import java.io.Writer;

import net.sourceforge.pmd.lang.vm.util.LogUtil;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * Directive that puts an unrendered AST block in the context
 * under the specified key, postponing rendering until the
 * reference is used and rendered.
 *
 * @author Andrew Tetlaw
 * @author Nathan Bubna
 * @version $Id: Define.java 686842 2008-08-18 18:29:31Z nbubna $
 */
public class Define extends Block
{
    /**
     * Return name of this directive.
     */
    public String getName()
    {
        return "define";
    }

    /**
     *  simple init - get the key
     */
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node)
        throws TemplateInitException
    {
        super.init(rs, context, node);

        // the first child is the block name (key), the second child is the block AST body
        if ( node.jjtGetNumChildren() != 2 )
        {
            throw new VelocityException("parameter missing: block name at "
                 + LogUtil.formatFileString(this));
        }
        
        /*
         * first token is the name of the block. We don't even check the format,
         * just assume it looks like this: $block_name. Should we check if it has
         * a '$' or not?
         */
        key = node.jjtGetChild(0).getFirstToken().image.substring(1);

        /*
         * default max depth of two is used because intentional recursion is
         * unlikely and discouraged, so make unintentional ones end fast
         */
        maxDepth = rs.getInt(RuntimeConstants.DEFINE_DIRECTIVE_MAXDEPTH, 2);
    }

    /**
     * directive.render() simply makes an instance of the Block inner class
     * and places it into the context as indicated.
     */
    public boolean render(InternalContextAdapter context, Writer writer, Node node)
    {
        /* put a Block.Reference instance into the context,
         * using the user-defined key, for later inline rendering.
         */
        context.put(key, new Reference(context, this));
        return true;
    }

}
