INSTALL
This should work on pretty much any CVS server, however, all the server names and directories are hardcoded.

- install Perl and Ruby (http://www.ruby-lang.org/) if they're not already installed

- check out the code from Sourceforge

- put webpmd.pl and PMD/Project.pm in your cgi-bin directory

- create a cgi-bin/jobs directory and put jobs in there in the format:
File name: something unique, usually reponame_modulename.txt, i.e., pmd_pmd-jedit.txt
File contents: Sourceforge:PMD-JEdit:pmd:pmd-jedit:pmd-jedit/src

- create a reports/ directory under htdocs

- put processor.rb and pmd.rb in your home directory

- edit processor.rb and point it to your reports directory and your jobs directory

- set up a cron job:
10 0,3,6,9,12,15,18,21 * * * nice -n 19 /path/to/processor.rb

- let the fun begin!

I realize these are lousy instructions, but I'm not sure if anyone other than I will ever read them.  Thus, updates are welcome.


CHANGELOG
1/22/03 - Added this file

1/21/03 - Updated to use new SF CVS configuration