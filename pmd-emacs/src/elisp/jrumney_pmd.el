(require 'compile)

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

(add-to-list 'compilation-error-regexp-alist
             '("\\([a-zA-Z]:?[-a-zA-Z._0-9\\\\/]+\\)\t\\([0-9]+\\)\t[^\n]+" 1 2))

(defun pmd-current-buffer ()
  "Run PMD on the contents of the current buffer."
  (interactive)
  (let ((file-name         (buffer-file-name) )
        (pmd-buffer-create (get-buffer "*PMD*"))
        (pmd-jar           (concat pmd-home "/lib/pmd-1.01.jar")))
    (compile (concat pmd-java-home " -cp " pmd-jar 
                     " net.sourceforge.pmd.PMD "
                     file-name " text " pmd-rulesets ))))

(provide 'pmd)
