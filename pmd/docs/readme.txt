Here's how to use PMD:

1) Check out the source code
2) Change to the etc directory - "cd etc"
3) run the scp.bat file - "scp"
3) Make sure you have Ant 1.4.1 installed
4) Run the Ant build file - "ant clean"
5) write a small Ant target that uses PMD (see etc/build.xml for an example)
6) Run the Ant target - "ant pmd"
7) This generates a text file containing problem names and line numbers