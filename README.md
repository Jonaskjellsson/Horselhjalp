# Hörselhjälp - Svenskt Tal till Text

En enkel och tillgänglig Android-app för personer med hörselnedsättning. Appen omvandlar tal till text i realtid.

## 🎯 Vem är appen för?

Denna app är speciellt utformad för:
- Personer med hörselnedsättning
- Personer med synnedsättning
- Alla som behöver textning av tal

Appen använder Androids inbyggda taligenkänning för att omvandla tal till text med stora, lättlästa bokstäver.

## ✨ Funktioner

- **Tal till text i realtid**: Omvandlar svenskt eller engelskt tal till text direkt
- **Stora bokstäver**: Text visas i stora storlekar (32sp) för bättre läsbarhet
- **Hög kontrast**: Svart text på vit bakgrund ger optimal läsbarhet
- **Enkla knappar**: Stora knappar med tydliga symboler
- **Tillgänglig**: Fungerar med Android TalkBack för synskadade
- **Fungerar offline**: Använder enhetens inbyggda taligenkänning (ingen internetanslutning krävs efter första användningen)

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

## 🎮 Så här använder du appen

1. **Starta appen** - Öppna Hörselhjälp på din Android-enhet
2. **Ge tillstånd** - Tillåt appen att använda mikrofonen (första gången du startar)
3. **Tryck på "🎤 STARTA TAL"** - Börja tala på svenska eller engelska
4. **Se texten** - Din talade text visas direkt på skärmen med stora bokstäver
5. **Tryck på "🛑 STOPPA"** - När du vill pausa inspelningen
6. **Tryck på "🗑️ RENSA TEXT"** - För att rensa hela skärmen
7. **Tryck på "🔄 LÄGEVÄXLING"** - För att byta mellan ljust och mörkt läge
8. **Tryck på "🌐 SPRÅK"** - För att växla mellan svenska och engelska

## 🏗️ Teknisk information

### Teknologi

- **Kotlin**: Modern Android-utveckling
- **Android SpeechRecognizer API**: Använder enhetens inbyggda taligenkänning
- **Material Design 3**: Modernt och tillgängligt gränssnitt
- **Språkstöd**: Svenska (sv-SE) och Engelska (en-US)

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

Appen är utformad för maximal tillgänglighet:

- **Stora textstorlekar**: 32sp för huvudtext, 26sp för status
- **Hög kontrast**: Svart text på vit bakgrund (ljust läge)
- **Mörkt läge**: Vit text på svart bakgrund för ögonkomfort
- **TalkBack-stöd**: Alla element har beskrivningar för skärmläsare
- **Stora knappar**: 50dp höjd för enkel tryckning
- **Tydliga symboler**: Alla knappar har emoji-ikoner för visuell vägledning
- **Tydlig feedback**: Statusmeddelanden visar vad som händer

## 🔧 Felsökning

### Problem: "Taligenkänning är inte tillgänglig"

**Lösning:**
1. Kontrollera att din Android-enhet har Google-appen installerad
2. Kontrollera att svenska språket är installerat i Google-appen
3. Gå till: **Inställningar → Appar → Google → Behörigheter** och aktivera mikrofon

### Problem: "Mikrofon-tillstånd krävs"

**Lösning:**
Appen behöver tillgång till mikrofonen för att fungera:
1. Gå till: **Inställningar → Appar → Hörselhjälp**
2. Tryck på **Behörigheter**
3. Aktivera **Mikrofon**

### Problem: Taligenkänningen fungerar inte

**Möjliga lösningar:**
1. Kontrollera att du har internetanslutning (kan krävas första gången)
2. Tala tydligt och inte för snabbt
3. Kontrollera att det inte är för mycket bakgrundsljud
4. Håll telefonen närmare din mun
5. Starta om appen

## 📄 Licens

Detta är ett open source-projekt för att hjälpa personer med hörsel- eller synnedsättning.

## 👤 Kontakt

För frågor eller problem, öppna ett issue på GitHub.

---

**Utvecklad med ❤️ för tillgänglighet**
