# Hörselhjälp - Funktionsöversikt

![Usage Flowchart](usage-flowchart.svg)

## Appens huvudfunktioner illustrerade

```
┌────────────────────────────────────────────────┐
│          🎤 HÖRSELHJÄLP 🎤                    │
│    Svenskt Tal till Text i Realtid            │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│  Språk: [🇸🇪 Svenska]                         │
├────────────────────────────────────────────────┤
│                                                │
│  📝 TEXT VISAS HÄR                            │
│                                                │
│  Hej! Välkommen till Hörselhjälp.            │
│                                                │
│  Denna app omvandlar svenskt tal              │
│  till text i realtid med stora,               │
│  lättlästa bokstäver.                         │
│                                                │
│  Perfekt för personer med hörsel-             │
│  eller synnedsättning!                        │
│                                                │
│                                                │
└────────────────────────────────────────────────┘

┌──────────┬──────────┬──────────┐
│ 🎤 START │ 🗑️ RENSA │ 🌙 TEMA  │
│  TAL     │   TEXT   │          │
└──────────┴──────────┴──────────┘

```

## Användningsflöde

1. **STARTA** 
   - Tryck på "START TAL" knappen
   - Ge mikrofontillstånd första gången
   - Börja tala på svenska eller engelska

2. **LYSSNA**
   - Appen lyssnar på ditt tal
   - Text visas i realtid på skärmen
   - Stora, lättlästa bokstäver

3. **STOPPA**
   - Automatisk paus efter 5 sekunders tystnad
   - Eller tryck på "STOPPA" knappen
   - Texten sparas på skärmen

4. **RENSA**
   - Tryck på "RENSA TEXT" för att börja om
   - Börja en ny konversation

5. **TEMA**
   - Växla mellan ljust och mörkt tema
   - Optimerad kontrast för läsbarhet

## Tillgänglighetsfunktioner

### 👁️ Synvänligt
- **Stora textstorlekar**: 32sp för optimal läsbarhet
- **Hög kontrast**: Svart text på vit bakgrund (ljust läge)
- **Rullbar textyta**: Hantera långa samtal

### 👂 Hörselstöd
- **Realtids transkribering**: Se text medan du pratar
- **Ingen fördröjning**: Omedelbar feedback
- **Svenskt tal**: Optimerad för svenska språket

### ♿ TalkBack-kompatibel
- **Alla knappar har beskrivningar**
- **Fungerar med skärmläsare**
- **Stora klickområden**

## Tekniska Detaljer

### Språkstöd
- 🇸🇪 Svenska (sv-SE) - Standard
- 🇬🇧 Engelska (en-US) - Via språkväxlare

### Tema
- ☀️ Ljust läge - Vit bakgrund, svart text
- 🌙 Mörkt läge - Mörk bakgrund, ljus text
- 🎨 Material 3 Design

### Sekretess
- ✅ Ingen internetanslutning krävs*
- ✅ Ingen data sparas på servrar
- ✅ Använder enhetens inbyggda taligenkänning

*Obs: Vissa Android-enheter kan kräva internet första gången

## Exempel på användning

```
Användare säger:      App visar (ackumulerat):
───────────────       ──────────
"Hej"           →     Hej

"Hur mår du?"   →     Hej
                      
                      
                      Hur mår du?

"Bra tack!"     →     Hej
                      
                      
                      Hur mår du?
                      
                      
                      Bra tack!
```

## Systemkrav

- Android 7.0 (API 24) eller senare
- Mikrofontillstånd
- Google-appen installerad (för taligenkänning)
- Svenskt språk installerat i taligenkänningen

## Installation

1. Ladda ner APK från [Releases](https://github.com/Jonaskjellsson/Horselhjalp/releases)
2. Installera på din Android-enhet
3. Ge mikrofontillstånd
4. Börja använda!
