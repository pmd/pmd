#!/usr/bin/ruby

require 'pmd.rb'

jobsDir = Dir.new("jobs")
jobsDir.each { |candidate| 
 begin 	
  if candidate[".txt"] 
   title,unixname,moduleDir,srcDir = File.new("jobs/#{candidate}").read.split(":") 
   job = PMD::Job.new(title,unixname,moduleDir,srcDir)
   job.checkout_code
   job.run_pmd
   job.clear
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}


