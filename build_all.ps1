$dirs = Get-ChildItem -Path ".\versiones" -Directory
foreach ($dir in $dirs) {
    if ($dir.Name -match "^([AB]\d)-(.*)$") {
        $version = $matches[2]
        $jarName = "ji-zoom-cinematic-$version-1.0.0.jar"
        Write-Host "Compiling $($dir.Name)..."
        Push-Location $dir.FullName
        .\gradlew.bat build
        if ($LASTEXITCODE -eq 0) {
            $compiledJar = Get-ChildItem "build\libs" -Filter "*.jar" | Where-Object { $_.Name -notmatch "sources|dev" } | Select-Object -First 1
            if ($compiledJar) {
                Copy-Item $compiledJar.FullName "c:\xampp\htdocs\Ji-Zoom-Cinematic\compilaciones\$jarName" -Force
                Write-Host "Copied $jarName"
            }
        } else {
            Write-Host "Failed to compile $($dir.Name)"
        }
        Pop-Location
    }
}
