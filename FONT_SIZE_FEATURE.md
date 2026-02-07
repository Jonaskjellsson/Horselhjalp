# Font Size Adjustment Feature

## Overview
This document describes the font size adjustment feature implemented for the Hörselhjälp (Hearing Help) Android app. This feature allows users to adjust the text size to better suit their visual needs, which is particularly important for users with hearing and vision impairments.

## Feature Description

### User Interface
The font size controls are located in the header of the app, next to the language switcher. The interface consists of:

1. **Label**: "Textstorlek:" (Swedish) / "Font size:" (English)
2. **Four size buttons**:
   - **A-** (Small): 24sp for main text, 20sp for status text
   - **A** (Medium): 32sp for main text, 26sp for status text - **Default**
   - **A+** (Large): 40sp for main text, 32sp for status text
   - **A++** (Extra Large): 48sp for main text, 38sp for status text

### Visual Feedback
- The currently selected font size button is highlighted in blue (#0061A4)
- Non-selected buttons appear in gray (#73777F)
- A toast message "Textstorlek ändrad" / "Font size changed" appears when the user changes the font size

### Persistence
- Font size preference is saved to SharedPreferences
- The selected font size persists across app restarts
- Preference key: `font_size` in `horsel_interna` SharedPreferences

## Technical Implementation

### Files Modified

#### 1. MainActivity.kt
**Added:**
- `FontSize` enum class with four size options (SMALL, MEDIUM, LARGE, EXTRA_LARGE)
- Four lateinit button variables for font size controls
- `currentFontSize` state variable (default: MEDIUM)
- `loadFontSize()`: Loads saved font size from SharedPreferences
- `saveFontSize()`: Saves current font size to SharedPreferences
- `setFontSize(size: FontSize)`: Changes the current font size
- `applyFontSize()`: Applies the font size to text views
- `updateFontSizeButtons()`: Updates button visual feedback (highlighting)

**Modified:**
- `onCreate()`: Added font size button initialization and click listeners
- Added call to `loadFontSize()` and `applyFontSize()` on startup

#### 2. activity_main.xml
**Modified:**
- Header LinearLayout: Changed from `gravity="end"` to `gravity="space_between|center_vertical"`
- Added nested LinearLayout for font size controls (label + 4 buttons)
- Used MaterialButton with TextButton style for a clean, accessible interface

#### 3. strings.xml (Swedish)
**Added:**
- `font_size_label`: "Textstorlek:"
- `font_size_small`: "A-"
- `font_size_medium`: "A"
- `font_size_large`: "A+"
- `font_size_extra_large`: "A++"
- `font_size_changed`: "Textstorlek ändrad"
- Accessibility descriptions for all four font size buttons

#### 4. strings.xml (English - values-en)
**Added:**
- English translations of all font size strings
- `font_size_label`: "Font size:"
- `font_size_changed`: "Font size changed"
- Accessibility descriptions in English

#### 5. colors.xml
**Added:**
- `font_size_active`: #0061A4 (blue for active button)
- `font_size_inactive`: #73777F (gray for inactive buttons)

## Accessibility Features

1. **Accessibility Descriptions**: All font size buttons have descriptive content descriptions for TalkBack users
   - "Liten textstorlek" / "Small font size"
   - "Normal textstorlek" / "Medium font size"
   - "Stor textstorlek" / "Large font size"
   - "Extra stor textstorlek" / "Extra large font size"

2. **Visual Indicators**: Clear visual feedback with button size and color changes

3. **Proportional Sizing**: Status text scales proportionally with main text for consistency

4. **Compatibility**: Works seamlessly with existing dark/light mode toggle

## Usage

### For Users
1. Open the Hörselhjälp app
2. Look at the top left of the screen for "Textstorlek:" label
3. Tap one of the four buttons (A-, A, A+, A++) to change text size
4. The text size changes immediately
5. The selected size is saved automatically and will be remembered when you restart the app

### For Developers
The font size feature is self-contained and follows the existing patterns in the codebase:
- Uses SharedPreferences for persistence (same as language and theme settings)
- Follows Material Design 3 guidelines
- Maintains accessibility standards
- Supports both Swedish and English localization

## Testing Checklist

- [ ] Font size changes are applied immediately
- [ ] Font size persists across app restarts
- [ ] Font size buttons show correct visual feedback (active button highlighted)
- [ ] Font size works in light mode
- [ ] Font size works in dark mode
- [ ] Font size works with Swedish language
- [ ] Font size works with English language
- [ ] TalkBack correctly announces font size button labels
- [ ] All four size options (Small, Medium, Large, Extra Large) work correctly
- [ ] Text remains readable at all font sizes
- [ ] Layout doesn't break at any font size

## Future Enhancements

Potential improvements for future versions:
1. Add more granular font size options
2. Add a slider interface for continuous font size adjustment
3. Consider adding separate controls for status text vs main text
4. Add font size preview before applying
5. Consider respecting Android system font size settings

## Conclusion

This font size adjustment feature significantly improves the accessibility of the Hörselhjälp app, allowing users with different visual needs to customize the text display to their preferences. The implementation is clean, maintainable, and follows Android best practices.
