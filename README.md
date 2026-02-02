# Hörselhjälp

En Android-applikation för hörselhjälp.

## Snabbstart - Hur bygger jag appen?

### Steg-för-steg: Kör ./gradlew assembleDebug

Följ dessa steg för att köra kommandot och bygga din första APK:

**1. Öppna en terminal/kommandotolk**
   - På Mac/Linux: Öppna Terminal
   - På Windows: Öppna Command Prompt eller PowerShell

**2. Navigera till projektmappen**
   ```bash
   cd /sökväg/till/Horselhjalp
   ```
   (Ersätt `/sökväg/till/` med faktisk sökväg där du klonat/laddat ner projektet)

**3. Ge exekveringsrättigheter (endast första gången på Mac/Linux)**
   ```bash
   chmod +x gradlew
   ```
   På Windows: Detta steg behövs inte, använd `gradlew.bat` istället för `./gradlew`

**4. Kör build-kommandot**
   ```bash
   ./gradlew assembleDebug
   ```
   På Windows:
   ```bash
   gradlew.bat assembleDebug
   ```

**5. Vänta på att byggprocessen slutförs**
   - Första gången kan ta längre tid (laddar ner beroenden)
   - Du kommer se byggstatus i terminalen
   - När det är klart ser du "BUILD SUCCESSFUL"

**6. Hitta din färdiga APK**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

Läs vidare för mer detaljerade instruktioner och alternativa byggmetoder.

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

**Steg 1: Öppna terminal och navigera till projektet**
```bash
cd /sökväg/till/Horselhjalp
```

**Steg 2: Verifiera att du är i rätt mapp**
Du ska se filer som `gradlew`, `build.gradle.kts`, och en `app`-mapp:
```bash
ls -la
```

**Steg 3: Kör Gradle-kommandon**
Använd någon av följande kommandon beroende på vad du vill göra:

- **Bygga debug APK**: `./gradlew assembleDebug`
- **Bygga release APK**: `./gradlew assembleRelease`
- **Installera på enhet**: `./gradlew installDebug`
- **Rensa byggfiler**: `./gradlew clean`

**Windows-användare**: Använd `gradlew.bat` istället för `./gradlew`:
```bash
gradlew.bat assembleDebug
```

**Vad händer när du kör kommandot?**
1. Gradle laddar ner nödvändiga beroenden (första gången)
2. Kompilerar din kod
3. Paketerar allt till en APK-fil
4. Sparar APK:n i `app/build/outputs/apk/`

## Felsökning

### "Plugin [id: 'com.android.application', version: 'X.X.X'] was not found"

Om du får detta fel vid byggning kan det bero på att versionsnumret i `gradle/libs.versions.toml` inte är tillgängligt ännu eller att det finns ett nätverksproblem.

**Lösning**: Använd en stabil version av Android Gradle Plugin. Öppna filen `gradle/libs.versions.toml` och ändra raden:
```toml
agp = "8.7.3"
```
till en stabil version som finns tillgänglig, t.ex.:
```toml
agp = "8.2.0"
```

Du kan hitta tillgängliga versioner på: https://developer.android.com/studio/releases/gradle-plugin

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
