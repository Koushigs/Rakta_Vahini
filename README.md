# Rakta Vahini — Demo build

This repository contains the Rakta Vahini Android app. A permanent demo APK is published to the `demo-latest` prerelease so you can share a single download link.

- Permanent demo APK: https://github.com/Koushigs/Rakta_Vahini/releases/download/demo-latest/demo-latest.apk
- Friendly demo page: https://Koushigs.github.io/Rakta_Vahini/

Quick install (device or emulator)

1. Download `demo-latest.apk` from the link above, or build locally:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open the app after installing it:

```text
raktavahini://open
```

The friendly demo page above is deployed by GitHub Pages from the `docs/` folder using GitHub Actions.

To enable it in GitHub:
1. Open the repository on GitHub.
2. Go to `Settings` -> `Pages`.
3. Set the source to `GitHub Actions`.
4. Push to `main` and wait for the `Deploy GitHub Pages` workflow to finish.

2. On first run grant the app the runtime permission to read call logs if you want to view call logs.

Notes
- This APK is a debug/demo build for demonstration only.
- To publish to Play Store or produce a signed release, provide a keystore and Play Store service account credentials and configure the CI (secrets required). See the TODOs in the repo for next steps.

If you want, I can also add a short badge or GitHub Pages redirect to make the link friendlier.
