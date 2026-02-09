# Session Separator Fix - Visual Summary

## The Problem

After PR #87, the session separator was **NOT** working. When users pressed STOP and then START, no blank line was inserted between the recording sessions.

```
Expected behavior:
First session text
                    ← ONE BLANK LINE
Second session text

Actual behavior before this fix:
First session text Second session text  ← No blank line!
```

## Root Cause: Incorrect Flag Reset

PR #87 added this line in `startListening()`:
```kotlin
isNewSession = false // ← BUG! This prevented the separator from being added
```

## The Fix

**REMOVED** the incorrect flag reset:

```diff
 private fun startListening() {
     ...
     // Reset for new listening session
     lastPartialText = ""
-    isNewSession = false // ← REMOVED THIS LINE
+    // NOTE: Do NOT reset isNewSession here!
+    // It must remain true (if set by stopListening)
+    // so that the first result after STOP→START will add the session separator
     ...
 }
```

## How It Works Now

### Flag Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                  Flag Lifecycle Diagram                     │
└─────────────────────────────────────────────────────────────┘

Initial State:
    isNewSession = false

Step 1: User records first session
    [START] → speak → text appears
    isNewSession = false

Step 2: User presses STOP
    stopListening() executes
    ┌──────────────────────────────┐
    │ if (recognizedText.isNotEmpty()) │
    │     isNewSession = true      │
    └──────────────────────────────┘
    ✓ Flag is set to signal next session needs separator

Step 3: User presses START
    startListening() executes
    ┌──────────────────────────────┐
    │ lastPartialText = ""         │
    │ // isNewSession stays TRUE!  │  ← THE FIX
    └──────────────────────────────┘
    ✓ Flag remains true

Step 4: Speech recognition delivers result
    onResults() executes
    ┌───────────────────────────────────────┐
    │ if (recognizedText.isNotEmpty()) {    │
    │     if (isNewSession) {               │ ← Checks flag (TRUE)
    │         recognizedText.append("\n\n") │ ← Adds separator!
    │         isNewSession = false          │ ← Then resets
    │     }                                 │
    │ }                                     │
    └───────────────────────────────────────┘
    ✓ Separator added, flag reset

Step 5: Next result in same session
    isNewSession = false → just adds space
```

## Code Flow Comparison

### BEFORE (Buggy):
```
User Action          → Flag State         → Result
──────────────────────────────────────────────────────
Record session 1     : false              : "Text 1"
Press STOP           : true               : (waiting)
Press START          : false (WRONG!)     : (waiting)
Record session 2     : false              : "Text 1 Text 2"
                                            No separator! ✗
```

### AFTER (Fixed):
```
User Action          → Flag State         → Result
──────────────────────────────────────────────────────
Record session 1     : false              : "Text 1"
Press STOP           : true               : (waiting)
Press START          : true (CORRECT!)    : (waiting)
Record session 2     : false              : "Text 1\n\nText 2"
                       (reset in onResults)  One blank line! ✓
```

## Test Scenarios

### Scenario 1: Continuous Recording (No STOP)
```kotlin
User:   [START] "First" [pause] "second" [pause] "third" [STOP]
Result: "First second third"
        └─ No blank lines within session ✓
```

### Scenario 2: Two Sessions with STOP
```kotlin
User:   [START] "Session one" [STOP]
        [START] "Session two" [STOP]

Result: "Session one
        
        Session two"
        └─ Exactly 1 blank line ✓
```

### Scenario 3: Multiple Sessions
```kotlin
User:   [START] "A" [STOP]
        [START] "B" [STOP]
        [START] "C" [STOP]

Result: "A
        
        B
        
        C"
        └─ Consistent spacing ✓
```

## Why PR #87 Introduced This Bug

The PR documentation said: "Reset session flag when starting new listening"

This **sounded** logical but was actually **wrong** because:

❌ **Incorrect thinking**: "When starting a new session, reset the flag"
✓ **Correct thinking**: "The flag is set by STOP and consumed by the next result"

The flag is not about the "current" session state, it's a **signal** that the next result should have a separator.

## The Proper Flag Semantics

```
isNewSession is NOT "are we in a new session?"
isNewSession IS  "should the next result start a new paragraph?"
```

This is a **one-shot flag**:
1. Set by STOP: "next result needs separator"
2. Checked by onResults: "add separator if needed"
3. Reset by onResults: "separator consumed"

It's a **communication mechanism** between STOP and the next result.

## Key Insight

The flag operates in a **set-check-reset** pattern:

```
SET:   stopListening()    → isNewSession = true
CHECK: onResults()        → if (isNewSession)
RESET: onResults()        → isNewSession = false
```

If you reset it between SET and CHECK, the CHECK never sees the true value!

```
SET:   stopListening()    → isNewSession = true
❌ RESET: startListening() → isNewSession = false  ← BUG!
CHECK: onResults()        → if (isNewSession)     ← Always false!
```

## Summary

**Problem**: PR #87 reset the flag too early, preventing the session separator from working

**Solution**: Remove the incorrect reset, let the flag maintain its value until consumed

**Result**: Session separator now works correctly - exactly 1 blank line between STOP→START sessions

**Files Changed**:
- `MainActivity.kt`: Removed 1 line, added 2 comment lines
- `FIX_SESSION_SEPARATOR_ISSUE_330.md`: Added comprehensive documentation
- `SESSION_SEPARATOR_VISUAL_SUMMARY.md`: This visual guide

**Impact**: Minimal, surgical change that fixes the core issue without affecting other functionality
