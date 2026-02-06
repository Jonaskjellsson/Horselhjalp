# Play Store Assets - Complete Guide / Komplett Guide

## 🎯 Vad har skapats / What Has Been Created

Denna PR innehåller alla nödvändiga grafiska resurser för att publicera Hörselhjälp-appen på Google Play Store.

This PR contains all necessary graphic assets to publish the Hörselhjälp app on Google Play Store.

---

## 📦 Innehåll / Contents

### 1. App-ikon / App Icon
**📁 Location:** `play-store-assets/icon/app-icon-512x512.png`

- ✅ Format: PNG
- ✅ Storlek / Size: 512 x 512 pixlar
- ✅ Färgrymd / Color space: RGBA
- ✅ Filstorlek / File size: 132 KB
- ✅ Uppfyller Play Store-krav / Meets Play Store requirements

**Beskrivning / Description:**  
Ikonen visar en hand som håller en smartphone, vilket representerar appens syfte att hjälpa användare via mobil teknik.

The icon shows a hand holding a smartphone, representing the app's purpose of helping users through mobile technology.

### 2. Skärmdumpar / Screenshots
**📁 Location:** `play-store-assets/screenshots/`

Alla skärmdumpar är i Full HD-upplösning (1080 x 1920 pixlar) och uppfyller Play Stores krav.

All screenshots are in Full HD resolution (1080 x 1920 pixels) and meet Play Store requirements.

#### Screenshot 1: Huvudskärm / Main Screen
**File:** `01-main-screen.png` (34 KB)

Visar appens initiala tillstånd med placeholder-text:
- Textruta med: "Din talade text kommer att visas här..."
- Tre knappar: 🎤 STARTA TAL (grön), 🗑️ RENSA (orange), 🔄 MÖRKT (blå)
- Språkväxlare: 🌐 SPRÅK

Shows the app's initial state with placeholder text:
- Text area with: "Your spoken text will be displayed here..."
- Three buttons: 🎤 START SPEECH (green), 🗑️ CLEAR (orange), 🔄 DARK (blue)
- Language switcher: 🌐 LANGUAGE

#### Screenshot 2: Lyssningsläge / Listening Mode
**File:** `02-listening.png` (26 KB)

Visar appen när den aktivt lyssnar på tal:
- Textruta visar: "Lyssnar..."
- Mikrofon-knappen har ändrats till: 🛑 STOPPA (röd)
- Demonstrerar appens aktiva tillstånd

Shows the app while actively listening to speech:
- Text area shows: "Listening..."
- Microphone button changed to: 🛑 STOP (red)
- Demonstrates the app's active state

#### Screenshot 3: Med igenkänd text / With Recognized Text
**File:** `03-with-text.png` (75 KB)

Visar appen med faktisk igenkänd svensk text:
- Exempel på svensk text i stor, lättläst storlek (32sp)
- Text: "Hej och välkommen till Hörselhjälp. Detta är en app för att omvandla tal till text..."
- Demonstrerar appens huvudfunktion

Shows the app with actual recognized Swedish text:
- Example of Swedish text in large, readable size (32sp)
- Text: "Hello and welcome to Hearing Help. This is an app to convert speech to text..."
- Demonstrates the app's main function

#### Screenshot 4: Mörkt läge / Dark Mode
**File:** `04-dark-mode.png` (33 KB)

