#!/usr/local/bin/ruby

require '/home/tom/data/pmd/pmd-web/src/pmd.rb'

Dir.chdir(PMD::Job::ROOT);

jobsDir = Dir.new("jobs/")
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
   job.clear
   job.checkout_code
   if (job.checkOutOK)
    job.run_pmd
    job.run_cpd
    job.ncss
    job.copy_up
    job.clear
   end
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}
stop=Time.now

`echo #{stop} > lastruntime.txt`

