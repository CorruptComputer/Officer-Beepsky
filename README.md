[![Build Status](https://travis-ci.org/CorruptComputer/Officer-Beepsky.svg?branch=master)](https://travis-ci.org/CorruptComputer/Officer-Beepsky)
### Officer-Beepsky is a Discord bot made using:
* [Discord4J](https://github.com/austinv11/Discord4J)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [SLF4J](https://www.slf4j.org/)

### Building
To build you need the latest JDK 8 (9 works as well, but is not officially supported by D4j yet),
RedHat's OpenJDK or Oracle's JDK should both work fine, however OpenJDK is what I will be using and
what is used for TravisCI. Optionally you may also want to install Intellij IDEA Community.

The easiest way to build is using `./gradlew fatJar` in either command prompt (Windows), or in the
terminal (Linux/Mac).

You could alternatively use Intellij and select fatJar from `Gradle Tasks->Other->fatJar`, and once
you've done this step you can simply build by pressing the green run arrow at the top.

The build name is fatJar because well, the jar is 'fat'. Which means all dependencies will be packed
into it in order to make it easily portable.

### Running
You can run it from the terminal/command prompt by typing `java -jar Officer-Beepsky-x.x.x.jar <Discord token> <Owner ID>`.

### Build, run, and update script
Optionally if you are using Linux to host you can use the following script to build, run, and update
the bot when needed:
```
curl https://raw.githubusercontent.com/CorruptComputer/Officer-Beepsky/master/startAndRun.sh -o startAndRun.sh
bash startAndRun.sh
```
And when its time to update just private message the bot `restart` and it will update and restart!
**WARNING: Currently with the `restart` command if you do not have the script setup it will not
restart the bot, but only shut it down.**

### Contributing
This project follows the [Google Java Style Guide](http://google.github.io/styleguide/javaguide.html).
Warnings will appear during the build process if the style is incorrect, you can add the style to your
Intellij by using [this](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)
and [this guide](https://www.jetbrains.com/help/idea/code-style.html).