#!/usr/bin/ruby

Dir.chdir("/home/groups/p/pm/pmd/cgi-bin");

require 'pmd.rb'

jobsDir = Dir.new("jobs")
start=Time.now
jobsDir.each { |candidate| 
 begin 	
  if candidate[".txt"] 
   title,unixname,moduleDir,srcDir = File.new("jobs/#{candidate}").read.split(":") 
   job = PMD::Job.new(title,unixname,moduleDir,srcDir)
   puts "Checking out code"
   job.checkout_code
   puts "Running PMD"
   job.run_pmd
   puts "Cleaning up"
   job.clear
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}
stop=Time.now

`echo #{stop-start} > lastruntime.txt`

