

# Tag to reference a rule
#
# Usage:
# {% rule "java/codestyle/LinguisticNaming" %} works from anywhere
# If inside the doc page of a ruleset/category, the language and
# category segment can be dropped, they're taken to be the same.
#
# That means rule descriptions can also reference rules e.g. by simply
# saying {% rule AvoidFinalLocalVars %} if they're in the same category
# This could allow deprecated rule notices to link to the replacement rule

class RuleTag < Liquid::Tag
  def initialize(tag_name, rule_ref, tokens)
    super

    if %r!(?:(?:(\w+)/)?(\w+)/)?(\w+)! =~ rule_ref

      @lang_name = $1
      @category_name = $2
      @rule_name = $3

    else
      fail "Invalid rule reference format"
    end

  end

  def render(context)



    if /pmd_rules_(\w+)_(\w+)\.html/ =~ context["page.permalink"]
      # If we're in a page describing a ruleset,
      # omitted language or category are taken to be that of this page
      @lang_name = @lang_name || $1
      @category_name = @category_name || $2
    end


    unless @category_name
      fail "no category for rule reference, and no implicit category name available"
    end

    unless @lang_name
      fail "no language for rule reference, and no implicit language name available"
    end


    url_prefix = ""
    # This is passed from the release notes processing script
    # When generating links for the release notes, the links should be absolute
    if context["is_release_notes_processor"]
      url_prefix = "https://pmd.github.io/pmd-#{context["site.pmd.version"]}/"
    end

    markup_link(@rule_name, url_prefix + relativelink(@lang_name, @category_name, @rule_name))
  end

  private

  def relativelink(lang, cat, rname)
    "pmd_rules_#{lang}_#{cat}.html##{rname.downcase}"
  end

  def markup_link(rname, link)
    "[`#{rname}`](#{link})"
  end

end

Liquid::Template.register_tag('rule', RuleTag)
