Remove-Item .\Officer-Beepsky -Force -Recurse
git clone -b master https://github.com/CorruptComputer/Officer-Beepsky
Set-Location Officer-Beepsky
$token = Read-Host -Prompt 'What is the Discord token? '
$owner = Read-Host -Prompt 'What is the Discord ID of the owner? '

for($i = 1; $i -gt 0; $i = $LASTEXITCODE){
   git pull origin master
   ./gradlew.bat jar
   $file = Resolve-Path 'build/libs/Officer-Beepsky-*.jar' | Select-Object -ExpandProperty Path
   java -jar $file $token $owner
}
