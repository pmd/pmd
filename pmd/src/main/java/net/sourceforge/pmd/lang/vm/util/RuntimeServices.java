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

import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vm.ast.ParseException;
import net.sourceforge.pmd.lang.vm.ast.SimpleNode;
import net.sourceforge.pmd.lang.vm.ast.VmParser;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.Uberspect;

/**
 * Interface for internal runtime services that are needed by the various components w/in Velocity. This was taken from
 * the old Runtime singleton, and anything not necessary was removed.
 * 
 * Currently implemented by RuntimeInstance.
 * 
 * @author <a href="mailto:geirm@optonline.net">Geir Magusson Jr.</a>
 * @version $Id: RuntimeServices.java 898050 2010-01-11 20:15:31Z nbubna $
 */
public interface RuntimeServices {

    /**
     * This is the primary initialization method in the Velocity Runtime. The systems that are setup/initialized here
     * are as follows:
     * 
     * <ul>
     * <li>Logging System</li>
     * <li>ResourceManager</li>
     * <li>Parser Pool</li>
     * <li>Global Cache</li>
     * <li>Static Content Include System</li>
     * <li>Velocimacro System</li>
     * </ul>
     */
    public void init();

    /**
     * Allows an external system to set a property in the Velocity Runtime.
     * 
     * @param key property key
     * @param value property value
     */
    public void setProperty(String key, Object value);

    /**
     * Allow an external system to set an ExtendedProperties object to use. This is useful where the external system
     * also uses the ExtendedProperties class and the velocity configuration is a subset of parent application's
     * configuration. This is the case with Turbine.
     * 
     * @param configuration
     */
    public void setConfiguration(ExtendedProperties configuration);

    /**
     * Add a property to the configuration. If it already exists then the value stated here will be added to the
     * configuration entry. For example, if
     * 
     * resource.loader = file
     * 
     * is already present in the configuration and you
     * 
     * addProperty("resource.loader", "classpath")
     * 
     * Then you will end up with a Vector like the following:
     * 
     * ["file", "classpath"]
     * 
     * @param key
     * @param value
     */
    public void addProperty(String key, Object value);

    /**
     * Clear the values pertaining to a particular property.
     * 
     * @param key of property to clear
     */
    public void clearProperty(String key);

    /**
     * Allows an external caller to get a property. The calling routine is required to know the type, as this routine
     * will return an Object, as that is what properties can be.
     * 
     * @param key property to return
     * @return The value.
     */
    public Object getProperty(String key);

    /**
     * Initialize the Velocity Runtime with a Properties object.
     * 
     * @param p
     */
    public void init(Properties p);

    /**
     * Initialize the Velocity Runtime with the name of ExtendedProperties object.
     * 
     * @param configurationFile
     */
    public void init(String configurationFile);

    /**
     * Wraps the String in a StringReader and passes it off to {@link #parse(Reader,String)}.
     * 
     * @since 1.6
     */
    public SimpleNode parse(String string, String templateName) throws ParseException;

    /**
     * Parse the input and return the root of AST node structure. <br>
     * <br>
     * In the event that it runs out of parsers in the pool, it will create and let them be GC'd dynamically, logging
     * that it has to do that. This is considered an exceptional condition. It is expected that the user will set the
     * PARSER_POOL_SIZE property appropriately for their application. We will revisit this.
     * 
     * @param reader inputstream retrieved by a resource loader
     * @param templateName name of the template being parsed
     * @return The AST representing the template.
     * @throws ParseException
     */
    public SimpleNode parse(Reader reader, String templateName) throws ParseException;

    /**
     * Parse the input and return the root of the AST node structure.
     * 
     * @param reader inputstream retrieved by a resource loader
     * @param templateName name of the template being parsed
     * @param dumpNamespace flag to dump the Velocimacro namespace for this template
     * @return The AST representing the template.
     * @throws ParseException
     */
    public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException;

    /**
     * Renders the input string using the context into the output writer. To be used when a template is dynamically
     * constructed, or want to use Velocity as a token replacer.
     * 
     * @param context context to use in rendering input string
     * @param out Writer in which to render the output
     * @param logTag string to be used as the template name for log messages in case of error
     * @param instring input string containing the VTL to be rendered
     * 
     * @return true if successful, false otherwise. If false, see Velocity runtime log
     * @throws ParseErrorException The template could not be parsed.
     * @throws MethodInvocationException A method on a context object could not be invoked.
     * @throws ResourceNotFoundException A referenced resource could not be loaded.
     * @throws IOException While rendering to the writer, an I/O problem occured.
     * @since Velocity 1.6
     */
    public boolean evaluate(Context context, Writer out, String logTag, String instring);

