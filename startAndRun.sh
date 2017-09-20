git clone https://github.com/CorruptComputer/PolizziaHut

cd PolizziaHut

echo "What is the Discord token? "
read token

echo "What is the Discord ID of the owner? "
read owner

while [true]; do
    chmod -X gradlew
    ./gradlew fatJar
    java -jar build/libs/PolizziaHut-*.jar "$token" "$owner"

    rm -rf build
    git pull
done