PMD-DCPD

Contents:
Overview
How to run it on one machine (Win32)

OVERVIEW
PMD-DCPD is a distributed duplicate code detector.  It is loosely based on Michael Wise's Greedy String Tiling Algorithm.

HOW TO RUN IT ON ONE MACHINE (Win32)
Download the latest Jini Starter kit
Download the latest PMD-DCPD release
Start a JavaSpace
Change the JavaSpace server name in Util.java and recompile the source code
Open a console window and go to the pmd-dcpd\etc directory
Change the JavaSpace server name in go.bat and client_go.bat
Start a manager by running "go DCPDManager c:\path\to\source\code 70"
Start a worker by running "client_go DCPDWorker"





