#!/bin/bash

rm -rf pmd-emacs-0.6
mkdir pmd-emacs-0.6
cp src/elisp/pmd.el CHANGELOG INSTALL LICENSE.txt pmd-emacs-0.6
zip -q -r pmd-emacs-0.6.zip pmd-emacs-0.6/
rm -rf pmd-emacs-0.6
