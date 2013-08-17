#!/bin/bash

VERSION=0.7

rm -rf pmd-emacs-$VERSION
mkdir pmd-emacs-$VERSION
cp src/elisp/pmd.el CHANGELOG INSTALL LICENSE.txt pmd-emacs-$VERSION
zip -q -r pmd-emacs-$VERSION.zip pmd-emacs-$VERSION/
rm -rf pmd-emacs-$VERSION
