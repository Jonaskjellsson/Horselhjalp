# Visual Explanation of Text Display Fix

## Before Fix (with embedded newlines)

### What the speech recognition API returned:
```
"Ja då provar vi igen\ndenna gång om det \nblir bättre"
```

### How it displayed on mobile:
```
Ja då provar vi igen
                         <- EMPTY LINE (unwanted)
denna gång om det 
                         <- EMPTY LINE (unwanted)
blir bättre
```

## After Fix (newlines removed)

### What we do with the speech recognition result:
```kotlin
// Before appending to display, we clean the text:
val newText = matches?.getOrNull(0)
    ?.replace(NEWLINE_REGEX, " ")       // "Ja då provar vi igen denna gång om det blir bättre"
    ?.replace(MULTIPLE_SPACES_REGEX, " ")  // Clean up any multiple spaces
    ?.trim()
```

### How it now displays on mobile:
```
Ja då provar vi igen denna gång om
det blir bättre
```
(Natural line wrapping by the EditText widget, no empty lines)

## Session Separation (still works correctly)

### First session:
```
Ja då provar vi igen denna gång om
det blir bättre
```

### User stops recording and starts again

### Second session added with 2 empty lines separator:
```
Ja då provar vi igen denna gång om
det blir bättre
                         <- EMPTY LINE (wanted)
                         <- EMPTY LINE (wanted)
Samma skit igen varför
```

## Key Changes

1. **Added `NEWLINE_REGEX`**: Matches one or more newlines (`\n+`)
2. **In `onResults()`**: Strip newlines from final recognition results
3. **In `onPartialResults()`**: Strip newlines from partial results during recognition
4. **Session separator preserved**: The `SESSION_SEPARATOR` ("\n\n\n") is explicitly added between recording sessions

## Result
✅ No unwanted empty lines within session text
✅ 2 empty lines between different recording sessions
✅ Natural text wrapping by the UI widget
