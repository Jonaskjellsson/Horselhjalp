# Hörselhjälp

En Android-applikation för hörselhjälp.

## Förutsättningar

- **Java Development Kit (JDK)**: Version 17 eller senare
- **Android SDK**: Installeras automatiskt av Android Studio
- **Android Studio**: Rekommenderas för utveckling (valfritt)

## Bygga APK

Det finns flera sätt att bygga en APK från detta projekt:

### 1. Bygga Debug APK (för testning)

För att bygga en debug-version av appen som kan installeras direkt på din enhet:

```bash
./gradlew assembleDebug
```

Den färdiga APK-filen hittar du i:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 2. Bygga Release APK (för distribution)

För att bygga en release-version av appen:

```bash
./gradlew assembleRelease
```

Den färdiga APK-filen hittar du i:
```
app/build/outputs/apk/release/app-release.apk
```

**Obs!** För att ladda upp till Google Play Store behöver APK:n vara signerad med din egen nyckel.

### 3. Bygga AAB (Android App Bundle)

Android App Bundle är det rekommenderade formatet för Google Play Store:

```bash
./gradlew bundleRelease
```

Den färdiga AAB-filen hittar du i:
```
app/build/outputs/bundle/release/app-release.aab
```

### 4. Rensa byggfiler

Om du vill börja om från början och rensa alla genererade filer:

```bash
./gradlew clean
```

Du kan kombinera clean med build-kommandot:

```bash
./gradlew clean assembleRelease
```

## Installation av APK

### På Android-enhet via USB

1. Aktivera "Utvecklarläge" på din Android-enhet
2. Aktivera "USB-felsökning" i utvecklarinställningarna
3. Anslut enheten via USB
4. Kör kommandot:
```bash
./gradlew installDebug
```

### Manuell installation

1. Överför APK-filen till din Android-enhet
2. Öppna filen på enheten
3. Tillåt installation från okända källor om du blir ombedd
4. Följ installationsinstruktionerna

## Användbara Gradle-kommandon

- `./gradlew tasks` - Visa alla tillgängliga tasks
- `./gradlew assembleDebug` - Bygg debug APK
- `./gradlew assembleRelease` - Bygg release APK
- `./gradlew bundleRelease` - Bygg release AAB
- `./gradlew installDebug` - Installera debug-appen på ansluten enhet
- `./gradlew clean` - Rensa alla byggfiler

## Projektstruktur

```
Horselhjalp/
├── app/                    # Huvudapplikationen
│   ├── src/               # Källkod
│   │   ├── main/         # Main source set
│   │   └── test/         # Enhetstester
│   ├── build.gradle.kts  # App-nivå Gradle-konfiguration
│   └── release/          # Release-byggen (om de finns)
├── gradle/               # Gradle wrapper-filer
├── build.gradle.kts     # Projekt-nivå Gradle-konfiguration
├── settings.gradle.kts  # Gradle-inställningar
└── gradlew              # Gradle wrapper script (Unix/Mac)
```

## Utveckling

### Med Android Studio

1. Öppna Android Studio
2. Välj "Open an existing project"
3. Navigera till projektmappen och välj den
4. Vänta tills Gradle har synkroniserat
5. Kör appen genom att klicka på gröna play-knappen

### Från kommandoraden

1. Navigera till projektmappen
2. Använd Gradle-kommandona ovan för att bygga och installera

## Felsökning

### "Permission denied" när du kör ./gradlew

Om du får ett felmeddelande om att tillstånd nekas:

```bash
chmod +x gradlew
```

### Gradle-synkronisering misslyckas

Om Gradle inte kan synkronisera korrekt:

1. Kontrollera din internetanslutning
2. Försök rensa Gradle-cachen:
```bash
./gradlew clean --refresh-dependencies
```

## Licens

[Lägg till licensinformation här]

## Kontakt

[Lägg till kontaktinformation här]
