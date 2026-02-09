# Speech Recognition Newline Handling Fix

## Problem Statement

The Hörselhjälp Android app (Swedish speech-to-text) had persistent issues with unwanted blank lines:

### Issues Before Fix
1. **Within continuous listening session**: Unwanted blank lines or visual gaps appeared
   - Example: "Ja då provar vi igen [blank line] denna gång om det [blank line] blir bättre"
   - This disrupted reading flow and made text hard to scan

2. **Between manual STOP → START sessions**: Inconsistent spacing
   - Sometimes no blank line (text just continues)
   - Sometimes correct spacing
   - Made it hard to distinguish separate recording sessions

### Desired Behavior
1. **Within one continuous speech session** (no manual STOP/START):
   - Single line with spaces only
   - Example: "Ja då provar vi igen denna gång om det blir bättre"

2. **After manual STOP → START**:
   - Exactly ONE blank line between paragraphs
   - Example:
     ```
     Ja då provar vi igen denna gång om det blir bättre
     
     Samma skit igen varför
     ```

## Root Cause Analysis

The previous implementation used `SESSION_SEPARATOR = "\n\n\n"` (3 newlines), which created 2 blank lines between sessions. The requirement is for only 1 blank line (2 newlines).

Additionally, the `isNewSession` flag was not being reset when starting a new listening session, which could cause incorrect paragraph breaks.

## Solution Implemented

### Changes Made

#### 1. Session Separator (Line 49)
**Before:**
```kotlin
private const val SESSION_SEPARATOR = "\n\n\n"  // 3 newlines = 2 blank lines
```

**After:**
```kotlin
private const val SESSION_SEPARATOR = "\n\n"  // 2 newlines = 1 blank line
```

This ensures exactly ONE blank line appears between manual recording sessions.

#### 2. Multiple Newlines Regex (Line 55)
**Before:**
```kotlin
private val MULTIPLE_NEWLINES_REGEX = Regex("\n{4,}")  // Match 4+ newlines
```

**After:**
```kotlin
private val MULTIPLE_NEWLINES_REGEX = Regex("\n{3,}")  // Match 3+ newlines
```

This cleans up any excessive newlines (3 or more) down to the session separator (2 newlines), preventing more than 1 blank line from appearing.

#### 3. Reset Session Flag in startListening() (Line 498)
**Before:**
```kotlin
lastPartialText = ""
```

**After:**
```kotlin
lastPartialText = ""
isNewSession = false  // Reset session flag when starting new listening
```

This prevents incorrect paragraph breaks by resetting the session flag each time listening starts.

#### 4. Updated Comments Throughout
All comments were updated to correctly reflect "1 blank line" instead of "2 blank lines".

## How It Works

### Text Flow Logic

1. **Within Single Recording Session**:
   ```kotlin
   if (recognizedText.isNotEmpty()) {
       if (isNewSession) {
           recognizedText.append(SESSION_SEPARATOR)  // Not executed within session
           isNewSession = false
       } else {
           recognizedText.append(" ")  // Single space between results
       }
   }
   ```

2. **After Manual STOP → START**:
   - When user presses STOP: `isNewSession = true` is set (line 523)
   - When next result arrives: `SESSION_SEPARATOR` ("\n\n") is appended
   - Creates exactly 1 blank line between sessions

3. **Aggressive Text Cleaning**:
   ```kotlin
   val cleaned = recognizedText.toString()
       .replace(MULTIPLE_SPACES_REGEX, " ")           // Multiple spaces → one
       .replace(NEWLINE_WITH_SPACE_REGEX, "\n")       // Remove space after newline
       .replace(MULTIPLE_NEWLINES_REGEX, SESSION_SEPARATOR)  // 3+ newlines → 2
       .trim()
   ```

### Key Design Decisions

1. **No Live Updates in onPartialResults**: 
   - Only updates `statusText` to show what's being heard
   - Prevents flickering and duplicate text
   - Final text only appears in `onResults`

2. **No Silence Detection**:
   - Removed all auto-pause/silence detection logic
   - User has full control via manual STOP/START
   - More predictable behavior

3. **Aggressive Newline Removal**:
   - All newlines from speech recognition are stripped
   - Replaced with spaces within session
   - Only manual STOP creates paragraph breaks

## Testing

### Unit Tests Updated

Three unit tests were updated to reflect the new behavior:

1. **testNewlineRemovalFromRecognizedText()**: Verifies newlines are stripped from recognition results
2. **testSessionSeparatorPreserved()**: Verifies 2 newlines (1 blank line) are preserved
3. **testExcessiveNewlinesReduced()**: Verifies 3+ newlines are cleaned to 2 newlines

### Manual Testing Scenarios

#### Test 1: Continuous Speech
1. Press START
2. Speak: "Ja då provar vi igen denna gång om det blir bättre"
3. Expected: Single line with natural word spacing, no blank lines

#### Test 2: Multiple Sessions
1. Press START
2. Speak: "Ja då provar vi igen denna gång om det blir bättre"
3. Press STOP
4. Press START
5. Speak: "Samma skit igen varför"
6. Expected: Two paragraphs with exactly 1 blank line between them

#### Test 3: Multiple Results in Same Session
1. Press START
2. Speak several phrases with natural pauses
3. Do NOT press STOP
4. Expected: All text on single line with spaces, no blank lines

## Benefits

1. **Consistent Spacing**: Predictable behavior - 1 blank line between manual sessions
2. **Clean Text**: No unwanted blank lines within continuous speech
3. **Better Readability**: Text flows naturally without visual interruptions
4. **User Control**: Only manual STOP/START creates paragraph breaks
5. **Simpler Logic**: Removed complexity of silence detection

## Files Changed

- `MainActivity.kt`: Core logic changes for session separator and text cleaning
- `ExampleUnitTest.kt`: Updated unit tests to match new behavior
- `NEWLINE_FIX_DOCUMENTATION.md`: This documentation

## Compatibility

- Minimum SDK: 21 (Android 5.0 Lollipop)
- Target SDK: 35 (Android 14)
- Language support: Swedish (sv-SE) and English (en-US)
- No breaking changes to existing functionality
