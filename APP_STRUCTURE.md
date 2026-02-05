# Hörselhjälp - App Structure

## Visual Layout

```
┌─────────────────────────────────────┐
│                                     │
│         Hörselhjälp                 │  ← 32sp, bold, centered
│                                     │
├─────────────────────────────────────┤
│                                     │
│  Tryck på knappen för att börja     │  ← 20sp status text
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────────┐  │
│  │                               │  │
│  │  Din talade text kommer att   │  │  ← 24sp text display
│  │  visas här...                 │  │     (scrollable)
│  │                               │  │
│  │                               │  │
│  │                               │  │
│  │                               │  │
│  └───────────────────────────────┘  │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────────┐  │
│  │     🎤 STARTA TAL             │  │  ← 80dp high, 24sp text
│  │        (Green)                │  │     Main action button
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │     🗑️ RENSA TEXT            │  │  ← 60dp high, 20sp text
│  │        (Orange)               │  │     Clear button
│  └───────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

## File Structure

```
app/src/main/
├── AndroidManifest.xml          # App configuration
├── java/se/jonas/horselhjalp/
│   └── MainActivity.kt          # Main logic (230 lines)
└── res/
    ├── layout/
    │   └── activity_main.xml    # UI layout
    ├── values/
    │   ├── colors.xml           # High contrast colors
    │   └── strings.xml          # Swedish text resources
    └── ...                      # Other resources (icons, etc.)
```

## Code Flow

```
User Opens App
    ↓
Request Microphone Permission
    ↓
User Presses "🎤 STARTA TAL"
    ↓
SpeechRecognizer.startListening()
    ↓
Status: "Lyssnar..."
    ↓
User Speaks in Swedish
    ↓
Status: "Tal upptäckt"
    ↓
Speech Recognition Processing
    ↓
Status: "Bearbetar..."
    ↓
Text Displayed on Screen
    ↓
Status: "Klart! Tryck på mikrofonen för att fortsätta"
    ↓
Repeat or Clear
```

## Key Components

### MainActivity.kt
- **SpeechRecognizer**: Android's native speech recognition API
- **RecognitionListener**: Handles speech events (onResults, onError, etc.)
- **Permission Handler**: Manages microphone access
- **UI Controllers**: Buttons, text display, status updates

### activity_main.xml
- **LinearLayout**: Vertical orientation
- **TextView**: App title and status
- **ScrollView**: Contains text display
- **Buttons**: Start/Stop speech, Clear text

### strings.xml (36 strings)
- App name
- Status messages (9)
- Error messages (10)
- Toast messages (3)
- Button labels (3)
- Placeholder text

## Accessibility Features

1. **Large Text**: 24-32sp font sizes
2. **High Contrast**: Black text on white background
3. **TalkBack**: All elements have contentDescription
4. **Big Buttons**: 60-80dp height for easy touch
5. **Orientation**: Works in both portrait and landscape
6. **Auto-scroll**: Text area scrolls automatically
7. **Clear Status**: Always shows what's happening

## Technical Details

- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Language**: Kotlin 1.9.0
- **Build Tool**: AGP 8.1.0
- **Permissions**: RECORD_AUDIO only

## User Journey

1. **Install** app from APK
2. **Open** app
3. **Grant** microphone permission
4. **Press** green "STARTA TAL" button
5. **Speak** in Swedish
6. **See** text appear on screen
7. **Press** "STOPPA" to pause
8. **Press** "RENSA TEXT" to clear
9. **Repeat** as needed

## Benefits Over Old Implementation

| Old Web-Based | New Native |
|---------------|------------|
| 696KB assets | 0KB assets |
| WebView complexity | Simple native code |
| Network dependent | Offline capable |
| Web permissions | Native permissions |
| 2472 lines | 436 lines |
| Complex frontend | Clean Android |
| Multiple languages | Swedish focused |

---

**Built specifically for Swedish-speaking users with visual or hearing impairments.**
