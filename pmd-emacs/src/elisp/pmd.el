;;; pmd.el --- Major mode for pluging PMD into Emacs.

;; Author: John Russell <drjimmy42@yahoo.com>
;; Maintainer: John Russell <drjimmy42@yahoo.com>
;; Created: Dec 30 2002
;; Version: $Revision$
;; Keywords: PMD major-mode

;; Copyright (C) 2002 John Russell

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to
;; the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

;;; Commentary:
;; Installation:
;; 	add this line to your .emacs file:
;; 	    (autoload 'pmd-current-buffer "pmd" "PMD Mode" t)
;; 	once this is done, you can call the pmd-current-buffer function
;; 	using C-x or bind it to a key sequence.

;;   NOTE: This requies the xml parser in xml.el.  This is distributed
;; 	with the standard Gnu Emacs 21.x release. 

;; Configuration:
;; 	Most user variables can be customized through standard
;; 	customization buffers.  Type:
;; 	    M-x pmd-customize
;; 		--or--
;; 	    M-x customize-group <RET>
;; 	    pmd <RET>
	
;; Description:
;; 	This mode is similar to the compilation buffer found in the
;; 	JDE.  When you run pmd-current-buffer on a .java file, it runs
;; 	the PMD tool (http://pmd.sourceforge.net/) on the file with
;; 	the rulesets defined in pmd-rulesets.
	
;; 	Type ? in the *PMD* buffer for a list of key bindings for
;; 	pmd-mode and usage help.

(defgroup pmd nil "PMD"
  :group 'emacs)

(defcustom pmd-java-home "/usr/local/bin/java"
  "Java binary to run PMD with."
  :type 'file
  :group 'pmd )

(defcustom pmd-home "~/pmd"
  "Directory where PMD is installed."
  :type 'directory
  :group 'pmd)

(defcustom pmd-rulesets "rulesets/basic.xml"
  "A comma delimited list of Rulesets to apply"
  :type 'string
  :group 'pmd)

(defcustom pmd-append-output 'nil
  "If non-nil, append each output to the end of the *PMD* buffer,
else clear buffer each time."
  :type 'boolean
  :group 'pmd)

(defcustom pmd-version "1.02"
  "The main library version, used to create the jar file name for the Java invocation."
  :type 'string
  :group 'pmd)


(defun pmd-mode ()
  "Plugin for PMD (http://pmd.sourceforge.net).  Opens a buffer which \
displays the output of PMD when run on the Java file in the current buffer.
Uses the standard compilation mode for navigation."

  (interactive)
  (kill-all-local-variables)
  (setq mode-name "PMD Mode" 
   truncate-lines nil)
  (compilation-mode))

;;-------------------------
;;Inner workings
;;-------------------------

(defun pmd-help ()
  "Help for `pmd-mode'."
  (interactive)
  (describe-function 'pmd-mode))

;;-------------------------
;; Main functions
;;-------------------------


;;;###autoload 
(defun pmd-customize ()
  "Customization of group `pmd' for Pmd-mode."
  (interactive)
  (customize-group "pmd"))

;;;###autoload 
(defun pmd-current-buffer ()
  "Run PMD on the contents of the current buffer."
  (interactive)
  (cond ((string-equal (file-name-extension
(buffer-file-name)) "java")
	 (let ((file-name         (buffer-file-name) )
;; Nascif: updated version
	       (pmd-jar (concat pmd-home "/lib/pmd-" pmd-version ".jar"))) 
	   (if (eq (count-windows) 1)
	       (split-window-vertically))
	   (other-window 1)
	   (switch-to-buffer (get-buffer-create "*PMD*"))
	   (cond (pmd-append-output
		  (goto-char(point-max)))
		 ((not pmd-append-output)
		  (erase-buffer)))
		  
	   (pmd-mode)
	   (insert-string (concat " PMD output for " pmd-rulesets "\n\n"))
	   (insert (concat (shell-command-to-string 
			    (concat pmd-java-home " -cp " pmd-jar " net.sourceforge.pmd.PMD "
				    file-name " emacs " pmd-rulesets )) "\n"))))
	((not(string-equal (file-name-extension (buffer-file-name)) "java"))
	 (message "File is not a Java file.  Aborting."))))

(provide 'pmd)



