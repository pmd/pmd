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
(require 'xml)

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

(defvar pmd-mode-font-lock-keywords
  (list ;;fontlock keywords
   (list "\\(PMD output for\\) \\(.*$\\)"
	 '(1 font-lock-type-face append)
	 '(1 'bold append)
	 '(2 font-lock-function-name-face append))
   (list "\\(<violation.*>\\)"
	 '(1 font-lock-variable-name-face append))
   (list "\\(^[^<].*\\)"
	 '(1 font-lock-warning-face append))
   ))

;;set up keymap
(if (and nil pmd-mode-map)
    ()
  (setq pmd-mode-map (make-sparse-keymap))

  (let ((key ?1))
    (while (<= key ?9)
      (define-key pmd-mode-map (char-to-string key)
'digit-argument)
      (setq key (1+ key))))
  (define-key pmd-mode-map "c" 'pmd-clear-display)
  (define-key pmd-mode-map "q" 'pmd-quit)
  (define-key pmd-mode-map "Q" 'pmd-quit-kill)
  (define-key pmd-mode-map "p" 'pmd-prev-violation)
  (define-key pmd-mode-map [up] 'pmd-prev-violation)
  (define-key pmd-mode-map [?\C-p] 'pmd-prev-violation)
  (define-key pmd-mode-map "n" 'pmd-next-violation)
  (define-key pmd-mode-map [down] 'pmd-next-violation)
  (define-key pmd-mode-map [?\C-n] 'pmd-next-violation)
  (define-key pmd-mode-map "?" 'pmd-help)
  (define-key pmd-mode-map [?\C-x ?`] 'pmd-goto-next-violation)
  (define-key pmd-mode-map [?\C-m] 'pmd-goto-violation)
  )

(defun pmd-mode ()
  "Plugin for PMD (http://pmd.sourceforge.net).  Opens a buffer which \
displays xml output of PMD when run on the Java file in the current buffer.

Function                  Description               Keybinding
\\[pmd-next-violation] -- Move point to next violation.        n | up
\\[pmd-prev-violation] -- Move point to previous violation.    p | down
\\[pmd-quit] -- Bury *PMD* buffer.                  q
\\[pmd-quit-kill] -- Kill *PMD* buffer              Q
\\[pmd-goto-violation] -- Go to the source code matching this violation.           RET
\\[pmd-goto-next-violation] -- Go to the source code matching violation after point     C-x `
\\[pmd-clear-display] -- Clear *PMD* buffer (for append mode)  c"
  (interactive)
  (kill-all-local-variables)
  (use-local-map pmd-mode-map)
  (make-local-variable 'font-lock-defaults)
  (make-local-variable 'font-lock-verbose)
  (setq major-mode 'pmd-mode
	mode-name "PMD Mode"
	font-lock-defaults '(pmd-mode-font-lock-keywords)
	font-lock-verbose nil
					;buffer-read-only t
	truncate-lines nil))

;;-------------------------
;;Inner workings
;;-------------------------

(defun pmd-help ()
  "Help for `pmd-mode'."
  (interactive)
  (describe-function 'pmd-mode))


(defun pmd-goto-violation ()
  "Goto the line of the file in which the violation
on the current
line is found."
  (interactive)
  (block pmd-goto
    ;(setq pmd-original-point (point))
    (cond ((not (search-backward "<violation" nil t))
	   (message "No violation on this line.")
	   (goto-char pmd-original-point)
	   (return-from pmd-goto)))
    (set 'pmd-current-violation-tag (xml-parse-tag (point-max)))
    (pmd-prev-violation)
    (setq pmd-original-point (point))
    (cond ((not (search-backward "<file" nil t))
	   (message "Can't find file tag associated with this violation.")
	   (goto-char pmd-original-point)
	   (return-from pmd-goto)))
    (set 'pmd-current-file-tag (xml-parse-tag
(point-max)))
    (goto-char pmd-original-point)
    (other-window 1)
    (find-file (xml-get-attribute pmd-current-file-tag 'name))
    (goto-line (string-to-int (xml-get-attribute pmd-current-violation-tag 'line)))
    ) ;end of pmd-goto block
  )

(defun pmd-prev-violation ()
  "Go up one violation in the *PMD* buffer."
  (interactive)
  (block pmd-prev 
    (cond ((not(search-backward "</violation" nil 1))
	   (cond ((not (search-forward "</file" nil t))
		  (message "Can't fine file tag associated with this violation.")
		  (return-from pmd-prev)))
	   (cond ((not (search-backward "</violation" nil 1))
		  (message "No violations found.")
		  (return-from pmd-prev)))))
    (next-line -1)
    (beginning-of-line)))

(defun pmd-next-violation ()
  "Go up one violation in the *PMD* buffer."
  (interactive)
  (block pmd-next
    (cond ((not (search-forward "<violation" nil 1))
	   (cond ((not (search-backward "<file" nil 1))
		  (message "Can't fine file tag associated with this violation.")
		  (return-from pmd-next)))
	   (cond ((not (search-forward "<violation" nil 1))
		  (message "No violations found.")
		  (return pmd-next)))))
    (next-line 1)
    (beginning-of-line)))

(defun pmd-goto-next-violation (&optional backwards)
  "Go to next violation after point and show the location in the source code."
  (interactive)
  (if backwards
      (pmd-prev-violation)
    (pmd-next-violation))
  (pmd-goto-violation))
(defun pmd-quit ()
  "Bury the pmd buffer."
  (interactive)
  (quit-window))

(defun pmd-quit-kill ()
  "Kill the pmd buffer."
  (interactive)
  (kill-buffer (current-buffer)))


(defun pmd-clear-display ()
  (interactive)
  "Clear the *PMD* buffer of previous outputs."
  (erase-buffer))


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
	       (pmd-jar           (concat pmd-home "/lib/pmd-1.01.jar")))
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
				    file-name " xml " pmd-rulesets )) "\n"))))
	((not(string-equal (file-name-extension (buffer-file-name)) "java"))
	 (message "File is not a Java file.  Aborting."))))

(provide 'pmd)



