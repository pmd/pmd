PMD-DCPD

Contents:
Overview
How to run PMD_DCPD on one machine (Win32)
How to run PMD_DCPD on several machines (mixed)

OVERVIEW
PMD-DCPD is a distributed duplicate code detector.  It is loosely based on Michael Wise's Greedy String Tiling Algorithm.

HOW TO RUN IT ON ONE MACHINE (Win32)
Download the latest Jini Starter kit
Download the latest PMD-DCPD release
Download and install Ant (optional, but handy)
Start a JavaSpace
Change the JavaSpace server name in dcpd.properties, go.bat, client_go.bat, linuxclient_go.sh, and build.xml
Copy the dcpd.jar file to RMI codebase HTTP server (see "ant copytoserver" for an automated example)
Open a console window and go to the pmd-dcpd\etc directory
Start a manager by running "go DCPDManager c:\path\to\source\code 70"
Start a worker by running "client_go DCPDWorker"

HOW TO RUN IT ON SEVERAL MACHINES (mixed)
Download the latest Jini Starter kit
Download the latest PMD-DCPD release
Download and install Ant (optional, but handy)
Start a JavaSpace
Change the JavaSpace server name in dcpd.properties, go.bat, client_go.bat, linuxclient_go.sh, and build.xml
Copy the dcpd.jar file to RMI codebase HTTP server (see "ant copytoserver" for an automated example)
Create some workers and distribute them to your worker machines - use "ant buildworker" to build the zip file
Open a console window and go to the pmd-dcpd\etc directory
Start a manager by running "go DCPDManager c:\path\to\source\code 70"
Start the workers by running "client_go.bat" or "linuxclient_go.sh"






