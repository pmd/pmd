
# Tags to reference a javadoc page or package summary.
#
# Provides several tags, which should not be mixed up:
# * The tag "jdoc" is used for a type or member reference
# * The tag "jdoc_package" is used for a package summary reference
#
# Both of these link to the latest version of the API so when editing the site,
# since it refers to an unpublished API version, the links don't work. When releasing
# though, links on the published jekyll site will work.
# To refer to the previous API version, e.g. to refer to a type or member that was removed,
# the tag "jdoc_old" may be used. This tag is used exactly the same way as "jdoc". There's
# no "jdoc_package_old" tag.
#
#
# Usage (don't miss the DO NOT section at the bottom):
#
# * Simple type reference: {% jdoc core net.sourceforge.pmd.properties.PropertyDescriptor %}
#   * First arg must be the name of the artifact, without the "pmd-" prefix, eg  "core" will be expanded to "pmd-core"
#   * After a space, the fqcn of the type to reference is mentioned
#   * This will be expanded to [`PropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/latest/net/sourceforge/pmd/properties/PropertyDescriptor.html)
#     * Only the simple name of the type is visible by default
#     * Prefixing the reference with a double bang ("!!") displays the FQCN instead of the simple name
#       * E.g. {% jdoc core !!net.sourceforge.pmd.properties.PropertyDescriptor %} -> [`net.sourceforge.pmd.properties.PropertyDescriptor`](...),
#
# * Using context handles
#   * FQCNs are tedious to write and read so you can define shortcuts to a package or type name relevant
#     to what you're documenting to save some keystrokes.
#   * I call these shortcuts "context handles", they consist of an artifact id and a package or type name
#   * The "jdoc_handle" tag is used to declare a handle. E.g.
#     {% jdoc_handle @{coreast} core @.lang.ast %}
#     {% jdoc_handle @{jast} java @.lang.java.ast}
#     * The first argument is the handle as it will be referenced. It must have the form @{handle_name}
#     * The second argument is the artifact id of the package.
#     * The third argument is the package name, within which "@" is expanded to "net.sourceforge.pmd". Other handles
#       may not be used within it.
#   * After a handle is declared, it's used with the @{name} syntax, eg
#     {% jdoc @{jast}.ASTType %}, or {% jdoc @{coreast}.Node %}
#     The artifact id of the handle used for the type reference is used implicitly,
#     and the handle reference is expanded to the package name.
#   * "@" not followed by braces (not a handle) is expanded to "net.sourceforge.pmd", so
#     {% jdoc core !!@.properties.PropertyDescriptor %} is the same as {% jdoc core !!net.sourceforge.pmd.properties.PropertyDescriptor %}
#     Note that using "@" doesn't provide an implicit artifact id.
#   * Handles can be used in method arguments but their artifact id is ignored. E.g.
#     {% jdoc @.lang.java.ast.JavaNode.#(@{coreast}.Node) %} is invalid because neither
#     * the artifact id was on the jdoc tag, nor did
#     * the reference to JavaNode use a context handle that would have provided
#       an implicit artifact id
#
# * To reference a method or field: {% jdoc @.Rule#addRuleChainVisit(java.lang.Class) %}
#   * The suffix # is followed by the name of the method or field
#   * The (erased) types of method arguments must be fully qualified. This is the same
#     convention as in javadoc {@link} tags, so you can use you're IDE's javadoc auto-
#     complete and copy-paste. "@" and context handles can still be used to shorten FQCNs.
#
# * To reference a package: eg {% jdoc_package @{foo}.properties %}, {% jdoc-package @ %}
#
# * Bang options:
#   * Rendering may be customized by prefixing the reference to the linked member or type with some options.
#   * Option syntax is "!name", and the options, if any, must be separated from the reference by another bang ("!")
#   * Available options:
#     * No options -> just the member name:
#         * {% jdoc @.Rule %} -> [`Rule`](...)
#         * {% jdoc @.Rule#setName(java.lang.String) %} -> [`setName`](...)
#         * {% jdoc @.AbstractRule#children %} -> [`children`](...)
#     * args -> adds the simple name of the argument types for method references, noop for other references
#         *  {% jdoc !args!@.Rule#setName(java.lang.String) %} -> [`setName(String)`](...)
#     * qualify -> prefix with the fqcn of the class, noop for package references
#         *  {% jdoc !qualify!@.Rule %} -> [`net.sourceforge.pmd.Rule`](...)
#         *  {% jdoc !qualify!@.Rule#setName(java.lang.String) %} -> [`net.sourceforge.pmd.Rule#setName`](...)
#     * class -> prefix the class name for member references, noop for type and package references, or if "qualify" is specified
#         *  {% jdoc !class!@.Rule#setName(java.lang.String) %} -> [`Rule#setName`](...)
#
# * Double-bang shorthands:
#     * For field or method references, "!!" is the "class" option
#       * {% jdoc !!@.Rule#setName(java.lang.String) %} -> [`Rule#setName`](...)
#     * For type references, "!!" is the "qualify" option
#       *  {% jdoc !!@.Rule %} -> [`net.sourceforge.pmd.Rule`](...)
#     * For package references, "!!" is a noop, they're always fully qualified
#   * Options may be concatenated:
#     * {% jdoc !args!!@.Rule#setName(java.lang.String) %} -> [`Rule#setName(String)`](...)
#     * {% jdoc !args!qualify!@.Rule#setName(java.lang.String) %} -> [`net.sourceforge.pmd.Rule#setName(String)`](...)
#
#
# * DO NOT:
#   - Include spaces between dots, or anywhere except between the artifact reference and the page reference
#   - Use double or single quotes around the arguments
#   - Use the "#" suffix to reference a nested type, instead, use a dot "." and reference it like a normal type name
#
class JavadocTag < Liquid::Tag

  FQCN_OPTION = "qualify"
  ARGS_OPTION = "args"
  CLASS_OPTION = "class"
  DOUBLE_BANG_OPTION = "shorthand"

  def initialize(tag_name, doc_ref, tokens)
    super

    options_str = ""

    if %r/^"?(\w+\s+)?((?:!\w+)*!?!)?(@(?:\{\w+\})?(?:\.?\w+)*)(#.*)?"?$/ =~ doc_ref.strip

      @artifact_name = $1 && ("pmd-" + $1.strip) # is nil if not mentioned
      options_str = $2 || ""
      @display_options = [] # empty
      @type_fqcn = $3 # may be just "@"
      @member_suffix = $4 || "" # default to empty string instead of nil

    else
      fail "Invalid javadoc reference format, see doc on javadoc_tag.rb"
    end

    if options_str.end_with?("!!")
      @display_options.push(DOUBLE_BANG_OPTION)
    end

    @display_options += options_str.split("!").compact.reject {|s| s.empty?} # filter out empty

    if tag_name == "jdoc_package"
      @is_package_ref = true
      @display_options.push("qualify")
    elsif tag_name == "jdoc_old"
      @use_previous_api_version = true
    end

    @display_options.each do |opt|
      if opt != FQCN_OPTION && opt != ARGS_OPTION && opt != CLASS_OPTION && opt != DOUBLE_BANG_OPTION
        fail "Unknown display option '#{opt}'"
      end
    end
  end

  def get_visible_name

    fqcn_regex =


    # method or field
    if @member_suffix && /#(\w+)(\((\s*[\w.]+(?:\[\])*(?:,\s*[\w.]+(?:\[\])*)*)\s*\))?/ =~ @member_suffix


      suffix = $1 # method or field name

      if @display_options.include?(ARGS_OPTION) && $2 && !$2.empty? # is method


        args = ($3 || "").split(",").map {|a| a.gsub(/\w+\./, "").strip} # map to simple names

        suffix = "#{suffix}(#{args.join(", ")})"
      end

      visible_name = if @display_options.include?(FQCN_OPTION)
                       @type_fqcn + "#" + suffix
                     elsif @display_options.include?(CLASS_OPTION) || @display_options.include?(DOUBLE_BANG_OPTION)
                       @type_fqcn.split("\.").last + "#" + suffix # type simple name
                     else
                       suffix
                     end

      return visible_name
    end

    # else package or type, for packages the FQCN_OPTION is present

    if @display_options.include? FQCN_OPTION || @display_options.include?(DOUBLE_BANG_OPTION)
      @type_fqcn
    else
      @type_fqcn.split("\.").last
    end
  end

  def render(rendering_context)

    doc_ctx, @type_fqcn = JDocContextDeclaration::escape_path(@type_fqcn, rendering_context)
    @artifact_name = @artifact_name || doc_ctx.first # if the artifact was mentioned in the tag, it takes precedence
    _, @member_suffix = JDocContextDeclaration::escape_path(@member_suffix, rendering_context)

    visible_name = get_visible_name

    # Hack to reference the package summary
    # Has to be done after finding the visible_name
    if @is_package_ref
      @type_fqcn = @type_fqcn + ".package-summary"
    end


    # Always hardcode the artifact version instead of using "latest"
    api_version =
        if @use_previous_api_version
        then
          rendering_context["site.pmd.previous_version"]
        else
          rendering_context["site.pmd.version"]
        end


    markup_link(visible_name, doclink(@artifact_name, api_version, @type_fqcn, @member_suffix))
  end

  private

  def doclink(artifact, api_version, type_name, member_suffix)
    "https://javadoc.io/page/net.sourceforge.pmd/#{artifact}/#{api_version}/#{type_name.gsub("\.", "/")}.html#{member_suffix.gsub(/\s+/, "")}"
  end

  def markup_link(rname, link)
    "[`#{rname}`](#{link})"
  end

