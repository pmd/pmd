#!/usr/bin/env ruby


require "liquid"
require "yaml"
require_relative "docs/_plugins/rule_tag"
require_relative "docs/_plugins/custom_filters"


release_notes_file = "docs/pages/release_notes.md"

# wrap the config under a "site." namespace because that's how jekyll does it
liquid_env = {'site' => YAML.load_file("docs/_config.yml")}

@template = Liquid::Template.parse(File.read(release_notes_file))
print(@template.render(liquid_env))
