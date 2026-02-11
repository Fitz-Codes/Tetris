# Tetris Build Script
# This script compiles the Java files and copies resources to the bin directory

$projectRoot = "C:\Users\micha\OneDrive\Personal Coding Projects\Tetris"
$binDir = "$projectRoot\bin"

# Create bin directory if it doesn't exist
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

# Compile Java files
Write-Host "Compiling Java files..."
& 'C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot\bin\javac.exe' -d $binDir "$projectRoot\main\*.java" "$projectRoot\mino\*.java"

# Copy audio resources
Write-Host "Copying audio resources..."
Copy-Item "$projectRoot\SOURCES\*.wav" -Destination $binDir

Write-Host "Build complete!"
Write-Host "Run with: java -cp bin main.main"