end


# Tag used to declare a javadoc handle to shorten javadoc references.
#
# Usage:
# {% jdoc_handle @{coreast} core @.lang.ast %}
# {% jdoc_handle @{jast} java @.lang.java.ast}
#
# * The first argument is the handle as it will be referenced. It must have the form @{handle_name}
# * The second argument is the artifact id of the package.
# * The third argument is the package name, within which "@" means net.sourceforge.pmd. Other handles
#   may not be used within it.
#
# After those tags have been declared, the handle is used with the @{name} syntax, eg {% jdoc @{jast}.ASTType %}
#
class JDocContextDeclaration < Liquid::Tag

  DEFAULT_JDOC_CONTEXT = ["pmd-core", "net.sourceforge.pmd"]
  JDOC_CONTEXT_NAMESPACE = "jdoc_context"


  def initialize(tag_name, arg, tokens)
    super

    all_args = arg.split(" ")

    @ctx_name = JDocContextDeclaration::get_handle_name(all_args.shift)

    @this_context = JDocContextDeclaration::validate_ctx(all_args || DEFAULT_JDOC_CONTEXT)

  end

  def render(ctx)

    unless ctx[JDOC_CONTEXT_NAMESPACE]
      ctx[JDOC_CONTEXT_NAMESPACE] = {} #empty map
    end

    ctx[JDOC_CONTEXT_NAMESPACE][@ctx_name] = @this_context

    ""
  end


  def self.validate_ctx(ctx_arr)

    unless ctx_arr && ctx_arr.compact.length == 2
      fail "Invalid javadoc context format, you must specify artifact + package prefix in exactly two words"
    end

    # Allows to use @ as shortcut when assigning javadoc_context
    ctx_arr[1].sub!("@", "net.sourceforge.pmd").gsub!("\"", "")


    unless ctx_arr[0].strip.start_with?("pmd-")
      ctx_arr[0] = "pmd-#{ctx_arr[0].strip}"
    end

    ctx_arr
  end


  # gets the expansion of a full handle, given as @{name}
  def self.get_context(at_prefixed_name, ctx)

    name = get_handle_name(at_prefixed_name)

    unless ctx[JDOC_CONTEXT_NAMESPACE] && ctx[JDOC_CONTEXT_NAMESPACE][name]
      fail "Undeclared javadoc context handle #{at_prefixed_name}"
    end

    ctx[JDOC_CONTEXT_NAMESPACE][name] # return the variable
  end

  def self.get_handle_name(at_prefixed_name)
    if /@\{(\w+)\}/ =~ at_prefixed_name
      $1
    else
      fail "Invalid format for javadoc context handle, expected @{name}, was #{at_prefixed_name}"
    end
  end


  def self.escape_path(fqcn, rendering_ctx)

    unless fqcn
      return [DEFAULT_JDOC_CONTEXT, nil]
    end

    doc_ctx = DEFAULT_JDOC_CONTEXT

    fqcn = fqcn.gsub(/@(\{(\w+)\})?/) do |h|
      if $1
        doc_ctx = get_context(h, rendering_ctx)
      else
        doc_ctx = DEFAULT_JDOC_CONTEXT
      end
      doc_ctx[1]
    end

    # return the last found context
    # For the type fqcn there's only one so it's ok
    # For argument types there may be several but they're ignored so it's ok
    [doc_ctx, fqcn]
  end

end


Liquid::Template.register_tag('jdoc_context', JDocContextBlock)
Liquid::Template.register_tag('jdoc', JavadocTag)
Liquid::Template.register_tag('jdoc_package', JavadocTag)
Liquid::Template.register_tag('jdoc_old', JavadocTag)
Liquid::Template.register_tag('jdoc_handle', JDocContextDeclaration)



