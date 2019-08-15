[![Officer Beepsky](https://i.imgur.com/BMbWjGx.png)](https://tgstation13.org/wiki/Beepsky#Securitron) [![Build Status](https://semaphoreci.com/api/v1/corruptcomputer/officer-beepsky/branches/master/badge.svg)](https://semaphoreci.com/corruptcomputer/officer-beepsky)

### Officer-Beepsky is a Discord bot made using:
* [Discord4J](https://github.com/austinv11/Discord4J)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [SLF4J](https://www.slf4j.org/)

### Building
To build you need JDK 10, RedHat's OpenJDK or Oracle's JDK should both work fine, 
however OpenJDK is what I will be using and what is used for Semaphore Builds. 
Optionally you may also want to install Intellij IDEA Community.

The easiest way to build is using `./gradlew fatJar` in either PowerShell (Windows), or in the
terminal (Linux/Mac).

You could alternatively use Intellij and select fatJar from `Gradle Tasks->Other->fatJar`, and once
you've done this step you can simply build by pressing the green run arrow at the top.

The build name is fatJar because well, the jar is 'fat'. Which means all dependencies will be packed
into it in order to make it easily portable.

### Running
You can run it from the terminal/PowerShell by typing `java -jar Officer-Beepsky-x.x.x.jar <Discord token> <Owner ID>`.

### Build, run, and update script
Optionally you can use the following script to build, run, and update the bot when needed.

**Linux/Mac:**
```
curl https://raw.githubusercontent.com/CorruptComputer/Officer-Beepsky/master/run.sh -o run.sh
bash run.sh
```

**Windows:** (PowerShell)
```
Invoke-WebRequest https://raw.githubusercontent.com/CorruptComputer/Officer-Beepsky/master/run.ps1 -OutFile run.ps1
./run.ps1
```

And when its time to update just private message the bot `restart` and it will update and restart!
**WARNING: The `restart` command will only shut it down if you do not use the script.**

### Contributing
This project follows the [Google Java Style Guide](http://google.github.io/styleguide/javaguide.html).
You can add the style to your Intellij by using [this](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml)
and [this guide](https://www.jetbrains.com/help/idea/code-style.html).

You can check if your code is good by using the `checkstyleMain` gradle task:
```
./gradlew checkstyleMain
```

The generated JavaDocs can be found [here](https://nickolas.gupton.xyz/Officer-Beepsky/). 