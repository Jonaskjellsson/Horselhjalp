# Play Store Assets - Hörselhjälp

Denna mapp innehåller alla nödvändiga resurser för Google Play Store-uppgifter (Standardbutiksuppgifter).

## 📂 Innehåll

### 1. App-ikon (`icon/`)
✅ **app-icon-512x512.png** - Huvudappikon för Play Store
- Format: PNG
- Storlek: 512x512 pixlar
- Använd i Play Console under "Appikon" i Butiksuppgifter

### 2. Skärmdumpar (`screenshots/`)
Denna sektion innehåller instruktioner för att ta nödvändiga skärmdumpar av appen.

## 📱 Skärmdumpar som behövs för Play Store

Google Play Store kräver minst **2 skärmdumpar** och tillåter upp till **8 skärmdumpar** per enhetstyp.

### Rekommenderade skärmdumpar:

#### 1. **Startskärm** (huvudskärm)
- **Beskrivning**: Visar appens huvudgränssnitt med placeholder-text
- **Vad som ska synas**:
  - Appens namn/header
  - Den stora textrutan med texten "Din talade text kommer att visas här..."
  - Tre knappar längst ner:
    - 🎤 STARTA TAL (grön)
    - 🗑️ RENSA TEXT (orange)
    - 🔄 MÖRKT LÄGE (blå)
  - Språkknapp 🌐 SPRÅK i övre högra hörnet

#### 2. **Under taligenkänning**
- **Beskrivning**: Visar appen när den lyssnar på tal
- **Vad som ska synas**:
  - Mikrofon-knappen har ändrats till "🛑 STOPPA" (röd)
  - Textrutan kan vara tom eller visa "Lyssnar..." 
  - Statusindikator kan visa "Lyssnar…"

#### 3. **Med igenkänd text**
- **Beskrivning**: Visar appen med svensk text som har igenkänts
- **Vad som ska synas**:
  - Textrutan fylld med exempel på svensk text, t.ex.:
    - "Hej och välkommen till Hörselhjälp"
    - "Detta är en app för att omvandla tal till text"
    - "Den fungerar med svenska språket"
  - Text visas i stor, lättläst storlek (32sp)
  - Knapparna i normalt läge

#### 4. **Mörkt läge** (valfritt men rekommenderat)
- **Beskrivning**: Visar appen i mörkt läge
- **Vad som ska synas**:
  - Samma layout som startskärmen
  - Mörk bakgrund (#1A1C1E)
  - Ljus text (#E2E2E6)
  - Visar att appen har tillgänglighetsfunktioner

#### 5. **Med mycket text** (valfritt)
- **Beskrivning**: Visar att appen kan hantera långa textflöden
- **Vad som ska synas**:
  - Textrutan fylld med flera meningar/paragrafer
  - Visar att texten är scrollbar
  - Demonstrerar radavstånd och läsbarhet

## 🎯 Krav för skärmdumpar på Google Play Store

### Tekniska specifikationer:
- **Format**: PNG eller JPEG
- **Minsta upplösning**: 
  - Telefon: 320 pixlar
  - 7-tums surfplatta: 320 pixlar
  - 10-tums surfplatta: 1080 pixlar
- **Högsta upplösning**: 3840 pixlar
- **Förhållande**: Minst 2:1 (höjd:bredd eller bredd:höjd)

### Rekommenderade storlekar för telefon:
- 1080 x 1920 pixlar (Full HD)
- 1440 x 2560 pixlar (2K)
- 1440 x 3040 pixlar (modern 19:9)

## 📝 Hur man tar skärmdumpar

### Metod 1: På Android-enhet
1. Installera APK:n på din Android-enhet
2. Öppna appen
3. Ta skärmdumpar med enhetens skärmdumpsfunktion:
   - På de flesta enheter: Håll in Ström + Volym ned samtidigt
   - Eller använd skärmdumpsknappen i snabbinställningar

### Metod 2: Med Android Studio Emulator
1. Öppna projektet i Android Studio
2. Starta emulatorn (välj en modern enhet som Pixel 6)
3. Installera och kör appen
4. Ta skärmdumpar med emulatorns kameraikon (högersidans verktygsrad)
5. Skärmdumparna sparas automatiskt

### Metod 3: Med ADB (Android Debug Bridge)
```bash
# Ta skärmdump
adb shell screencap -p /sdcard/screenshot.png

# Ladda ner skärmdumpen till datorn
adb pull /sdcard/screenshot.png ./play-store-assets/screenshots/
```

## 📋 Checklist för Play Store-publicering

- [x] App-ikon (512x512 PNG) finns i `icon/`
- [ ] Minst 2 skärmdumpar tagna och placerade i `screenshots/`
- [ ] Skärmdumpar visar appens huvudfunktioner
- [ ] Skärmdumpar är i rätt format (PNG/JPEG)
- [ ] Skärmdumpar har rätt upplösning (minst 320px)
- [ ] Eventuellt mörkt läge-skärmdump för tillgänglighet
- [ ] Alla skärmdumpar är utan personlig information

## 🎨 Design-information

### Färgschema (Ljust läge):
- Bakgrund: #F4F6F8 (ljusgrå)
- Text: #1C2730 (mörkgrå/svart)
- Textruta: #E5E9EF (ljusare grå)
- Start-knapp: #4CAF50 (grön)
- Stopp-knapp: #F44336 (röd)
- Rensa-knapp: #FF9800 (orange)
- Tema-knapp: #0749A8 (blå)

### Färgschema (Mörkt läge):
- Bakgrund: #1A1C1E (mörk)
- Text: #E2E2E6 (ljus)
- Knappar: Samma som ljust läge

### Typografi:
- Text i textruta: 32sp
- Knappar: 14sp bold
- Hög läsbarhet med 1.5x radavstånd

## 📞 Support

För frågor om Play Store-publicering, se Google Play Console dokumentation:
https://support.google.com/googleplay/android-developer/answer/9866151

---

**Senast uppdaterad**: 2026-02-06
