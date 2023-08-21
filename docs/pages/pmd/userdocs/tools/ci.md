---
title: Continuous Integrations plugins
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_ci.html
author: Romain PELISSE <belaran@gmail.com>
---

## Introduction

PMD can be integrate through some of the Continuous Integration tools that exist now.
Here is a list of known (to us) plugin to do so.

## Jenkins Plugin

[Ullrich Hafner](https://github.com/uhafner) developed the
[Warnings Next Generation](https://plugins.jenkins.io/warnings-ng/) plugin for Jenkins. It supports
PMD among many other linting tools.

* Homepage: https://plugins.jenkins.io/warnings-ng/
* Source: https://github.com/jenkinsci/warnings-ng-plugin and https://github.com/jenkinsci/analysis-model

## Continuum

Continuum does not have a plugin for PMD per see, but can failed the build according to the
result of the PMD maven plugin.

## GitHub Action

See [Other Tools / Integrations](pmd_userdocs_tools.html#github-actions)

## MegaLinter

[🦙 Mega-Linter](https://oxsecurity.github.io/megalinter/latest/) analyzes 50 languages, 22 formats, 21 tooling formats, excessive copy-pastes, spelling mistakes and security issues in your repository sources with a GitHub Action, other CI tools or locally.

It [natively embeds PMD](https://oxsecurity.github.io/megalinter/latest/descriptors/java_pmd/).

<!-- TODO: Find out about other plugins ? -->
