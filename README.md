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

**Viktigt om APK-signering:** Alla release APK:er som byggs med detta projekt kommer alltid att vara signerade och installerbara. Om du inte konfigurerar en egen signeringsnyckel kommer APK:n att signeras med debug-nyckeln, vilket gör den installerbar men inte lämplig för Google Play Store-distribution.

För att bygga en release APK med din egen signeringsnyckel kan du använda följande kommando:

```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=/path/to/your/keystore.jks \
  -Pandroid.injected.signing.store.password=your_store_password \
  -Pandroid.injected.signing.key.alias=your_key_alias \
  -Pandroid.injected.signing.key.password=your_key_password
```

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

### "APK:n går inte att installera på mobilen"

Om du får ett felmeddelande när du försöker installera en release APK kan det bero på att APK:n inte är korrekt signerad. Detta projekt är konfigurerat att alltid signera release APK:er:

- **Med egen signeringsnyckel**: Om du bygger med flaggor för signering (se avsnittet "Bygga Release APK") kommer APK:n att signeras med din egen nyckel.
- **Utan egen signeringsnyckel**: Om du bara kör `./gradlew assembleRelease` utan signeringsflaggor kommer APK:n automatiskt att signeras med debug-nyckeln, vilket gör den installerbar på alla enheter men inte lämplig för Play Store.

**Lösning**: Om du fortfarande har problem med installation:
1. Kontrollera att "Installera okända appar" är aktiverat för den app du använder för att installera (t.ex. Filer-appen eller Chrome)
2. Om APK:n tidigare var installerad med en annan signering, avinstallera den gamla versionen först
3. Bygg en ny APK med `./gradlew clean assembleRelease`

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
