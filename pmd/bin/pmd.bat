@echo off
java -cp "%~dp0\..\lib\pmd-3.9.jar;%~dp0\..\lib\jaxen-1.1.jar;%~dp0\..\lib\asm-3.0.jar;%CLASSPATH%" net.sourceforge.pmd.PMD %*

 	  	 
