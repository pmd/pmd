#!/usr/bin/ruby

Dir.chdir("/home/users/t/to/tomcopeland/pmdweb");

require 'pmd.rb'

jobsDir = Dir.new("/home/groups/p/pm/pmd/cgi-bin/jobs")
start=Time.now
jobsDir.each { |candidate| 
 begin 	
  if candidate[".txt"] 
   location,title,unixname,moduleDir,srcDir = File.new("/home/groups/p/pm/pmd/cgi-bin/jobs/#{candidate}").read.split(":") 
   if ARGV.length != 0 && ARGV[0] != moduleDir
    next
   end
   job = PMD::Job.new(location,title,unixname,moduleDir,srcDir)
   puts "Processing #{job}"
   File.open("/home/groups/p/pm/pmd/cgi-bin/currentjob.txt", "w") { |file| file.syswrite(job.unixName) }
   job.checkout_code
   job.run_pmd
   job.clear
   File.delete("/home/groups/p/pm/pmd/cgi-bin/currentjob.txt");
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}
stop=Time.now

`echo #{stop-start} > /home/groups/p/pm/pmd/cgi-bin/lastruntime.txt`

