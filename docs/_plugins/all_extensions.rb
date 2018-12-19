# This file requires all the defined Liquid extensions for ease of reference
# Thanks stackoverflow <3
Dir[File.join(File.dirname(__FILE__), "*.rb")].reject {|file| file == __FILE__}.each {|file| require file}
