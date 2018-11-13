
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
# * Using context:
#   * A context block may be used to shorten references
#     * {% jdoc %} tags and such may be surrounded by a {% jdoc_context %} ... {% endjdoc_context %} block
#     * You can open it like so:
#       * {% jdoc_context core @.properties %}
#         * The first word is the artifact id, the second is the name of e.g. a package or type
#           * The "@" symbol *in the second word* is expanded to "net.sourceforge.pmd" regardless of the enclosing context
#         * So inside the block, the context will be set to "pmd-core net.sourceforge.pmd.properties"
#         * Then the symbol "@" in a jdoc tag will be expanded to "core net.sourceforge.pmd.properties"
#         * E.g. the reference {% jdoc @.PropertyDescriptor %} is expanded to the same as {% jdoc core net.sourceforge.pmd.properties.PropertyDescriptor %}
#         * Again, you may use "!!" to show the FQCN, like {% jdoc !!@.PropertyDescriptor %}
#     * If a jdoc link occurs outside of a context block, then "@" in a javadoc tag means "core net.sourceforge.pmd", eg
#       {% jdoc @.properties.PropertyDescriptor %} works anywhere.
#     * The artifact can be overridden from the context, eg with the default context "core net.sourceforge.pmd"
#       {% jdoc java @.lang.java.JavaLanguageModule %} will be linked correctly to pmd-java, and @ will be expanded to
#       "net.sourceforge.pmd"
#     * The special package name "<<" may be used to go up once in the package tree. E.g. if the context is
#       "core @.properties", then {% jdoc @.<<.rule.Rule %} is first expanded to {% jdoc core net.sourceforge.pmd.properties.<<.Rule %},
#       then reduced to {% jdoc core net.sourceforge.pmd.Rule %}. "<<" behaves the same as ".." in file system paths.
#
# * To reference a method or field: {% jdoc @.Rule#addRuleChainVisit(java.lang.Class) %}
#   * The suffix # is followed by the name of the method or field
#   * The (erased) types of method arguments must be fully qualified. This is the same
#     convention as in javadoc {@link} tags, so you can use you're IDE's javadoc auto-
#     complete and copy-paste. The "@" can still be used to reference the context package
#     to shorten FQCNs.
#
# * To reference a package: {% jdoc_package @.properties %}, or {% jdoc-package @ %}, context works the same
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
#   - Forget to use an artifact id whenever opening a jdoc_context block
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

    if %r/^"?(\w+\s+)?((?:!\w+)*!?!)?(@(?:\.?+(?:<<|\w+))*)(#.*)?"?$/ =~ doc_ref.strip

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

  def escape_path(fqcn, ctx)

    fqcn = fqcn.gsub("@", ctx.last)

    while fqcn.include? ".<<"
      fqcn = fqcn.gsub(/\.\w+\.<</, "") # move up in the package tree
    end

    fqcn
  end

  def render(rendering_context)



    if @type_fqcn.include?("@") # Expand using the context

      doc_ctx = JDocContextBlock.get_jdoc_context(rendering_context)

      @artifact_name = @artifact_name || doc_ctx.first # if the artifact was mentioned in the tag, it takes precedence
      @type_fqcn = escape_path(@type_fqcn, doc_ctx)
      @member_suffix = escape_path(@member_suffix, doc_ctx)
    end

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


# Block used to set the javadoc context
#
# Usage:
#
# {% jdoc_context java @.lang.java.ast}
#
#   Links here in here use the context "java net.sourceforge.pmd.lang.java.ast" as context
#
# {% endjdoc_context %}
#
# Context is reset to the previous value
#
# If no arg is provided to the opening tag, context is reset to the default
#
#
class JDocContextBlock < Liquid::Block

  DEFAULT_JDOC_CONTEXT = "core net.sourceforge.pmd"
  JDOC_CONTEXT_VARNAME = "javadoc_context"


  def initialize(tag_name, arg, tokens)
    super

    @this_context = JDocContextBlock.build_ctx(arg || DEFAULT_JDOC_CONTEXT).join(" ") #just a syntax check

    @body = tokens
  end

  def render(context)

    ctx_sfg = context[JDOC_CONTEXT_VARNAME] || DEFAULT_JDOC_CONTEXT

    context[JDOC_CONTEXT_VARNAME] = @this_context

    contents = @body.render(context)

    context[JDOC_CONTEXT_VARNAME] = ctx_sfg

    contents
  end


  def self.build_ctx(ctx_str)
    # Allows to use @ as shortcut when assigning javadoc_context
    ctx_str = ctx_str.to_s.sub("@", "net.sourceforge.pmd").gsub("\"", "")
    res = ctx_str.split(" ")

    if res.length != 2
      fail "Invalid javadoc context format, you must specify artifact + package prefix in exactly two words"
    end

    unless res[0].strip.start_with?("pmd-")
      res[0] = "pmd-#{res[0].strip}"
    end

    res
  end

  def self.get_jdoc_context(context)
    build_ctx(context[JDOC_CONTEXT_VARNAME] || DEFAULT_JDOC_CONTEXT)
  end

end

Liquid::Template.register_tag('jdoc_context', JDocContextBlock)
Liquid::Template.register_tag('jdoc', JavadocTag)
Liquid::Template.register_tag('jdoc_package', JavadocTag)
Liquid::Template.register_tag('jdoc_old', JavadocTag)



