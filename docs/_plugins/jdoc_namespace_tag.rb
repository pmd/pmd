# Tag used to declare a javadoc namespace to shorten javadoc references.
#
# Usage:
# {% jdoc_nspace :coreast core::lang.ast %}
# {% jdoc_nspace :jast java::lang.java.ast %}
#
# * The first argument is the name of the namespace, it can be prefixed with a ":" for readability
# * The second argument is the package prefix of the namespace, which itself must use an already declared namespace
# Base namespaces are declared for most of the modules of PMD, with the "net.sourceforge.pmd" package prefix.
# E.g. "core::" and "pmd-core::" (aliased) point to pmd-core's "net.sourceforge.pmd" package.
#
# After those tags have been declared, the handle is used with the "name::" syntax, eg {% jdoc jast::ASTType %}
# To refer to only the package prefix defined by the namespace, use instead the ":name" syntax, e.g. {% jdoc_package :jast %}
#
class JDocNamespaceDeclaration < Liquid::Tag

  # a namespace is a pair [artifactId, base package]

  def initialize(tag_name, arg, tokens)
    super

    all_args = arg.split(" ")

    if all_args.size != 2
      "Invalid arguments for jdoc namespace declaration, expected ':name baseNSpace::package.prefix'"
    end


    @nspace_name = all_args.first.delete(":")

    if RESERVED_NSPACES.include?(@nspace_name)
      fail "Javadoc namespace #{@nspace_name} is reserved and cannot be redefined"
    end

    @this_fqcn_unresolved = all_args.last

  end

  def render(var_ctx)
    unless var_ctx[JDOC_NAMESPACE_MAP]
      var_ctx[JDOC_NAMESPACE_MAP] = JDocNamespaceDeclaration::make_base_namespaces #base namespace map
    end

    # Add the resolved QName to the map
    var_ctx[JDOC_NAMESPACE_MAP][@nspace_name] = JDocNamespaceDeclaration::parse_fqcn(@this_fqcn_unresolved, var_ctx)

    ""
  end

  # Regex to match a prefixed fqcn, used for method arguments
  NAMESPACED_FQCN_REGEX = /(\w[\w-]*)::((?:\w+\.)*\w+)/
  SYM_REGEX = /:(\w[\w-]*)/

  # Parses a namespaced fqcn of the form nspace::a.b.c.Class into a tuple [artifactId, expandedFQCN]
  # If allow_sym is true, then the syntax :nspace is allowed as well
  def self.parse_fqcn(fqcn, var_ctx, allow_sym = true)
    unless var_ctx[JDOC_NAMESPACE_MAP]
      var_ctx[JDOC_NAMESPACE_MAP] = JDocNamespaceDeclaration::make_base_namespaces #base namespace map
    end

    nspace = nil
    fqcn_suffix = ""

    if NAMESPACED_FQCN_REGEX =~ fqcn
      nspace = $1
      fqcn_suffix = $2
    elsif allow_sym && SYM_REGEX =~ fqcn
      nspace = $1
      fqcn_suffix = ""
    else
      fail "Invalid javadoc fqcn format, expected nspace::a.b.c.Class" + (allow_sym ? " or :nspace" : "") + ", but was " + fqcn
    end

    resolved_nspace = []

    unless var_ctx[JDOC_NAMESPACE_MAP] && (resolved_nspace = var_ctx[JDOC_NAMESPACE_MAP][nspace])
      fail "Undeclared javadoc namespace #{nspace}"
    end

    unless resolved_nspace.size == 2
      fail "Badly registered namespace (implementation bug)" # just to be safe
    end

    expanded_fqcn = resolved_nspace.last
    unless fqcn_suffix.empty?
      expanded_fqcn += "." + fqcn_suffix
    end


    # Return the resolved artifactId + the expanded FQCN
    [resolved_nspace.first, expanded_fqcn]
  end

  private

  JDOC_NAMESPACE_MAP = "jdoc_nspaces"
  RESERVED_NSPACES = ['apex', 'core', 'cpp', 'cs', 'dist', 'doc', 'fortran', 'go', 'groovy', 'java', 'javascript', 'jsp',
    'kotlin', 'matlab', 'objectivec', 'perl', 'php', 'plsql', 'python', 'ruby', 'scala', 'swift', 'test', 'ui',
    'modelica', 'visualforce', 'vm', 'xml'].flat_map {|m| [m, "pmd-" + m]}

  def self.make_base_namespaces
    res = {}
    RESERVED_NSPACES.each do |mod|
      pmd_prefixed = mod.start_with?("pmd") ? mod : ("pmd-" + mod)

      # Each is aliased, eg core:: is equivalent to pmd-core::
      res[mod] = [pmd_prefixed, "net.sourceforge.pmd"]
      res[pmd_prefixed] = [pmd_prefixed, "net.sourceforge.pmd"]
    end

    res
  end

end

Liquid::Template.register_tag('jdoc_nspace', JDocNamespaceDeclaration)