    /**
     * Renders the input reader using the context into the output writer. To be used when a template is dynamically
     * constructed, or want to use Velocity as a token replacer.
     * 
     * @param context context to use in rendering input string
     * @param writer Writer in which to render the output
     * @param logTag string to be used as the template name for log messages in case of error
     * @param reader Reader containing the VTL to be rendered
     * 
     * @return true if successful, false otherwise. If false, see Velocity runtime log
     * @throws ParseErrorException The template could not be parsed.
     * @throws MethodInvocationException A method on a context object could not be invoked.
     * @throws ResourceNotFoundException A referenced resource could not be loaded.
     * @since Velocity 1.6
     */
    public boolean evaluate(Context context, Writer writer, String logTag, Reader reader);

    /**
     * Invokes a currently registered Velocimacro with the params provided and places the rendered stream into the
     * writer. <br>
     * Note : currently only accepts args to the VM if they are in the context.
     * 
     * @param vmName name of Velocimacro to call
     * @param logTag string to be used for template name in case of error. if null, the vmName will be used
     * @param params keys for args used to invoke Velocimacro, in java format rather than VTL (eg "foo" or "bar" rather
     *            than "$foo" or "$bar")
     * @param context Context object containing data/objects used for rendering.
     * @param writer Writer for output stream
     * @return true if Velocimacro exists and successfully invoked, false otherwise.
     * @since 1.6
     */
    public boolean invokeVelocimacro(final String vmName, String logTag, String[] params, final Context context,
            final Writer writer);

    /**
     * Returns a <code>Template</code> from the resource manager. This method assumes that the character encoding of the
     * template is set by the <code>input.encoding</code> property. The default is "ISO-8859-1"
     * 
     * @param name The file name of the desired template.
     * @return The template.
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException if template cannot be parsed due to syntax (or other) error.
     */
    public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException;

    /**
     * Returns a <code>Template</code> from the resource manager
     * 
     * @param name The name of the desired template.
     * @param encoding Character encoding of the template
     * @return The template.
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException if template cannot be parsed due to syntax (or other) error.
     */
    public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException;

    /**
     * Returns a static content resource from the resource manager. Uses the current value if INPUT_ENCODING as the
     * character encoding.
     * 
     * @param name Name of content resource to get
     * @return parsed ContentResource object ready for use
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException
     */
    public ContentResource getContent(String name) throws ResourceNotFoundException, ParseErrorException;

    /**
     * Returns a static content resource from the resource manager.
     * 
     * @param name Name of content resource to get
     * @param encoding Character encoding to use
     * @return parsed ContentResource object ready for use
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException
     */
    public ContentResource getContent(String name, String encoding) throws ResourceNotFoundException,
            ParseErrorException;

    /**
     * Determines is a template exists, and returns name of the loader that provides it. This is a slightly less hokey
     * way to support the Velocity.templateExists() utility method, which was broken when per-template encoding was
     * introduced. We can revisit this.
     * 
     * @param resourceName Name of template or content resource
     * @return class name of loader than can provide it
     */
    public String getLoaderNameForResource(String resourceName);

    /**
     * String property accessor method with default to hide the configuration implementation.
     * 
     * @param key property key
     * @param defaultValue default value to return if key not found in resource manager.
     * @return String value of key or default
     */
    public String getString(String key, String defaultValue);

    /**
     * Returns the appropriate VelocimacroProxy object if strVMname is a valid current Velocimacro.
     * 
     * @param vmName Name of velocimacro requested
     * @param templateName Name of the namespace.
     * @return VelocimacroProxy
     */
    public Directive getVelocimacro(String vmName, String templateName);

