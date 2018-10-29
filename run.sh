#!/bin/bash

rm -rf Officer-Beepsky
git clone -b master https://github.com/CorruptComputer/Officer-Beepsky
cd Officer-Beepsky

if [ -z "$TOKEN" ]; then
    echo "What is the Discord Token? "
    read TOKEN
    echo "Optionally you could also set the TOKEN var to skip this in the future."
fi

if [ -z "$OWNER" ]; then
    echo "What is the Discord ID of the owner? "
    read OWNER
    echo "Optionally you could also set the OWNER var to skip this in the future."
fi

for ((i = 1; i > 0; i=$?)); do
    git pull origin master;
    chmod +x gradlew;
    ./gradlew fatJar;
    java -jar build/libs/Officer-Beepsky-*.jar "$TOKEN" "$OWNER";
done

exit 0
