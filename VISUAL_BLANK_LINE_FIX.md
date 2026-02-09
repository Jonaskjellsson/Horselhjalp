# Visual Comparison: Before and After Blank Line Fix

## Overview
This document provides a visual comparison of the text display behavior before and after the blank line fix.

---

## Scenario 1: Continuous Speech (No Manual STOP)

### Input
User speaks continuously without pressing STOP:
> "Ja då provar vi igen denna gång om det blir bättre"

### BEFORE Fix
```
┌─────────────────────────────────────┐
│ Ja då provar vi igen                │
│                                     │  ← Unwanted blank line
│ denna gång om det                   │
│                                     │  ← Unwanted blank line
│ blir bättre                         │
│                                     │
└─────────────────────────────────────┘
```
**Problem**: Unwanted blank lines appeared within single recording session

### AFTER Fix
```
┌─────────────────────────────────────┐
│ Ja då provar vi igen denna gång om  │
│ det blir bättre                     │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```
**Solution**: Clean text with natural word wrapping, no blank lines

---

## Scenario 2: Two Recording Sessions (Manual STOP → START)

### Input
1. First session: "Ja då provar vi igen denna gång om det blir bättre"
2. Press STOP
3. Press START
4. Second session: "Samma skit igen varför"
5. Press STOP

### BEFORE Fix
```
┌─────────────────────────────────────┐
│ Ja då provar vi igen denna gång om  │
│ det blir bättre                     │
│                                     │  ← First blank line
│                                     │  ← Second blank line (unwanted)
│ Samma skit igen varför              │
│                                     │
└─────────────────────────────────────┘
```
**Problem**: TWO blank lines between sessions (too much spacing)

### AFTER Fix
```
┌─────────────────────────────────────┐
│ Ja då provar vi igen denna gång om  │
│ det blir bättre                     │
│                                     │  ← One blank line (correct)
│ Samma skit igen varför              │
│                                     │
│                                     │
└─────────────────────────────────────┘
```
**Solution**: Exactly ONE blank line between manual sessions

---

## Scenario 3: Three Recording Sessions

### Input
1. First: "Detta är första sessionen"
2. STOP → START
3. Second: "Detta är andra sessionen"
4. STOP → START
5. Third: "Detta är tredje sessionen"

### BEFORE Fix
```
┌─────────────────────────────────────┐
│ Detta är första sessionen           │
│                                     │
│                                     │  ← Too much spacing
│ Detta är andra sessionen            │
│                                     │
│                                     │  ← Too much spacing
│ Detta är tredje sessionen           │
└─────────────────────────────────────┘
```
**Problem**: Inconsistent or excessive blank lines between sessions

### AFTER Fix
```
┌─────────────────────────────────────┐
│ Detta är första sessionen           │
│                                     │  ← One blank line
│ Detta är andra sessionen            │
│                                     │  ← One blank line
│ Detta är tredje sessionen           │
│                                     │
└─────────────────────────────────────┘
```
**Solution**: Consistent 1 blank line between all sessions

---

## Scenario 4: Mixed - Multiple Results in Same Session

### Input
1. Press START
2. Speak: "Första delen"
3. Pause (but don't press STOP)
4. Continue: "andra delen"
5. Pause (but don't press STOP)
6. Continue: "tredje delen"
7. Press STOP

### BEFORE Fix
```
┌─────────────────────────────────────┐
│ Första delen                        │
│                                     │  ← Unwanted blank line
│ andra delen                         │
│                                     │  ← Unwanted blank line
│ tredje delen                        │
└─────────────────────────────────────┘
```
**Problem**: Natural pauses created unwanted blank lines

### AFTER Fix
```
┌─────────────────────────────────────┐
│ Första delen andra delen tredje    │
│ delen                               │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```
**Solution**: All parts in same line with spaces, no blank lines

---

## Technical Explanation

### Character Representation

**BEFORE Fix:**
```
"Ja då provar vi igen denna gång om det blir bättre\n\n\nSamma skit igen varför"
 └────────────────────────────────────┘└┬┘└───────────────────────┘
              First session             3 newlines    Second session
                                        = 2 blank lines
```

**AFTER Fix:**
```
"Ja då provar vi igen denna gång om det blir bättre\n\nSamma skit igen varför"
 └────────────────────────────────────┘└┬┘└───────────────────────┘
              First session            2 newlines   Second session
                                       = 1 blank line
```

### Within Session - No Blank Lines

**BEFORE Fix:**
```
"Första delen\nandra delen"  ← Recognition result had embedded newline
```
This newline would create a blank line in display.

**AFTER Fix:**
```
"Första delen andra delen"  ← Newlines removed, replaced with space
```
Clean text with single space between parts.

---

## Summary Table

| Situation | Before Fix | After Fix | Change |
|-----------|------------|-----------|--------|
| Continuous speech | Multiple blank lines | No blank lines | ✅ Fixed |
| Between sessions | 2 blank lines | 1 blank line | ✅ Fixed |
| Natural pauses | Unwanted blank lines | No blank lines | ✅ Fixed |
| Multiple sessions | Inconsistent | Consistent 1 blank line | ✅ Fixed |

---

## User Experience Impact

### Readability Improvement
- **Before**: Choppy, hard to read, visual interruptions
- **After**: Smooth, natural flow, easy to scan

### Predictability
- **Before**: Unclear when blank lines would appear
- **After**: Only manual STOP creates paragraph breaks

### Visual Cleanliness
- **Before**: Excessive whitespace, disorganized appearance
- **After**: Clean, professional text layout

---

## Code Constants

### SESSION_SEPARATOR
```kotlin
// Before
private const val SESSION_SEPARATOR = "\n\n\n"  // 3 newlines = 2 blank lines

// After  
private const val SESSION_SEPARATOR = "\n\n"    // 2 newlines = 1 blank line
```

### MULTIPLE_NEWLINES_REGEX
```kotlin
// Before
private val MULTIPLE_NEWLINES_REGEX = Regex("\n{4,}")  // Allows up to 3 blank lines

// After
private val MULTIPLE_NEWLINES_REGEX = Regex("\n{3,}")  // Allows max 1 blank line
```

### Session Flag Reset
```kotlin
// Before (in startListening)
lastPartialText = ""
// isNewSession was NOT reset

// After (in startListening)
lastPartialText = ""
isNewSession = false  // ← NEW: Reset flag to prevent incorrect breaks
```

---

## Testing Guide

To test the fix, try these scenarios:

1. **Test continuous speech**: Speak several phrases without stopping
   - Should see: Single line with spaces, no blank lines
   
2. **Test session separation**: Record, STOP, START, record again
   - Should see: Exactly 1 blank line between paragraphs
   
3. **Test multiple sessions**: Record 3+ separate sessions
   - Should see: Consistent 1 blank line between each

4. **Test natural pauses**: Speak with normal pauses (no manual STOP)
   - Should see: No blank lines, just continuous text
