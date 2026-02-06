# AAB (Android App Bundle) Implementation

## Översikt

Detta dokument beskriver implementationen av stöd för AAB (Android App Bundle) i Hörselhjälp-projektet.

## Vad är AAB?

Android App Bundle (AAB) är Googles rekommenderade publiceringsformat för Android-appar. AAB är nu obligatoriskt för nya appar på Google Play Store sedan augusti 2021.

### Fördelar med AAB

- **Mindre nedladdningsstorlek**: Användare laddar endast ner kod och resurser för sin specifika enhet
- **Optimerad distribution**: Google Play genererar automatiskt optimerade APK:er för varje enhetstyp
- **Dynamiska funktioner**: Möjlighet att ladda ner funktioner on-demand
- **Krav för nya appar**: Google Play kräver AAB för alla nya appar sedan augusti 2021

### Skillnad mellan AAB och APK

| Aspekt | APK | AAB |
|--------|-----|-----|
| Direktinstallation | ✓ Ja | ✗ Nej (kräver Google Play eller bundletool) |
| Distribution via Google Play | ✓ Ja | ✓ Ja (rekommenderat) |
| Filstorlek | Större | Mindre för slutanvändare |
| Optimering per enhet | Manuell | Automatisk |

## Implementerade Ändringar

### 1. Nytt Byggskript: `build-and-find-aab.sh`

Ett nytt skript som bygger release AAB och visar var filen finns:

```bash
./build-and-find-aab.sh
```

Skriptet:
- Rensar gamla byggfiler
- Bygger release AAB med `./gradlew bundleRelease`
- Visar filens plats och storlek
- Ger instruktioner för nästa steg

### 2. Uppdaterat Skript: `find-apk.sh`

Det befintliga skriptet har uppdaterats för att även visa AAB-filer:
- Visar status för debug APK, release APK, och release AAB
- Inkluderar filstorlekar och sökvägar
- Ger instruktioner för att bygga saknade filer

### 3. GitHub Actions: `android-build.yml`

Workflow uppdaterad för att bygga AAB vid varje push:

**Nya steg:**
- `Build signed release AAB` - Bygger signerad AAB när signeringscertifikat finns
- `Build release AAB with debug signing` - Bygger AAB med debug-signering annars
- `Upload release AAB artifact` - Laddar upp AAB som artifact

### 4. GitHub Actions: `create-release.yml`

Workflow uppdaterad för att inkludera AAB i releases:

**Ändringar:**
- Bygger både APK och AAB vid release
- Inkluderar AAB-filer i release assets
- Uppdaterad release-beskrivning med AAB-information
- Instruktioner för både APK och AAB

### 5. Uppdaterad README

README har uppdaterats med:
- Information om AAB i snabbstartsektionen
- Instruktioner för att bygga AAB lokalt
- Förklaring av skillnaden mellan AAB och APK
- När man ska använda AAB vs APK

## Användning

### Bygg AAB Lokalt

```bash
# Använd byggskriptet (rekommenderat)
./build-and-find-aab.sh

# Eller kör Gradle direkt
./gradlew bundleRelease
```

AAB-filen skapas i: `app/build/outputs/bundle/release/app-release.aab`

### Kontrollera Befintliga Filer

```bash
./find-apk.sh
```

Detta visar status för alla byggda filer (debug APK, release APK, release AAB).

### Ladda Ner från GitHub

**Från Actions:**
1. Gå till GitHub Actions
2. Välj senaste lyckade build
3. Ladda ner `app-release-aab` artifact

**Från Releases:**
1. Gå till GitHub Releases
2. Välj senaste version
3. Ladda ner `app-release.aab` från Assets

## Teknisk Information

### Gradle-konfiguration

AAB-byggnation fungerar automatiskt med Android Gradle Plugin:
- Använder samma signing config som APK
- Task: `bundleRelease`
- Output: `app/build/outputs/bundle/release/app-release.aab`

### Signering

AAB-filer signeras på samma sätt som APK:
- Med proper keystore när tillgänglig (produktion)
- Med debug keystore som fallback (utveckling)

### CI/CD-integration

GitHub Actions bygger automatiskt AAB:
- Vid varje push till main/master
- Vid pull requests
- Vid manuell trigger
- Vid release-taggning

## Nästa Steg

### För Utvecklare

1. **Lokal testning**: Använd bundletool för att testa AAB lokalt
2. **Signering**: Konfigurera proper signing för produktionsbyggen

### För Distribution

1. **Google Play**: Ladda upp AAB till Google Play Console
2. **Dokumentation**: Uppdatera användarguider om distribution

### För Underhåll

1. **Automatisering**: Överväg automatisk publicering till Google Play
2. **Versionshantering**: Implementera automatisk versionsnumrering
3. **Changelog**: Automatisk generering av ändringsloggar för releases

## Resurser

- [Android App Bundle Översikt](https://developer.android.com/guide/app-bundle)
- [Build and Test an App Bundle](https://developer.android.com/studio/build/building-cmdline#build_bundle)
- [bundletool Documentation](https://developer.android.com/tools/bundletool)
- [Google Play Console Help](https://support.google.com/googleplay/android-developer)

## Support

För frågor eller problem relaterade till AAB-implementationen, öppna ett issue på GitHub.
