# run-app.ps1 — build, install, launch and tail logs (Windows PowerShell)
# Run from repository root with the phone connected via USB (USB debugging enabled).

$adb = "C:\Users\gskou\AppData\Local\Android\Sdk\platform-tools\adb.exe"

Write-Host "Restarting ADB Server to clear stale connections..."
& $adb kill-server
& $adb start-server

Write-Host "Checking adb devices..."
$devicesOutput = & $adb devices
$devicesOutput

# Extract connected devices
$devices = $devicesOutput | Where-Object { $_ -match "`tdevice$" } | ForEach-Object { ($_ -split "`t")[0] }

if ($devices.Count -eq 0) {
    Write-Error "No devices found. Please connect a device or start an emulator."
    exit 1
}

$targetDevice = $devices[0]
if ($devices.Count -gt 1) {
    Write-Host "`nMultiple devices detected:"
    for ($i = 0; $i -lt $devices.Count; $i++) {
        Write-Host "[$($i + 1)] $($devices[$i])"
    }
    $choice = Read-Host "`nEnter the number of the device you want to run the app on (Default is 1)"
    if ([int]$choice -gt 0 -and [int]$choice -le $devices.Count) {
        $targetDevice = $devices[[int]$choice - 1]
    }
}

Write-Host "`nTargeting device: $targetDevice"
$env:ANDROID_SERIAL = $targetDevice

Write-Host "Building & installing debug APK..."
$gradle = Join-Path -Path $PWD -ChildPath ".\gradlew.bat"
if (-not (Test-Path $gradle)) {
    Write-Error "gradlew.bat not found in project root"
    exit 1
}

& $gradle installDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "Gradle install failed, trying clean + install..."
    & $gradle clean
    & $gradle installDebug
    if ($LASTEXITCODE -ne 0) { Write-Error "Gradle install failed after clean"; exit $LASTEXITCODE }
}

Write-Host "Launching app..."
& $adb shell am start -n com.raktavahini.app/.MainActivity
if ($LASTEXITCODE -ne 0) { Write-Host "Launch command returned non-zero" }

Write-Host "Clearing logcat and starting live logs (Ctrl+C to stop)..."
& $adb logcat -c
& $adb logcat