    /**
     * Returns the appropriate VelocimacroProxy object if strVMname is a valid current Velocimacro.
     * 
     * @param vmName Name of velocimacro requested
     * @param templateName Name of the namespace.
     * @param renderingTemplate Name of the template we are currently rendering. This information is needed when
     *            VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL setting is true and template contains a macro with the same name
     *            as the global macro library.
     * 
     * @since Velocity 1.6
     * 
     * @return VelocimacroProxy
     */
    public Directive getVelocimacro(String vmName, String templateName, String renderingTemplate);

    /**
     * Adds a new Velocimacro. Usually called by Macro only while parsing.
     * 
     * @param name Name of velocimacro
     * @param macro String form of macro body
     * @param argArray Array of strings, containing the #macro() arguments. the 0th is the name.
     * @param sourceTemplate
     * 
     * @deprecated Use addVelocimacro(String, Node, String[], String) instead
     * 
     * @return boolean True if added, false if rejected for some reason (either parameters or permission settings)
     */
    @Deprecated
    public boolean addVelocimacro(String name, String macro, String argArray[], String sourceTemplate);

    /**
     * Adds a new Velocimacro. Usually called by Macro only while parsing.
     * 
     * @param name Name of velocimacro
     * @param macro root AST node of the parsed macro
     * @param argArray Array of strings, containing the #macro() arguments. the 0th is the name.
     * @param sourceTemplate
     * 
     * @since Velocity 1.6
     * 
     * @return boolean True if added, false if rejected for some reason (either parameters or permission settings)
     */
    public boolean addVelocimacro(String name, Node macro, String argArray[], String sourceTemplate);

    /**
     * Checks to see if a VM exists
     * 
     * @param vmName Name of velocimacro
     * @param templateName
     * @return boolean True if VM by that name exists, false if not
     */
    public boolean isVelocimacro(String vmName, String templateName);

    /**
     * tells the vmFactory to dump the specified namespace. This is to support clearing the VM list when in
     * inline-VM-local-scope mode
     * 
     * @param namespace
     * @return True if the Namespace was dumped.
     */
    public boolean dumpVMNamespace(String namespace);

    /**
     * String property accessor method to hide the configuration implementation
     * 
     * @param key property key
     * @return value of key or null
     */
    public String getString(String key);

    /**
     * Int property accessor method to hide the configuration implementation.
     * 
     * @param key property key
     * @return int value
     */
    public int getInt(String key);

    /**
     * Int property accessor method to hide the configuration implementation.
     * 
     * @param key property key
     * @param defaultValue default value
     * @return int value
     */
    public int getInt(String key, int defaultValue);

    /**
     * Boolean property accessor method to hide the configuration implementation.
     * 
     * @param key property key
     * @param def default default value if property not found
     * @return boolean value of key or default value
     */
    public boolean getBoolean(String key, boolean def);

    /**
     * Return the velocity runtime configuration object.
     * 
     * @return ExtendedProperties configuration object which houses the velocity runtime properties.
     */
    public ExtendedProperties getConfiguration();

    /**
     * Return the specified application attribute.
     * 
     * @param key The name of the attribute to retrieve.
     * @return The value of the attribute.
     */
    public Object getApplicationAttribute(Object key);

    /**
     * Set the specified application attribute.
     * 
     * @param key The name of the attribute to set.
     * @param value The attribute value to set.
     * @return the displaced attribute value
     */
    public Object setApplicationAttribute(Object key, Object value);

    /**
     * Returns the configured class introspection/reflection implementation.
     * 
     * @return The current Uberspect object.
     */
    public Uberspect getUberspect();

    /**
     * Returns a convenient Log instance that wraps the current LogChute.
     * 
     * @return A log object.
     */
    public Log getLog();

    /**
     * Returns the event handlers for the application.
     * 
     * @return The event handlers for the application.
     */
    public EventCartridge getApplicationEventCartridge();

    /**
     * Returns the configured method introspection/reflection implementation.
     * 
     * @return The configured method introspection/reflection implementation.
     */
    public Introspector getIntrospector();

    /**
     * Returns true if the RuntimeInstance has been successfully initialized.
     * 
     * @return True if the RuntimeInstance has been successfully initialized.
     */
    public boolean isInitialized();

    /**
     * Create a new parser instance.
     * 
     * @return A new parser instance.
     */
    public VmParser createNewParser();

    /**
     * Retrieve a previously instantiated directive.
     * 
     * @param name name of the directive
     * @return the directive with that name, if any
     * @since 1.6
     */
    public Directive getDirective(String name);

}
