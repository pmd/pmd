PMD-DCPD

Contents:
Overview
How to run it on one machine
How to run it on several machines

OVERVIEW
PMD-DCPD is a distributed duplicate code detector.  It is loosely based on Michael Wise's Greedy String Tiling Algorithm.

HOW TO RUN IT ON ONE MACHINE
Download the latest release
Start a JavaSpace
Change the JavaSpace server name in Util.java and recompile the source code
Open a console window and go to the pmd-dcpd\etc directory
Start a manager by running "go DCPDManager c:\path\to\source\code 70"
Start a worker by running "client_go DCPDWorker"





