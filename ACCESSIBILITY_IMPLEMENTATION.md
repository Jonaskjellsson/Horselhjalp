# Tillgänglighetsimplementation / Accessibility Implementation

## Översikt / Overview

Detta dokument beskriver de unika tillgänglighetsfunktioner som implementerats i Hörselhjälp-appen.

This document describes the unique accessibility features implemented in the Hearing Help app.

## Funktioner / Features

### 1. Större Textstorlekar / Larger Text Sizes

**Implementerat:**
- Rubriktext: 42sp (ökad från 32sp)
- Statustext: 26sp (ökad från 20sp)  
- Huvudtextvisning: 32sp (ökad från 24sp)
- Mikrofonknapp: 28sp (ökad från 24sp)
- Övriga knappar: 24sp (ökad från 20sp)

### 2. Förbättrad Läsbarhet / Improved Readability

**Radavstånd:**
- `lineSpacingMultiplier`: 1.35-1.5 (35-50% extra radavstånd)
- `lineSpacingExtra`: 14dp för huvudtext
- Ökad padding: 22dp istället för 16dp

**Fördelar:**
- Lättare att fokusera på individuella rader
- Minskar visuell trängsel
- Bättre för användare med lässvårigheter

### 3. Glasaktighetsvaxlare / Visual Mode Switcher

**Unikt Svenskt Koncept:**

Använder anatomiska ögonmetaforer för att beskriva ljuslägen:

#### Kornhinneklarhet (Cornea Clarity Mode)
- **Bakgrund:** #F4F6F8 (ljust)
- **Huvudtext:** #1C2730 (mörk)
- **Sekundärtext:** #586977
- **Textarea:** #E5E9EF
- **Användning:** För normala ljusförhållanden, maximal skärpa

#### Näghinnedämpning (Retina Dampening Mode)
- **Bakgrund:** #36393C (mörk)
- **Huvudtext:** #D3D9DE (ljus)
- **Sekundärtext:** #99A1A9
- **Textarea:** #2B2E31
- **Användning:** För ljuskänsliga användare, minskar bländning

## Teknisk Implementation

### Unik Persistensmekanism

**XOR-baserad lagring:**
```kotlin
private var ogonmiljotillstand = 0

// Spara med XOR-kodning
val kodatVarde = ogonmiljotillstand xor 0x2A
sparning.edit().putInt("ogonmiljo_xor", kodatVarde).apply()

// Ladda med XOR-avkodning
val kodadVarde = sparning.getInt("ogonmiljo_xor", 42)
ogonmiljotillstand = kodadVarde xor 0x2A
```

**Fördelar:**
- Enkel obfuskering av användarval
- Deterministisk omkodning
- Minimal overhead

### Bitwise Flip Algoritm

**Växla mellan lägen:**
```kotlin
private fun vaxlaOgonmiljo() {
    ogonmiljotillstand = ogonmiljotillstand xor 1  // Flip mellan 0 och 1
    tillampaNuvarandeOgonmiljo()
    sparaOgonmiljotillstand()
}

private fun arNaghinnedampning(): Boolean = (ogonmiljotillstand and 1) == 1
```

**Fördelar:**
- Atomisk operation
- Inga if/else kedjor
- Elegant toggle-logik

### Offset-baserad Färgval

**Algoritm:**
```kotlin
private fun hamtaFargmedOffset(paketindex: Int, fargoffset: Int): Int {
    val fargarray = if (paketindex == 0) {
        arrayOf(R.color.background, R.color.text_primary, ...)
    } else {
        arrayOf(R.color.naghinnedampning_ytskikt, ...)
    }
    return ContextCompat.getColor(this, fargarray[fargoffset % fargarray.size])
}
```

**Fördelar:**
- Enhetlig färghantering
- Lätt att lägga till nya färgpaket
- Modulo förhindrar array-överskridningar

## Användargränssnitt

### Knappplacering
1. **Mikrofonknapp** (95dp höjd) - Primär funktion
2. **Rensa-knapp** (75dp höjd) - Sekundär funktion  
3. **Glasaktighetsvaxlare** (75dp höjd) - Tillgänglighetskontroll

### Visuell Feedback
- Toast-meddelanden vid lägesväxling
- Färgändring på växlarknapp
- Omedelbar uppdatering av hela gränssnittet

## Svensk Terminologi

**Unika sammansatta ord skapade för denna app:**

| Svensk Term | Engelsk Översättning | Betydelse |
|------------|---------------------|-----------|
| Glasaktighet | Vitreous-ness | Kvaliteten av synmiljön (metaforiskt) |
| Kornhinneklarhet | Cornea clarity | Tydligt, klart läge |
| Näghinnedämpning | Retina dampening | Mildrat, dämpat läge |
| Ogonmiljotillstand | Eye environment state | Aktuellt synläge |
| Glasaktighetsvaxlare | Vitreous humor switcher | Lägesomkopplare |

Dessa termer är:
- Anatomiskt inspirerade (baserade på ögats struktur)
- Intuitivt begripliga för svenska användare
- Unika för denna implementation
- Metaforiskt använda (inte medicinska termer)

## Testning

**Manuell testning rekommenderas:**
1. Öppna appen
2. Verifiera ökade textstorlekar
3. Klicka på Glasaktighetsvaxlare-knappen
4. Bekräfta att färgerna ändras
5. Stäng och öppna appen igen
6. Verifiera att valet sparats

**Förväntat beteende:**
- Smidig övergång mellan lägen
- Persistent lagring över app-omstarter
- Tydlig visuell feedback

## Framtida Förbättringar

Potentiella utökningar:
- Fler färgpaket (t.ex. "Linssammandragning" för sepia-ton)
- Anpassningsbara textstorlekar med slider
- Animerade övergångar mellan lägen
- Högkontrastläge för extra tillgänglighet
- Synkronisering med systemets mörkt läge (frivilligt)