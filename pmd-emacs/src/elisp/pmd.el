
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
  "Rulesets to apply"
  :type 'string
  :group 'pmd)

(defun pmd-current-buffer ()
  "Run PMD on the contents of the current buffer."
  (interactive)
  (let ((file-name         (buffer-file-name) )
	(pmd-buffer-create (get-buffer "*PMD*"))
	(pmd-jar           (concat pmd-home "/lib/pmd-0.4.jar")))
    (shell-command (concat pmd-java-home " -cp " pmd-jar 
			   " net.sourceforge.pmd.PMD "
			   file-name " xml " pmd-rulesets ))))

(provide 'pmd)
