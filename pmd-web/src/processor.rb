#!/usr/bin/ruby

Dir.chdir("/home/groups/p/pm/pmd/cgi-bin/");

require 'pmd.rb'

jobsDir = Dir.new("jobs")
start=Time.now
jobsDir.each { |candidate| 
 begin 	
  if candidate[".txt"] 
   location,title,unixname,moduleDir,srcDir = File.new("jobs/#{candidate}").read.split(":") 
   if ARGV.length != 0 && ARGV[0] != moduleDir
    next
   end
   job = PMD::Job.new(location,title,unixname,moduleDir,srcDir)
   puts "Processing #{job}"
   File.open("currentjob.txt", "w") { |file| file.syswrite(job.unixName) }
   job.checkout_code
   job.run_pmd
   job.clear
   File.delete("currentjob.txt");
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}
stop=Time.now

`echo #{stop-start} > lastruntime.txt`

