#!/usr/local/bin/ruby

Dir.glob("rulesets/*.xml").each do |f| 
  data = File.read(f).sub(/xmlns=\"http:\/\/pmd.sf.net\/ruleset\/1.0.0\"/, '')
  File.open(f, "w") {|x| x.write(data) } 
end
