param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$propertiesPath = Join-Path $scriptDir ".mvn/wrapper/maven-wrapper.properties"

if (-not (Test-Path -LiteralPath $propertiesPath)) {
    throw "Missing wrapper config: $propertiesPath"
}

$properties = Get-Content -Raw -LiteralPath $propertiesPath | ConvertFrom-StringData
$distributionUrl = $properties.distributionUrl

if (-not $distributionUrl) {
    throw "Cannot read distributionUrl from $propertiesPath"
}

$javaHome = $env:JAVA_HOME
if ($javaHome) {
    $javacPath = Join-Path $javaHome "bin/javac.exe"
    if (-not (Test-Path -LiteralPath $javacPath)) {
        throw "JAVA_HOME is set to '$javaHome', but javac.exe was not found. Install a full JDK 17 and point JAVA_HOME to it."
    }
} elseif (-not (Get-Command javac.exe -ErrorAction SilentlyContinue)) {
    throw "javac.exe was not found in PATH. Install a full JDK 17 and make sure JAVA_HOME and PATH point to it."
}

$mavenUserHome = if ($env:MAVEN_USER_HOME) { $env:MAVEN_USER_HOME } else { Join-Path $HOME ".m2" }
$cacheRoot = Join-Path $mavenUserHome "wrapper/dists"
$distributionName = Split-Path $distributionUrl -Leaf
$distributionBaseName = $distributionName -replace '\.zip$', ''
$distributionHash = [System.BitConverter]::ToString(
    [System.Security.Cryptography.SHA256]::Create().ComputeHash(
        [System.Text.Encoding]::UTF8.GetBytes($distributionUrl)
    )
) -replace '-', ''

$mavenHome = Join-Path (Join-Path $cacheRoot $distributionBaseName) $distributionHash
$mvnCmd = Join-Path $mavenHome "bin/mvn.cmd"

if (-not (Test-Path -LiteralPath $mvnCmd)) {
    New-Item -ItemType Directory -Force -Path $mavenHome | Out-Null

    $tempDir = Join-Path ([System.IO.Path]::GetTempPath()) ("mvnw-" + [Guid]::NewGuid().ToString("N"))
    New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

    try {
        $zipPath = Join-Path $tempDir $distributionName
        Invoke-WebRequest -Uri $distributionUrl -OutFile $zipPath
        Expand-Archive -Path $zipPath -DestinationPath $tempDir -Force

        $extracted = Get-ChildItem -LiteralPath $tempDir -Directory |
            Where-Object { $_.Name -like "apache-maven*" } |
            Select-Object -First 1

        if (-not $extracted) {
            throw "Failed to unpack Maven distribution from $distributionUrl"
        }

        Copy-Item -Path (Join-Path $extracted.FullName '*') -Destination $mavenHome -Recurse -Force
    } finally {
        Remove-Item -LiteralPath $tempDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

& $mvnCmd @MavenArgs
exit $LASTEXITCODE
