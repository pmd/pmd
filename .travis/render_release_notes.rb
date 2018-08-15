#!/usr/bin/env ruby

# Renders the release notes for Github releases,
# and prints them to standard output

# Doesn't trim the header, which is done in shell

# Args:
# ARGV[0] : location of the file to render

require "liquid"
require "yaml"

# include some custom liquid extensions
require_relative "../docs/_plugins/rule_tag"
require_relative "../docs/_plugins/custom_filters"


# this could be somewhere else
module Logger

  def Logger.log_error(should_exit = true, message)
    log_col(COL_RED, :error, message)
    if should_exit
      exit 1
    end
  end

  private

  def Logger.log_col(col, tag, message)
    puts "#{col}[#{tag.to_s.upcase}] In #{$0}: #{message}#{COL_RESET}"
  end

  COL_GREEN = "\e[32m"
  COL_YELLOW = "\e[33;1m"
  COL_RED = "\e[31m"
  COL_RESET = "\e[0m"

end

# START OF THE SCRIPT

unless ARGV.length == 1
  Logger::log_error "No file name provided"
end

unless File.exists?(ARGV[0])
  Logger::log_error("The provided file must exist")
end

release_notes_file = ARGV[0]

# wrap the config under a "site." namespace because that's how jekyll does it
liquid_env = {'site' => YAML.load_file("docs/_config.yml")}


to_render = File.read(release_notes_file)
rendered = Liquid::Template.parse(to_render).render(liquid_env)


print(rendered)
