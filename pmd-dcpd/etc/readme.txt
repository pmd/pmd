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
Change the source code directory that you want to check in DCPDManager.java
Start a DCPD manager by running etc\go DCPDManager
Start a DCPD worker by running etc\go DCPDWorker




