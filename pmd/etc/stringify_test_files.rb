#!/usr/local/bin/ruby

output = "private static final String TEST#{ARGV[1]} = \n"
File.open(ARGV[0]).each {|line|
 output.concat "\"#{line.chomp}\" + CPD.EOL + \n"
}
output = output.slice(0, output.length-14)
output.concat ";" 
puts output
