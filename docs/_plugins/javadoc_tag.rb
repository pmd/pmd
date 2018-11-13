# Tags to reference a javadoc page or package summary.
#
# Provides several tags, which should not be mixed up:
# * The tag "jdoc" is used for a type or member reference
# * The tag "jdoc_package" is used for a package summary reference
#
# Both of these link to the latest version of the API so when editing the site,
# since it concerns an unpublished API version, the links don't work. When releasing
# though, links on the published jekyll site will work.
# To refer to the previous API version, e.g. to refer to a type or member that was removed,
# the tag "jdoc_old" may be used. This tag is used exactly the same way as "jdoc". There's
# no "jdoc_package_old" tag.
#
#
# Usage (don't miss the DO NOT section at the bottom):
#
# * Simple type reference: {% jdoc core net.sourceforge.pmd.properties.PropertyDescriptor %}
#   * First arg must be the name of the module, without the "pmd-" prefix, eg  "core" will be expanded to "pmd-core"
#   * After a space, the fqcn of the type to reference is mentioned
#   * This will be expanded to [`PropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/latest/net/sourceforge/pmd/properties/PropertyDescriptor.html)
#     * Only the simple name of the type is visible by default
#     * If you want to make the visible text expand to the FQCN like so [`net.sourceforge.pmd.properties.PropertyDescriptor`](...),
#       then prefix the type name with a double bang (!!), e.g. {% jdoc core !!net.sourceforge.pmd.properties.PropertyDescriptor %}
#
# * Using context:
#   * The variable javadoc_context may be used to shorten references
#     * You can assign it like so:
#       * {% assign javadoc_context = "core @.properties" %}
#         * The "@" symbol in the initializer is expanded to "net.sourceforge.pmd", the artifact id is not optional though
#         * So this assignment sets the context to pmd-core/net.sourceforge.pmd.properties
#         * Then the symbol "@" in a javadoc tag will be expanded to "core net.sourceforge.pmd.properties"
#         * E.g. the reference {% jdoc @.PropertyDescriptor %} is expanded to the same as {% jdoc core net.sourceforge.pmd.properties.PropertyDescriptor %}
#         * Again, you may use "!!" to show the FQCN, like {% jdoc !!@.PropertyDescriptor %}
#     * If the variable is not set, then "@" in a javadoc tag means "core net.sourceforge.pmd", eg
#       {% jdoc @.properties.PropertyDescriptor %} works.
#     * The artifact can be overridden from the context, eg with the default context "core net.sourceforge.pmd"
#       {% jdoc java @.lang.java.JavaLanguageModule %} will be linked correctly to pmd-java, and @ will be expanded to
#       "net.sourceforge.pmd"
#
# * To reference a method or field: {% jdoc core @.Rule#addRuleChainVisit(java.lang.Class) %}
#   * The suffix # is followed by the name of the method
#   * The (erased) types of method arguments must be fully qualified!! This is the same
#   convention as in javadoc {@link} tags
#
# * To reference a package: {% jdoc_package @.properties %}, or {% jdoc-package @ %}
#   * The "!!" is implicit, the full package name is always displayed
#
# * DO NOT:
#   - Include spaces between dots, or anywhere except between the artifact reference and the page reference
#   - Forget to use an artifact id whenever setting javadoc_context
#   - Use double or single quotes around the arguments
#   - Use the "#" suffix to reference a nested type, instead, use a dot "." and reference it like a normal type name
#
class JavadocTag < Liquid::Tag
  def initialize(tag_name, doc_ref, tokens)
    super

    if %r/(\w+\s+)?(!!)?(@(?:\.\w+)*)(#.*)?/ =~ doc_ref

      @artifact_name = $1 && ("pmd-" + $1.strip) # is nil if not mentioned
      @show_full_name = !!$2
      @type_fqcn = $3 # may be just "@"
      @member_suffix = $4 || "" # default to empty string instead of nil

    else
      fail "Invalid javadoc reference format, see doc on javadoc_tag.rb"
    end


    if tag_name == "jdoc_package"
      @is_package_ref = true
      @show_full_name = true
    elsif tag_name == "jdoc_old"
      @use_previous_api_version = true
    end

  end

  def render(context)


    doc_ctx = context["javadoc_context"] || "core net.sourceforge.pmd"
    doc_ctx = doc_ctx.sub("@", "net.sourceforge.pmd") # Allows to use @ as shortcut when assigning javadoc_context
    doc_ctx = doc_ctx.split(" ") # first is module, snd is package

    if @type_fqcn.include?("@") # Expand using the context
      if doc_ctx.length == 2 # if it's two words then the first is the module name
        # if the artifact was mentioned in the tag, it takes precedence
        @artifact_name = @artifact_name || ("pmd-" + doc_ctx.first)
        @type_fqcn = @type_fqcn.sub("@", doc_ctx.last)
      else
        fail "Invalid javadoc context format, you must specify artifact + package prefix in exactly two words"
      end
    end

    unless @artifact_name
      fail "No artifact id was mentioned either in the tag or in the context"
    end

    if @show_full_name # !! was mentioned
      visible_name = @type_fqcn
    else
      visible_name = @type_fqcn.split("\.").last # simple name
    end

    # Hack to reference the package summary
    # Has to be done after finding the visible_name
    if @is_package_ref
      @type_fqcn = @type_fqcn + ".package-summary"
    end


    # Always hardcode the artifact version instead of using "latest"
    api_version =
        if @use_previous_api_version
        then
          context["site.pmd.previous_version"]
        else
          context["site.pmd.version"]
        end


    markup_link(visible_name, doclink(@artifact_name, api_version, @type_fqcn, @member_suffix))
  end

  private

  def doclink(artifact, api_version, type_name, member_suffix)
    "https://javadoc.io/page/net.sourceforge.pmd/#{artifact}/#{api_version}/#{type_name.gsub("\.", "/")}.html#{member_suffix}"
  end

  def markup_link(rname, link)
    "[`#{rname}`](#{link})"
  end

end

Liquid::Template.register_tag('jdoc', JavadocTag)
Liquid::Template.register_tag('jdoc_package', JavadocTag)
Liquid::Template.register_tag('jdoc_old', JavadocTag)
