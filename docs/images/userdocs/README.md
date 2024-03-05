## How to create pmd-demo.gif

### Prepare

```shell
mkdir $HOME/pmd-demo
cd $HOME/pmd-demo
curl -L -o jdk-master.zip https://github.com/openjdk/jdk/archive/refs/heads/master.zip
unzip jdk-master.zip
alias pmd=$HOME/PMD/source/pmd/pmd-dist/target/pmd-bin-7.0.0-SNAPSHOT/bin/pmd
clear
pmd --version
pmd check -R rulesets/java/quickstart.xml -d jdk-master/src/java.base -f text --cache pmd.cache --report-file jdk-report.txt
```

Second terminal window: `cd $HOME/pmd-demo; tail -f jdk-report.txt`

### Recording

Record screencast with https://github.com/EasyScreenCast/EasyScreenCast (a gnome3 extension)

The recorded screencast can be found in `$HOME/Videos`.

### Converting

Convert webm to gif: https://engineering.giphy.com/how-to-make-gifs-with-ffmpeg/

```shell
cd $HOME/Videos
ffmpeg -i pmd7-demo.webm -filter_complex "[0:v] fps=12,scale=960:-1,split [a][b];[a] palettegen [p];[b][p] paletteuse" pmd7-demo.gif
```
