#!/usr/local/bin/ruby

require 'rexml/document'
include REXML

Dir.glob("../rulesets/*.xml").sort.each do |ruleset_file|
  next if ["scratchpad", "favorites"].include?(File.basename(ruleset_file).split(".")[0]) || !ruleset_file["migrating"].nil?
  xml = Document.new(File.read(ruleset_file))
  puts "============================================ #{xml.elements["//ruleset"].attributes["name"]}"
  xml.elements.each("//ruleset/rule") do |rule|
    next if rule.elements["description"].nil?
    puts rule.attributes["name"]
    puts "Description: #{rule.elements["description"].text.strip}"
    puts "Priority: #{rule.elements["priority"].text}\n\n"
  end
end
