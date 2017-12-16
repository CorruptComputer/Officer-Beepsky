git clone https://github.com/CorruptComputer/Officer-Beepsky

cd Officer-Beepsky

echo "What is the Discord token? "
read token

echo "What is the Discord ID of the owner? "
read owner

for((i = 1; i > 0; i=$?)){
    rm -rf build
    git pull
    chmod +x gradlew
    ./gradlew fatJar
    java -jar build/libs/Officer-Beepsky-*.jar "$token" "$owner"
}

exit 0