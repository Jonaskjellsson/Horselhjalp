# Hörselhjälp - Svenskt Tal till Text

En enkel och tillgänglig Android-applikation för hörselskadade och synskadade som omvandlar svenskt tal till text i realtid.

## 🎯 Syfte

Denna app är speciellt utformad för personer med:
- Hörselnedsättning
- Synnedsättning
- Personer som behöver textning av tal

Appen använder Androids inbyggda taligenkänning för att omvandla svenskt tal till text som visas med stora, lättlästa bokstäver på skärmen.

## ✨ Funktioner

- **Svenskt tal till text**: Omvandlar svenska tal till text i realtid
- **Stora, lättlästa bokstäver**: Text visas i stora storlekar för bättre läsbarhet
- **Hög kontrast**: Svart text på vit bakgrund för optimal läsbarhet
- **Enkel användning**: Stora knappar och tydlig design
- **Tillgänglig**: Fungerar med Android TalkBack för synskadade
- **Ingen internetanslutning krävs**: Använder enhetens taligenkänning

## 📱 Snabbstart - Ladda ner färdig APK

### Alternativ 1: Ladda ner från GitHub Releases (Rekommenderat)

1. Gå till [Releases](https://github.com/Jonaskjellsson/Horselhjalp/releases)
2. Välj den senaste versionen
3. Under "Assets", ladda ner `app-release.apk`
4. Överför APK-filen till din Android-enhet och installera den

### Alternativ 2: Ladda ner från GitHub Actions

1. Gå till [Actions](https://github.com/Jonaskjellsson/Horselhjalp/actions)
2. Klicka på den senaste lyckade "Build Android APK" körningen
3. Scrolla ner till "Artifacts" och ladda ner `app-release-apk`
4. Packa upp ZIP-filen och installera APK:n på din Android-enhet

## 🔨 Bygg APK själv

### Förutsättningar

- **Java Development Kit (JDK)**: Version 17 eller senare
- **Android SDK**: Installeras automatiskt av Android Studio

### Bygga Debug APK

```bash
./gradlew assembleDebug
```

APK-filen finns sedan i: `app/build/outputs/apk/debug/app-debug.apk`

### Bygga Release APK

```bash
./gradlew assembleRelease
```

APK-filen finns sedan i: `app/build/outputs/apk/release/app-release.apk`

### Hjälpskript

Kolla om APK redan finns:
```bash
./find-apk.sh
```

Bygg och hitta APK automatiskt:
```bash
./build-and-find-apk.sh
```

## 🎮 Användning

1. **Starta appen** - Öppna Hörselhjälp på din Android-enhet
2. **Ge tillstånd** - Tillåt appen att använda mikrofonen (första gången)
3. **Tryck på "STARTA TAL"** - Börja tala på svenska
4. **Se texten** - Din talade text visas direkt på skärmen
5. **Tryck på "STOPPA"** - När du vill pausa inspelningen
6. **Tryck på "RENSA TEXT"** - För att rensa skärmen

## 🏗️ Teknisk information

### Arkitektur

- **Native Kotlin implementation**: Ingen WebView, endast ren Android-kod
- **Android SpeechRecognizer API**: Använder enhetens inbyggda taligenkänning
- **Material Design**: Enkelt och tillgängligt gränssnitt
- **Svenska språket**: Konfigurerad för svensk taligenkänning (sv-SE)

### Projektstruktur

```
Hörselhjälp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/se/jonas/horselhjalp/
│   │       │   └── MainActivity.kt          # Huvudlogik
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml    # UI-layout
│   │       │   ├── values/
│   │       │   │   ├── colors.xml           # Färgdefinitioner
│   │       │   │   └── strings.xml          # Textresurser
│   │       │   └── ...
│   │       └── AndroidManifest.xml          # App-manifest
│   └── build.gradle.kts                     # Build-konfiguration
├── build.gradle.kts                         # Projektinställningar
└── settings.gradle.kts                      # Gradle-inställningar
```

## ♿ Tillgänglighet

Appen är utformad för att vara maximalt tillgänglig:

- **Stora textstorlekar**: 24sp-32sp för optimal läsbarhet
- **Hög kontrast**: Svart text på vit bakgrund
- **TalkBack-kompatibel**: Alla element har contentDescription
- **Stora knappar**: 60-80dp höjd för enkel tryckning
- **Tydlig feedback**: Statusmeddelanden visar vad som händer

## 🔧 Felsökning

### "Taligenkänning är inte tillgänglig"

Om du får detta meddelande:
1. Kontrollera att din Android-enhet har Google-appen installerad
2. Kontrollera att svenska språket är installerat i Google-appen
3. Gå till Inställningar → Appar → Google → Behörigheter och aktivera mikrofon

### "Mikrofon-tillstånd krävs"

Appen behöver tillgång till mikrofonen för att fungera:
1. Gå till Inställningar → Appar → Hörselhjälp
2. Tryck på Behörigheter
3. Aktivera Mikrofon

### Taligenkänningen fungerar inte

1. Kontrollera att du har internetanslutning (vissa enheter kräver det första gången)
2. Tala tydligt och inte för snabbt
3. Kontrollera att det inte är för mycket bakgrundsljud
4. Starta om appen

## 📄 Licens

Detta är ett open source-projekt för att hjälpa personer med hörsel- eller synnedsättning.

## 👤 Kontakt

För frågor eller problem, öppna ett issue på GitHub.

---

**Utvecklad med ❤️ för tillgänglighet**
