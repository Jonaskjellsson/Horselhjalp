# Font Size Feature - UI Layout Description

## Visual Layout

The font size controls have been added to the header section of the app. Here's what the layout looks like:

```
┌────────────────────────────────────────────────────────────────┐
│  Textstorlek:  [A-] [A] [A+] [A++]        🌐 SVENSKA          │
│                                                                │
├────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────┐ │
│  │                                                          │ │
│  │  Din talade text visas här                              │ │
│  │                                                          │ │
│  │  (Text display area - size changes based on selection)  │ │
│  │                                                          │ │
│  │                                                          │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                │
│  [🎤 STARTA TAL]  [🗑️ RENSA TEXT]  [🔄 LÄGEVÄXLING]          │
└────────────────────────────────────────────────────────────────┘
```

## Header Section (Detailed)

### Left Side: Font Size Controls
```
Textstorlek:  [A-] [A] [A+] [A++]
```

**Label**: "Textstorlek:" in gray text (12sp)

**Button Sizes**:
- A- button: 40dp wide × 36dp high (10sp text)
- A button: 40dp wide × 36dp high (12sp text)
- A+ button: 40dp wide × 36dp high (14sp text)
- A++ button: 44dp wide × 36dp high (16sp text)

**Visual States**:
- **Active button** (currently selected): Blue text (#0061A4)
- **Inactive buttons**: Gray text (#73777F)

### Right Side: Language Switcher
The existing language button remains in its position on the right.

## Button Arrangement

The header uses a horizontal LinearLayout with `space_between` gravity:
- Font size controls are grouped on the left
- Language button is on the right
- Both sides are center-aligned vertically

## Font Size Effects

### Small (A-)
- Main text: 24sp (smaller than default)
- Status text: 20sp
- Good for: Users who want more text visible on screen at once

### Medium (A) - DEFAULT
- Main text: 32sp (original size)
- Status text: 26sp
- Good for: Standard readability

### Large (A+)
- Main text: 40sp (larger than default)
- Status text: 32sp
- Good for: Users with mild vision impairment

### Extra Large (A++)
- Main text: 48sp (largest size)
- Status text: 38sp
- Good for: Users with significant vision impairment

## Interaction Flow

1. User opens app → Medium size (A) is active by default (blue highlight)
2. User taps A+ button → Text immediately grows to 40sp, A+ turns blue, others turn gray
3. Toast message appears: "Textstorlek ändrad"
4. User closes and reopens app → A+ is still selected (persisted)

## Accessibility

### TalkBack Announcements
When a user navigates to font size buttons with TalkBack:
- A- button: "Liten textstorlek, knapp" (Small font size, button)
- A button: "Normal textstorlek, knapp" (Normal/Medium font size, button)
- A+ button: "Stor textstorlek, knapp" (Large font size, button)
- A++ button: "Extra stor textstorlek, knapp" (Extra large font size, button)

### Visual Clarity
- Buttons use increasing text sizes to represent their function
- Clear visual separation between each button (4dp margin)
- High contrast colors for active/inactive states
- Label clearly describes the purpose of the controls

## Responsive Design

The layout adjusts gracefully:
- Font size controls take only as much space as needed
- Language button stays anchored to the right
- Text display area automatically adjusts to show content at the selected size
- No overlap or clipping at any size setting

## Dark Mode Compatibility

The font size feature works seamlessly in both light and dark modes:
- Button text colors use the same active/inactive scheme
- Label text uses the existing `text_secondary` color (adapts to theme)
- No additional dark mode considerations needed

## Languages

The feature is fully localized:

### Swedish (sv-SE)
- Label: "Textstorlek:"
- Toast: "Textstorlek ändrad"
- Accessibility: "Liten/Normal/Stor/Extra stor textstorlek"

### English (en-US)
- Label: "Font size:"
- Toast: "Font size changed"
- Accessibility: "Small/Medium/Large/Extra large font size"

## Material Design Compliance

The implementation follows Material Design 3 guidelines:
- Uses MaterialButton with TextButton style
- Appropriate corner radius (4dp)
- Proper spacing and padding
- Touch target size meets minimum requirements (36dp height)
- Color scheme aligns with Material 3 color system