Visar appen i mörkt tema för tillgänglighet:
- Mörk bakgrund (#1A1C1E) med ljus text (#E2E2E6)
- Knappen visar: 🔄 LJUST (för att växla tillbaka)
- Demonstrerar tillgänglighetsfunktioner för olika ljusförhållanden

Shows the app in dark theme for accessibility:
- Dark background (#1A1C1E) with light text (#E2E2E6)
- Button shows: 🔄 LIGHT (to switch back)
- Demonstrates accessibility features for different lighting conditions

---

## 📋 Användning / Usage

### Steg-för-steg guide för Play Console / Step-by-step Guide for Play Console:

1. **Logga in / Log in**: https://play.google.com/console
2. **Välj app / Select app** eller skapa ny / or create new
3. **Gå till Butiksuppgifter / Go to Store listing**
4. **Under "Grafik" / Under "Graphics"**:
   - **App-ikon / App icon**: Ladda upp `icon/app-icon-512x512.png`
   - **Skärmdumpar för telefon / Phone screenshots**: Ladda upp minst 2, max 8 från `screenshots/`

### Rekommenderad ordning för skärmdumpar / Recommended Order for Screenshots:

1. `03-with-text.png` - Visar huvudfunktionen / Shows main function
2. `01-main-screen.png` - Visar initialt gränssnitt / Shows initial interface
3. `02-listening.png` - Visar aktivt läge / Shows active mode
4. `04-dark-mode.png` - Visar tillgänglighet / Shows accessibility

**Tips:** Google Play Store visar de första 2-3 skärmdumparna mest framträdande, så välj de som bäst representerar appens värde.

**Tip:** Google Play Store displays the first 2-3 screenshots most prominently, so choose the ones that best represent the app's value.

---

## ✅ Kravuppfyllelse / Requirements Compliance

### Google Play Store Krav / Requirements:

| Krav / Requirement | Status | Detaljer / Details |
|-------------------|--------|-------------------|
| App-ikon 512x512 PNG | ✅ | 512x512, PNG, RGBA |
| Minst 2 skärmdumpar | ✅ | 4 skärmdumpar inkluderade / 4 screenshots included |
| Minsta bredd 320px | ✅ | 1080px (Full HD) |
| PNG/JPEG format | ✅ | PNG |
| Max 8 skärmdumpar | ✅ | 4 av 8 möjliga / 4 of 8 possible |
| Korrekt proportioner | ✅ | 9:16 (portrait) |

---

## 📚 Dokumentation / Documentation

Denna mapp innehåller följande dokumentation:

This folder contains the following documentation:

1. **README.md** - Omfattande guide på svenska med detaljerade instruktioner
   - Comprehensive guide in Swedish with detailed instructions

2. **SUMMARY.md** - Snabb översikt och checklista
   - Quick overview and checklist

3. **screenshots/README.md** - Instruktioner för skärmdumpar
   - Instructions for screenshots

---

## 🎨 Design-specifikationer / Design Specifications

### Färger / Colors (Ljust läge / Light mode):
- Bakgrund / Background: `#F4F6F8`
- Text: `#1C2730`
- Textruta / Text area: `#E5E9EF`
- Start-knapp / Start button: `#4CAF50` (grön/green)
- Stopp-knapp / Stop button: `#F44336` (röd/red)
- Rensa-knapp / Clear button: `#FF9800` (orange)
- Tema-knapp / Theme button: `#0749A8` (blå/blue)

### Färger / Colors (Mörkt läge / Dark mode):
- Bakgrund / Background: `#1A1C1E`
- Text: `#E2E2E6`
- Textruta / Text area: `#2B2E31`

### Typografi / Typography:
- Huvudtext / Main text: 32sp
- Knappar / Buttons: 14sp (bold/fet)
- Radavstånd / Line spacing: 1.5x

---

## 🔗 Användbara länkar / Useful Links

- **Projektet / Project**: https://github.com/Jonaskjellsson/Horselhjalp
- **Google Play Console**: https://play.google.com/console
- **Play Store Asset Guide**: https://support.google.com/googleplay/android-developer/answer/9866151
- **Material Design Guidelines**: https://m3.material.io/

---

## ✨ Nästa steg / Next Steps

1. ✅ Granska alla tillgångar / Review all assets
2. ✅ Verifiera att filerna är korrekta / Verify files are correct
3. ⬜ Logga in på Play Console / Log in to Play Console
4. ⬜ Skapa eller välj din app / Create or select your app
5. ⬜ Ladda upp grafik / Upload graphics
6. ⬜ Fyll i beskrivningar / Fill in descriptions
7. ⬜ Publicera på Play Store / Publish to Play Store

---

## 📞 Support

För frågor om Play Store-publicering / For questions about Play Store publishing:
- Google Play dokumentation / documentation: https://support.google.com/googleplay
- GitHub Issues: https://github.com/Jonaskjellsson/Horselhjalp/issues

---

**Status:** ✅ Färdigt för Play Store / Ready for Play Store  
**Skapad / Created:** 2026-02-06  
**Version:** 1.0
