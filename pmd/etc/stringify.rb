#!/usr/local/bin/ruby

filename=ARGV[0]
if ARGV.length == 2
	count=ARGV[1]
	ARGV[1].to_i.times {|x|
	 fileindex=x+1
	 output = "private static final String TEST#{fileindex} = \n"
	 File.open("#{ARGV[0]}#{fileindex}.java").each {|line|
	  output.concat "\"#{line.chomp}\" + CPD.EOL + \n"
	 }
	 output = output.slice(0, output.length-14)
	 output.concat ";"
	 output.concat "\n\n" 
	 puts output
	}
elsif ARGV.length == 1
	output = "private static final String TEST? = \n"
	File.open(filename).each {|line|
	 output.concat "\"#{line.chomp}\" + CPD.EOL + \n"
	}
	output = output.slice(0, output.length-14)
	output.concat ";" 
	puts output
end

