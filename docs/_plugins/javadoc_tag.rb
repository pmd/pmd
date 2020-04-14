require_relative 'jdoc_namespace_tag'
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
# * Simple type reference: {% jdoc core::properties.PropertyDescriptor %}
#   * The arg of the tag is the FQCN of the type to reference, prefixed by a namespace
#   * A namespaced qname is of the form 'nspace::a.b.Class'
#     * The 'nspace::' identifies the namespace. A namespace is a shorthand for a package
#       name and also stores the reference to the maven artifact to link to. The package
#       name stored by the namespace is prefixed to the qname following the "::"
#     * Namespaces for the maven modules of PMD are predefined. E.g. 'core::' points to pmd-core, 'java::'
#       points to pmd-java. The package prefixes of these are all "net.sourceforge.pmd"
#   * Example:
#     * {% jdoc core::properties.PropertyDescriptor %} links to pmd-core, and the FQCN
#       'properties.PropertyDescriptor' is expanded to 'net.sourceforge.pmd.properties.PropertyDescriptor'
#       This is rendered as [`PropertyDescriptor`](https://javadoc.io/page/net.sourceforge.pmd/pmd-core/6.10.0/net/sourceforge/pmd/properties/PropertyDescriptor.html)
#
#
# * To reference a package: eg {% jdoc_package core::properties %} -> points to pmd-core, expanded to 'net.sourceforge.pmd.properties'
#
# * To reference a method or field: {% jdoc core::Rule#addRuleChainVisit(java.lang.Class) %}
#   * The suffix # is followed by the name of the method or field
#   * The (erased) types of method arguments must be fully qualified. This is the same
#     convention as in javadoc {@link} tags, so you can use you're IDE's javadoc auto-
#     complete and copy-paste. Namespaces also can be used for method arguments if they're from PMD.
#
#
# * Defining custom namespaces
#   * you can define a namespace pointing to a package name relevant to what
#     you're documenting to save some keystrokes.
#   * They're defined with the 'jdoc_nspace' tag, e.g.
#     {% jdoc_nspace :coreast core::lang.ast %}
#     {% jdoc_nspace :jmx java::lang.java.metrics %}
#       * The first argument is the name of the namespace, (optionally prefixed with a colon)
#       * The second argument is a namespaced FQCN identifying the package the namespace points to
#         * You need a namespace to define another namespace. Again, predefined namespaces point to
#           the different maven modules, eg 'core::' or 'apex::'
#   * After a jdoc_nspace tag, the namespace can be used with the 'name::' syntax in
#     jdoc tags or other jdoc_nspace tags. E.g.
#      {% jdoc coreast::Node %} -> points to pmd-core, expanded to 'net.sourceforge.pmd.lang.ast.Node'
#      {% jdoc jmx::impl.NcssVisitor %} -> points to pmd-java, expanded to 'net.sourceforge.pmd.lang.java.metrics.impl.NcssVisitor'
#   * If you want to reference the package prefix of a namespace, you can do so
#     by using the syntax ':nspace', e.g.
#     {% jdoc_package :jmx %} -> points to pmd-java, expanded to 'net.sourceforge.pmd.lang.java.metrics'
#     {% jdoc_package :coreast %} -> points to pmd-core, expanded to 'net.sourceforge.pmd.lang.ast'
#     * This is especially cool because it allows you to assign a namespace to a type name and then add a method suffix, eg
#       {% jdoc_nspace :PrD core::properties.PropertyDescriptor %}
#       {% jdoc :PrD#description() %}
#
# * Bang options:
#   * The visible text of the link may be customized by prefixing the reference to the
#     linked member or type with some options.
#   * Option syntax is "!opts!", and prefixes the namespace. Options are one-character switches.
#   * Available options:
#     * No options -> just the member name:
#         * {% jdoc core::Rule %} -> [`Rule`](...)
#         * {% jdoc core::Rule#setName(java.lang.String) %} -> [`setName`](...)
#         * {% jdoc core::AbstractRule#children %} -> [`children`](...)
#     * a (args) -> adds the simple name of the argument types for method references, noop for other references
#         *  {% jdoc !a!core::Rule#setName(java.lang.String) %} -> [`setName(String)`](...)
#     * q (qualify) -> prefix with the fqcn of the class, noop for package references
#         *  {% jdoc !q!core::Rule %} -> [`net.sourceforge.pmd.Rule`](...)
#         *  {% jdoc !q!core::Rule#setName(java.lang.String) %} -> [`net.sourceforge.pmd.Rule#setName`](...)
#     * c (class) -> prefix the class name for member references, noop for type and package references, or if "qualify" is specified
#         *  {% jdoc !c!core::Rule#setName(java.lang.String) %} -> [`Rule#setName`](...)
#     * Empty options ("!!") - > shorthand to a commonly relevant option
#         * For field or method references, "!!" is the "c" option
#           * {% jdoc !!core::Rule#setName(java.lang.String) %} -> [`Rule#setName`](...)
#         * For type references, "!!" is the "q" option
#           *  {% jdoc !!core::Rule %} -> [`net.sourceforge.pmd.Rule`](...)
#         * For package references, "!!" is a noop, they're always fully qualified
#     * Several options may be used at once, though this is only useful for method references:
#         * {% jdoc !ac!core::Rule#setName(java.lang.String) %} -> [`Rule#setName(String)`](...)
#         * {% jdoc !aq!core::Rule#setName(java.lang.String) %} -> [`net.sourceforge.pmd.Rule#setName(String)`](...)
#
# * DO NOT:
#   - Include spaces in any part of the reference
#   - Use double or single quotes around the arguments
#   - Use the "#" suffix to reference a nested type, instead, use a dot "." and reference it like a normal type name
#
#
class JavadocTag < Liquid::Tag

  QNAME_NO_NAMESPACE_REGEX = /((?:\w+\.)*\w+)/

  ARG_REGEX = Regexp.new(Regexp.union(JDocNamespaceDeclaration::NAMESPACED_FQCN_REGEX, QNAME_NO_NAMESPACE_REGEX).source + '(\[\])*')
  ARGUMENTS_REGEX = Regexp.new('\(\)|\((' + ARG_REGEX.source + "(?:,(?:" + ARG_REGEX.source + "))*" + ')\)')


  def initialize(tag_name, doc_ref, tokens)
    super

    # sanitize a little
    doc_ref.delete! " \"'"

    arr = doc_ref.split("#") # split into fqcn + member suffix

    @type_fqcn = arr[0]
    @member_suffix = arr[1] || "" # default to empty string

    unless Regexp.new('(!\w*!)?' + Regexp.union(JDocNamespaceDeclaration::NAMESPACED_FQCN_REGEX, JDocNamespaceDeclaration::SYM_REGEX).source ) =~ @type_fqcn
      fail "Wrong syntax for type reference, expected eg nspace::a.b.C, !opts!nspace::a.b.C, or :nspace"
    end

    # If no options, then split produces [@type_fqcn]
    # If options are present, then split produces eg ["", "aq", @type_fqcn] (there's an empty string first)
    # If !!, then split produces eg ["", "", @type_fqcn]
    *opts, @type_fqcn = @type_fqcn.split("!") # split into options + type fqcn

    @opts = Options.new(opts.last) # ignore first empty string, may be nil

    if tag_name == "jdoc_package"
      @is_package_ref = true
    elsif tag_name == "jdoc_old"
      @use_previous_api_version = true
    end
  end

  def render(var_ctx)

    artifact_name, @type_fqcn = JDocNamespaceDeclaration::parse_fqcn(@type_fqcn, var_ctx)

    JavadocTag::diagnose(artifact_name, @type_fqcn, @is_package_ref)

    # Expand FQCN of arguments
    @member_suffix.gsub!(JDocNamespaceDeclaration::NAMESPACED_FQCN_REGEX) {|fqcn| JDocNamespaceDeclaration::parse_fqcn(fqcn, var_ctx)[1]}
    @member_suffix.gsub!(JDocNamespaceDeclaration::SYM_REGEX) {|fqcn| JDocNamespaceDeclaration::parse_fqcn(fqcn, var_ctx)[1]}

    visible_name = JavadocTag::get_visible_name(@opts, @type_fqcn, @member_suffix, @is_package_ref)

    # Hack to reference the package summary
    # Has to be done after finding the visible_name
    if @is_package_ref
      @type_fqcn = @type_fqcn + ".package-summary"
    end

    # Hardcode the artifact version instead of using "latest"
    api_version = var_ctx["site.pmd." + (@use_previous_api_version ? "previous_version" : "version")]


    markup_link(visible_name, doclink(var_ctx["site.javadoc_url_prefix"], artifact_name, api_version, @type_fqcn, @member_suffix))
  end

  private

  def doclink(url_prefix, artifact, api_version, type_name, member_suffix)
    "#{url_prefix}/#{artifact}/#{api_version}/#{type_name.gsub("\.", "/")}.html##{member_suffix}"
  end

  def markup_link(rname, link)
    "<a href=\"#{link}\"><code>#{rname}</code></a>"
  end


  def self.get_visible_name(opts, type_fqcn, member_suffix, is_package_ref)

    # method or field
    if member_suffix && Regexp.new('(\w+)(' + ARGUMENTS_REGEX.source + ")?") =~ member_suffix

      suffix = $1 # method or field name

      if opts.show_args? && $2 && !$2.empty? # is method

        args = ($3 || "").split(",").map {|a| a.gsub(/\w+\./, "").strip} # map to simple names

        suffix = "#{suffix}(#{args.join(", ")})"
      end

      visible_name = if opts.show_fqcn?
                       type_fqcn + "#" + suffix
                     elsif opts.is_double_bang? || opts.show_class?
                       type_fqcn.split("\.").last + "#" + suffix # type simple name
                     else
                       suffix # just method name + possibly args
                     end

      return visible_name
    end

    # else package or type, for packages the FQCN_OPTION is present

    if is_package_ref || opts.show_fqcn? || opts.is_double_bang?
      type_fqcn
    else
      type_fqcn.split("\.").last # type simple name
    end
  end

  BASE_PMD_DIR = File.join(File.expand_path(File.dirname(__FILE__)), "..", "..")

  def self.diagnose(artifact_id, fqcn, expect_package)
    resolved_type = JavadocTag::fqcn_type(artifact_id, fqcn)

    tag_name= expect_package ? "jdoc_package" : "jdoc"

    if resolved_type == :package && !expect_package
      warn "\e[33;1m#{tag_name} generated link to #{fqcn}, but it was found to be a package name. Did you mean to use jdoc_package instead of jdoc?\e[0m"
    elsif resolved_type == :file && expect_package
      warn "\e[33;1m#{tag_name} generated link to #{fqcn}, but it was found to be a java file name. Did you mean to use jdoc instead of jdoc_package?\e[0m"
    elsif !resolved_type
      warn "\e[33;1m#{tag_name} generated link to #{fqcn}, but the #{expect_package ? "directory" : "source file"} couldn't be found in the source tree of #{artifact_id}\e[0m"
    end
  end

  # Returns :package, or :file depending on the type of entity the fqcn refers to on the filesystem
  # Returns nil if it cannot be found
  def self.fqcn_type(artifact_id, fqcn)

    artifact_dir = File.join(BASE_PMD_DIR, artifact_id)
    src_dirs = [
        File.join(artifact_dir, "src", "main", "java"),
        File.join(artifact_dir, "target", "generated-sources", "javacc")
    ].select {|dir| File.exist?(dir)}

    targets = src_dirs
                  .map {|dir| File.join(dir, fqcn.split("."))}
                  .map {|f| File.file?(f + ".java") ? :file : File.exist?(f) ? :package : nil}
                  .compact

    targets.first
  end

  class Options

    def initialize(str)
      if str.nil?
        @opts = ""
        return
      else
        @opts = str.empty? ? "!!" : str
      end

      invalid = str.delete("aqc")

      unless invalid.empty?
        fail "Unknown display options '#{invalid}', I know only aqc"
      end
    end

    def is_double_bang?
      @opts == "!!"
    end

    def show_args?
      @opts.include? "a"
    end

    def show_fqcn?
      @opts.include? "q"
    end


    def show_class?
      @opts.include? "c"
    end
  end

end

Liquid::Template.register_tag('jdoc', JavadocTag)
Liquid::Template.register_tag('jdoc_package', JavadocTag)
Liquid::Template.register_tag('jdoc_old', JavadocTag)
