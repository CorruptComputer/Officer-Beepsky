$branch = "master"
if($args){
    if($($args[0]) -eq "-d"){
        $branch = "dev"
    }else{
        Write-Output "Usage: .\run.ps1 [-h <for help>] [-d <for dev branch>]"
        exit 0
    }
}

Remove-Item .\Officer-Beepsky -Force -Recurse
git clone -b $branch https://github.com/CorruptComputer/Officer-Beepsky
Set-Location Officer-Beepsky
$token = Read-Host -Prompt 'What is the Discord token? '
$owner = Read-Host -Prompt 'What is the Discord ID of the owner? '

for($i = 1; $i -gt 0; $i = $LASTEXITCODE){
    git pull origin $branch
    ./gradlew.bat fatJar
    $file = Resolve-Path 'build/libs/Officer-Beepsky-*.jar' | Select-Object -ExpandProperty Path
    java -jar $file $token $owner
}
