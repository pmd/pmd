# PMD - source code analyzer

[![Join the chat at https://gitter.im/pmd/pmd](https://badges.gitter.im/pmd/pmd.svg)](https://gitter.im/pmd/pmd?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://github.com/pmd/pmd/workflows/build/badge.svg?branch=master)](https://github.com/pmd/pmd/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.pmd/pmd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.pmd/pmd)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-green?labelColor=blue)](https://github.com/jvm-repo-rebuild/reproducible-central#net.sourceforge.pmd:pmd)
[![Coverage Status](https://coveralls.io/repos/github/pmd/pmd/badge.svg)](https://coveralls.io/github/pmd/pmd)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a674ee8642ed44c6ba7633626ee95967)](https://www.codacy.com/app/pmd/pmd?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pmd/pmd&amp;utm_campaign=Badge_Grade)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md) 
[![Documentation (latest)](https://img.shields.io/badge/docs-latest-green)](https://pmd.github.io/latest/)

**PMD** is a source code analyzer. It finds common programming flaws like unused variables, empty catch blocks,
unnecessary object creation, and so forth. It supports many languages. It can be extended with custom rules.
It uses JavaCC and Antlr to parse source files into abstract syntax trees (AST) and runs rules against them to find violations.
Rules can be written in Java or using a XPath query.

It supports Java, JavaScript, Salesforce.com Apex and Visualforce,
Modelica, PLSQL, Apache Velocity, XML, XSL, Scala.

Additionally it includes **CPD**, the copy-paste-detector. CPD finds duplicated code in
C/C++, C#, Dart, Fortran, Go, Groovy, Java, JavaScript, JSP, Kotlin, Lua, Matlab, Modelica,
Objective-C, Perl, PHP, PLSQL, Python, Ruby, Salesforce.com Apex, Scala, Swift, Visualforce and XML.

In the future we hope to add support for data/control flow analysis and automatic (quick) fixes where
it makes sense.

## 🚀 Installation and Usage

Download the latest binary zip from the [releases](https://github.com/pmd/pmd/releases/latest)
and extract it somewhere.

Execute `bin/run.sh pmd` or `bin\pmd.bat`.

See also [Getting Started](https://pmd.github.io/latest/pmd_userdocs_installation.html)

**Demo:**

This shows how PMD can detect for loops, that can be replaced by for-each loops.

![Demo](docs/images/userdocs/pmd-demo.gif)

There are plugins for Maven and Gradle as well as for various IDEs.
See [Tools / Integrations](https://pmd.github.io/latest/pmd_userdocs_tools.html)

## ℹ️ How to get support?

*   How do I? -- Ask a question on [StackOverflow](https://stackoverflow.com/questions/tagged/pmd)
    or on [discussions](https://github.com/pmd/pmd/discussions).
*   I got this error, why? -- Ask a question on [StackOverflow](https://stackoverflow.com/questions/tagged/pmd)
    or on [discussions](https://github.com/pmd/pmd/discussions).
*   I got this error and I'm sure it's a bug -- file an [issue](https://github.com/pmd/pmd/issues).
*   I have an idea/request/question -- create a new [discussion](https://github.com/pmd/pmd/discussions).
*   I have a quick question -- ask on our [Gitter chat](https://gitter.im/pmd/pmd).
*   Where's your documentation? -- <https://pmd.github.io/latest/>

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Our latest source of PMD can be found on [GitHub](https://github.com/pmd/pmd). Fork us!

*   [How to build PMD](BUILDING.md)
*   [How to contribute to PMD](CONTRIBUTING.md)

The rule designer is developed over at [pmd/pmd-designer](https://github.com/pmd/pmd-designer).
Please see [its README](https://github.com/pmd/pmd-designer#contributing) for
developer documentation.

## 🪙 Financial Contributors

Become a financial contributor and help us sustain our community. [Contribute](https://opencollective.com/pmd/contribute)

## ✨ Contributors

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/0xflotus"><img src="https://avatars.githubusercontent.com/u/26602940?v=4?s=100" width="100px;" alt=""/><br /><sub><b>0xflotus</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=0xflotus" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/akshatbahety"><img src="https://avatars.githubusercontent.com/u/17676203?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Akshat Bahety</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=akshatbahety" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/albfernandez"><img src="https://avatars.githubusercontent.com/u/2701620?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Alberto Fernández</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=albfernandez" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/vovkss"><img src="https://avatars.githubusercontent.com/u/5391412?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Alex Shesterov</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=vovkss" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/anand13s"><img src="https://avatars.githubusercontent.com/u/3236002?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Anand Subramanian</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=anand13s" title="Code">💻</a></td>
    <td align="center"><a href="https://atrosinenko.github.io/"><img src="https://avatars.githubusercontent.com/u/9654772?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Anatoly Trosinenko</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=atrosinenko" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/andipabst"><img src="https://avatars.githubusercontent.com/u/9639382?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Andi Pabst</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=andipabst" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/adangel"><img src="https://avatars.githubusercontent.com/u/1573684?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Andreas Dangel</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=adangel" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=adangel" title="Documentation">📖</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/andrey-mochalov-063751108/?locale=en_US"><img src="https://avatars.githubusercontent.com/u/3083503?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Andrey Mochalov</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=epidemia" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ajeans"><img src="https://avatars.githubusercontent.com/u/2376384?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Arnaud Jeansen</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=ajeans" title="Code">💻</a></td>
    <td align="center"><a href="https://kroartem.wordpress.com/"><img src="https://avatars.githubusercontent.com/u/1813101?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Artem</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=KroArtem" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/djydewang"><img src="https://avatars.githubusercontent.com/u/18324858?v=4?s=100" width="100px;" alt=""/><br /><sub><b>BBG</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=djydewang" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=djydewang" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/pamidi99"><img src="https://avatars.githubusercontent.com/u/16791958?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Bhanu Prakash Pamidi</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=pamidi99" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Vampire"><img src="https://avatars.githubusercontent.com/u/325196?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Björn Kautler</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=Vampire" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/refactormyself"><img src="https://avatars.githubusercontent.com/u/17991837?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Bolarinwa Saheed Olayemi</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=refactormyself" title="Code">💻</a></td>
    <td align="center"><a href="https://blog.arkey.fr/"><img src="https://avatars.githubusercontent.com/u/803621?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Brice Dutheil</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=bric3" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/chrisdutz"><img src="https://avatars.githubusercontent.com/u/651105?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Christofer Dutz</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=chrisdutz" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Clint-Chester"><img src="https://avatars.githubusercontent.com/u/12729644?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Clint Chester</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=Clint-Chester" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/oowekyala"><img src="https://avatars.githubusercontent.com/u/24524930?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Clément Fournier</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=oowekyala" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=oowekyala" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/CyrilSicard"><img src="https://avatars.githubusercontent.com/u/45353161?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Cyril</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=CyrilSicard" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/daleanson"><img src="https://avatars.githubusercontent.com/u/2112276?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Dale</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=daleanson" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/davidburstromspotify"><img src="https://avatars.githubusercontent.com/u/2573207?v=4?s=100" width="100px;" alt=""/><br /><sub><b>David Burström</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=davidburstromspotify" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/DavidRenz"><img src="https://avatars.githubusercontent.com/u/8180433?v=4?s=100" width="100px;" alt=""/><br /><sub><b>David Renz</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=DavidRenz" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/borovikovd"><img src="https://avatars.githubusercontent.com/u/43751473?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Denis Borovikov</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=borovikovd" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/dreniers"><img src="https://avatars.githubusercontent.com/u/9007290?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Dennie Reniers</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=dreniers" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/dionisioC"><img src="https://avatars.githubusercontent.com/u/8872359?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Dionisio Cortés Fernández</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=dionisioC" title="Code">💻</a></td>
    <td align="center"><a href="http://www.filipesperandio.com/"><img src="https://avatars.githubusercontent.com/u/316873?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Filipe Esperandio</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=filipesperandio" title="Code">💻</a></td>
    <td align="center"><a href="http://domui.org/"><img src="https://avatars.githubusercontent.com/u/1500452?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Frits Jalvingh</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=fjalvingh" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/gibarsin"><img src="https://avatars.githubusercontent.com/u/9052089?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gonzalo Exequiel Ibars Ingman</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=gibarsin" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/GuntherSchrijvers"><img src="https://avatars.githubusercontent.com/u/56870283?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Gunther Schrijvers</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=GuntherSchrijvers" title="Code">💻</a></td>
    <td align="center"><a href="http://about.me/hgschmie"><img src="https://avatars.githubusercontent.com/u/39495?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Henning Schmiedehausen</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=hgschmie" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/hvbargen"><img src="https://avatars.githubusercontent.com/u/37015738?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Henning von Bargen</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=hvbargen" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/hooperbloob"><img src="https://avatars.githubusercontent.com/u/1541370?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Hooperbloob</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=hooperbloob" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/IDoCodingStuffs"><img src="https://avatars.githubusercontent.com/u/43346404?v=4?s=100" width="100px;" alt=""/><br /><sub><b>IDoCodingStuffs</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=IDoCodingStuffs" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/janaertgeerts/"><img src="https://avatars.githubusercontent.com/u/2192516?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jan Aertgeerts</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=JAertgeerts" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/jbartolotta-sfdc"><img src="https://avatars.githubusercontent.com/u/18196574?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jeff Bartolotta</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jbartolotta-sfdc" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/jeffhube"><img src="https://avatars.githubusercontent.com/u/1283264?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jeff Hube</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jeffhube" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/John-Teng"><img src="https://avatars.githubusercontent.com/u/16723151?v=4?s=100" width="100px;" alt=""/><br /><sub><b>John-Teng</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=John-Teng" title="Code">💻</a></td>
    <td align="center"><a href="https://darakian.github.io/"><img src="https://avatars.githubusercontent.com/u/3607524?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jon Moroney</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=darakian" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/jonathanwiesel"><img src="https://avatars.githubusercontent.com/u/1326781?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jonathan Wiesel</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jonathanwiesel" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/joseph-allen-9602671/"><img src="https://avatars.githubusercontent.com/u/3989748?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Joseph</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=JosephAllen" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/jfeingold35"><img src="https://avatars.githubusercontent.com/u/4054488?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Josh Feingold</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jfeingold35" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/jtjeferreira"><img src="https://avatars.githubusercontent.com/u/943051?v=4?s=100" width="100px;" alt=""/><br /><sub><b>João Ferreira</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jtjeferreira" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/jsotuyod"><img src="https://avatars.githubusercontent.com/u/802626?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Juan Martín Sotuyo Dodero</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=jsotuyod" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=jsotuyod" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/clem0110"><img src="https://avatars.githubusercontent.com/u/7726426?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Kirk Clemens</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=clem0110" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/kris-scheibe"><img src="https://avatars.githubusercontent.com/u/20039785?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Kris Scheibe</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=kris-scheibe" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/larry-diamond-3964042/"><img src="https://avatars.githubusercontent.com/u/1066589?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Larry Diamond</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=larrydiamond" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/lsoncini"><img src="https://avatars.githubusercontent.com/u/12226579?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Lucas Soncini</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=lsoncini" title="Code">💻</a></td>
    <td align="center"><a href="https://pmd.github.io/"><img src="https://avatars.githubusercontent.com/u/26070915?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Machine account for PMD</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=pmd-bot" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/maikelsteneker"><img src="https://avatars.githubusercontent.com/u/2788927?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Maikel Steneker</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=maikelsteneker" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/manuel-moya-ferrer-11163168/"><img src="https://avatars.githubusercontent.com/u/15876612?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Manuel Moya Ferrer</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=mmoyaferrer" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/markhall82"><img src="https://avatars.githubusercontent.com/u/22261511?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Mark Hall</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=markhall82" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/MatiasComercio"><img src="https://avatars.githubusercontent.com/u/9677633?v=4?s=100" width="100px;" alt=""/><br /><sub><b>MatiasComercio</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=MatiasComercio" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/matifraga"><img src="https://avatars.githubusercontent.com/u/7543268?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Matías Fraga</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=matifraga" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/marob"><img src="https://avatars.githubusercontent.com/u/3486231?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Maxime Robert</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=marob" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Drofff"><img src="https://avatars.githubusercontent.com/u/45700628?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Mykhailo Palahuta</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=Drofff" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/pyxide"><img src="https://avatars.githubusercontent.com/u/9992381?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Olivier Parent</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=pyxide" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ollieabbey"><img src="https://avatars.githubusercontent.com/u/52665918?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Ollie Abbey</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=ollieabbey" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ozangulle"><img src="https://avatars.githubusercontent.com/u/1334150?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Ozan Gulle</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=ozangulle" title="Code">💻</a></td>
    <td align="center"><a href="http://belaran.eu/wordpress/"><img src="https://avatars.githubusercontent.com/u/117836?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Pelisse Romain</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rpelisse" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=rpelisse" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/pchittum"><img src="https://avatars.githubusercontent.com/u/1127876?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Peter Chittum</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=pchittum" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/acanda"><img src="https://avatars.githubusercontent.com/u/174978?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Philip Graf</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=acanda" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/pzygielo"><img src="https://avatars.githubusercontent.com/u/11896137?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Piotrek Żygieło</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=pzygielo" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/rajeshggwp"><img src="https://avatars.githubusercontent.com/u/8025160?v=4?s=100" width="100px;" alt=""/><br /><sub><b>RajeshR</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rajeshggwp" title="Code">💻</a></td>
    <td align="center"><a href="https://dogeforce.com/"><img src="https://avatars.githubusercontent.com/u/6956403?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Renato Oliveira</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=renatoliveira" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/rmartinus"><img src="https://avatars.githubusercontent.com/u/12573669?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Robbie Martinus</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rmartinus" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/rsoesemann"><img src="https://avatars.githubusercontent.com/u/8180281?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Robert Sösemann</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rsoesemann" title="Code">💻</a> <a href="https://github.com/pmd/pmd/commits?author=rsoesemann" title="Documentation">📖</a> <a href="#talk-rsoesemann" title="Talks">📢</a></td>
    <td align="center"><a href="https://www.whatsthistimestamp.com/"><img src="https://avatars.githubusercontent.com/u/16778?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Robin Stocker</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=robinst" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/rsalvador"><img src="https://avatars.githubusercontent.com/u/1301827?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Roman Salvador</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rsalvador" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/xuthus"><img src="https://avatars.githubusercontent.com/u/6282044?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Sergey Yanzin</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=xuthus" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/prophet1906"><img src="https://avatars.githubusercontent.com/u/32415088?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Shubham</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=prophet1906" title="Code">💻</a></td>
    <td align="center"><a href="http://www.tiobe.com/"><img src="https://avatars.githubusercontent.com/u/2196103?v=4?s=100" width="100px;" alt=""/><br /><sub><b>TIOBE Software</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=tiobe" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/DTecheira"><img src="https://avatars.githubusercontent.com/u/1074288?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Techeira Damián</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=DTecheira" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Snap252"><img src="https://avatars.githubusercontent.com/u/10380619?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Thomas Smith</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=Snap252" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Thunderforge"><img src="https://avatars.githubusercontent.com/u/6200170?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Thunderforge</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=Thunderforge" title="Code">💻</a></td>
    <td align="center"><a href="https://miranda-ng.org/"><img src="https://avatars.githubusercontent.com/u/2698843?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Tobias Weimer</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=tweimer" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/tomidelucca"><img src="https://avatars.githubusercontent.com/u/1288160?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Tomi De Lucca</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=tomidelucca" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/utkuc"><img src="https://avatars.githubusercontent.com/u/15714598?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Utku Cuhadaroglu</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=utkuc" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/YodaDaCoda"><img src="https://avatars.githubusercontent.com/u/365349?v=4?s=100" width="100px;" alt=""/><br /><sub><b>William Brockhus</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=YodaDaCoda" title="Code">💻</a></td>
    <td align="center"><a href="http://xenoamess.com/"><img src="https://avatars.githubusercontent.com/u/17455337?v=4?s=100" width="100px;" alt=""/><br /><sub><b>XenoAmess</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=XenoAmess" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/YYoungC"><img src="https://avatars.githubusercontent.com/u/55069165?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Young Chan</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=YYoungC" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/andrey81inmd"><img src="https://avatars.githubusercontent.com/u/2624682?v=4?s=100" width="100px;" alt=""/><br /><sub><b>andrey81inmd</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=andrey81inmd" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/astillich-igniti"><img src="https://avatars.githubusercontent.com/u/57359104?v=4?s=100" width="100px;" alt=""/><br /><sub><b>astillich-igniti</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=astillich-igniti" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/berkam"><img src="https://avatars.githubusercontent.com/u/26228441?v=4?s=100" width="100px;" alt=""/><br /><sub><b>berkam</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=berkam" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/apps/dependabot"><img src="https://avatars.githubusercontent.com/in/29110?v=4?s=100" width="100px;" alt=""/><br /><sub><b>dependabot[bot]</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=dependabot[bot]" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/gwilymatgearset"><img src="https://avatars.githubusercontent.com/u/43957113?v=4?s=100" width="100px;" alt=""/><br /><sub><b>gwilymatgearset</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=gwilymatgearset" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/hvbtup"><img src="https://avatars.githubusercontent.com/u/7644776?v=4?s=100" width="100px;" alt=""/><br /><sub><b>hvbtup</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=hvbtup" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/josemanuelrolon"><img src="https://avatars.githubusercontent.com/u/1685807?v=4?s=100" width="100px;" alt=""/><br /><sub><b>josemanuelrolon</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=josemanuelrolon" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/kabroxiko"><img src="https://avatars.githubusercontent.com/u/20568120?v=4?s=100" width="100px;" alt=""/><br /><sub><b>kabroxiko</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=kabroxiko" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/kenji21"><img src="https://avatars.githubusercontent.com/u/1105089?v=4?s=100" width="100px;" alt=""/><br /><sub><b>kenji21</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=kenji21" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/orimarko"><img src="https://avatars.githubusercontent.com/u/17137249?v=4?s=100" width="100px;" alt=""/><br /><sub><b>orimarko</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=orimarko" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/piotrszymanski-sc"><img src="https://avatars.githubusercontent.com/u/71124942?v=4?s=100" width="100px;" alt=""/><br /><sub><b>piotrszymanski-sc</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=piotrszymanski-sc" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/reudismam"><img src="https://avatars.githubusercontent.com/u/1970407?v=4?s=100" width="100px;" alt=""/><br /><sub><b>reudismam</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=reudismam" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/rmohan20"><img src="https://avatars.githubusercontent.com/u/58573547?v=4?s=100" width="100px;" alt=""/><br /><sub><b>rmohan20</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=rmohan20" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ryan-gustafson"><img src="https://avatars.githubusercontent.com/u/1227016?v=4?s=100" width="100px;" alt=""/><br /><sub><b>ryan-gustafson</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=ryan-gustafson" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/sergeygorbaty"><img src="https://avatars.githubusercontent.com/u/14813710?v=4?s=100" width="100px;" alt=""/><br /><sub><b>sergeygorbaty</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=sergeygorbaty" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/sturton"><img src="https://avatars.githubusercontent.com/u/1734891?v=4?s=100" width="100px;" alt=""/><br /><sub><b>sturton</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=sturton" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/testation21"><img src="https://avatars.githubusercontent.com/u/47239708?v=4?s=100" width="100px;" alt=""/><br /><sub><b>testation21</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=testation21" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/xnYi9wRezm"><img src="https://avatars.githubusercontent.com/u/61201892?v=4?s=100" width="100px;" alt=""/><br /><sub><b>xnYi9wRezm</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=xnYi9wRezm" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/zgrzyt93"><img src="https://avatars.githubusercontent.com/u/54275965?v=4?s=100" width="100px;" alt=""/><br /><sub><b>zgrzyt93</b></sub></a><br /><a href="https://github.com/pmd/pmd/commits?author=zgrzyt93" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

## 📝 License

[BSD Style](LICENSE)
