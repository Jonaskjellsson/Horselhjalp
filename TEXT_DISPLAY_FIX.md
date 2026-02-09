# Text Display Fix - Remove Extra Empty Lines

## Problem
When using speech recognition, text was displaying with unwanted empty lines within a single recording session:

**Before fix:**
```
Ja då provar vi igen
[EMPTY LINE]
denna gång om det 
[EMPTY LINE]
blir bättre
```

## Root Cause
The Android speech recognition API (`SpeechRecognizer`) was returning recognized text with embedded newline characters (`\n`). These newlines were being preserved in the final text display, causing extra empty lines.

## Solution
Modified `MainActivity.kt` to remove all newlines from speech recognition results:

1. Added `NEWLINE_REGEX = Regex("\n+")` to match one or more newlines
2. In `onResults()`: Remove newlines from final speech recognition results before appending to text
3. In `onPartialResults()`: Remove newlines from partial results shown during recognition

**After fix:**
```
Ja då provar vi igen denna gång om det blir bättre
```

The text now displays as continuous text, with the EditText widget naturally handling line wrapping based on screen width.

## Session Separation Preserved
Between different recording sessions (when user stops and starts recording again), the app still correctly adds 2 empty lines (`\n\n\n` = 3 newlines) to separate sessions:

```
Ja då provar vi igen denna gång om det blir bättre


Samma skit igen varför
```

## Changes Made
- `MainActivity.kt`: Added newline removal in `onResults()` and `onPartialResults()`
- `ExampleUnitTest.kt`: Added unit tests to verify newline removal behavior

## Testing
Run the unit tests:
```bash
./gradlew test
```

Manual testing:
1. Record first session: "Ja då provar vi igen denna gång om det blir bättre"
   - Should display without empty lines within the text
2. Stop recording
3. Record second session: "Samma skit igen varför"
   - Should display with 2 empty lines separating the two sessions
