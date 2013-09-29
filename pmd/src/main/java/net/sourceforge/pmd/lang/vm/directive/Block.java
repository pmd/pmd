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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sourceforge.pmd.lang.vm.util.LogUtil;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * Directive that puts an unrendered AST block in the context
 * under the specified key, postponing rendering until the
 * reference is used and rendered.
 *
 * @author Andrew Tetlaw
 * @author Nathan Bubna
 * @author <a href="mailto:wyla@removethis.sci.fi">Jarkko Viinamaki</a>
 * @since 1.7
 * @version $Id: Block.java 686842 2008-08-18 18:29:31Z nbubna $
 */
public abstract class Block extends Directive
{
    protected Node block;
    protected Log log;
    protected int maxDepth;
    protected String key;

    /**
     * Return type of this directive.
     */
    public int getType()
    {
        return BLOCK;
    }

    /**
     *  simple init - get the key
     */
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node)
        throws TemplateInitException
    {
        super.init(rs, context, node);

        log = rs.getLog();

        /**
         * No checking is done. We just grab the last child node and assume
         * that it's the block!
         */
        block = node.jjtGetChild(node.jjtGetNumChildren() - 1);
    }

    public boolean render(InternalContextAdapter context, Writer writer)
    {
        preRender(context);
        try
        {
            return block.render(context, writer);
        }
        catch (IOException e)
        {
            String msg = "Failed to render " + id(context) + " to writer "
              + " at " + LogUtil.formatFileString(this);

            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        catch (StopCommand stop)
        {
            if (!stop.isFor(this))
            {
                throw stop;
            }
            return true;
        }
        finally
        {
            postRender(context);
        }
    }

    /**
     * Creates a string identifying the source and location of the block
     * definition, and the current template being rendered if that is
     * different.
     */
    protected String id(InternalContextAdapter context)
    {
        StrBuilder str = new StrBuilder(100)
            .append("block $").append(key);
        if (!context.getCurrentTemplateName().equals(getTemplateName()))
        {
            str.append(" used in ").append(context.getCurrentTemplateName());
        }
        return str.toString();
    }
    
    /**
     * actual class placed in the context, holds the context
     * being used for the render, as well as the parent (which already holds
     * everything else we need).
     */
    public static class Reference implements Renderable
    {
        private InternalContextAdapter context;
        private Block parent;
        private int depth;
        
        public Reference(InternalContextAdapter context, Block parent)
        {
            this.context = context;
            this.parent = parent;
        }
        
        /**
         * Render the AST of this block into the writer using the context.
         */
        public boolean render(InternalContextAdapter context, Writer writer)
        {
            depth++;
            if (depth > parent.maxDepth)
            {
                /* this is only a debug message, as recursion can
                 * happen in quasi-innocent situations and is relatively
                 * harmless due to how we handle it here.
                 * this is more to help anyone nuts enough to intentionally
                 * use recursive block definitions and having problems
                 * pulling it off properly.
                 */
                parent.log.debug("Max recursion depth reached for " + parent.id(context)
                    + " at " + LogUtil.formatFileString(parent));
                depth--;
                return false;
            }
            else
            {
                parent.render(context, writer);
                depth--;
                return true;
            }
        }

        public String toString()
        {
            Writer writer = new StringWriter();
            if (render(context, writer))
            {
                return writer.toString();
            }
            return null;
        }
    }
}
