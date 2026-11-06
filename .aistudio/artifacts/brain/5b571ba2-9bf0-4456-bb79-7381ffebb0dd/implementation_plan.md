# DAYZERO: Tactical UI & Wallpaper Cleanup Plan

## 1. Overview & Core Concept
This plan addresses the 7 specific tactical cleanup items requested to eliminate visual clutter, labels, and metadata stamps across the application and the generated wallpaper canvas, delivering an ultra-clean, pure aesthetic.

---

## 2. Proposed Changes

### Top Bar Removal (`ui/DayZeroScreen.kt`)
- Remove the `TopAppBar` component from the `Scaffold`.
- Eliminates:
  - `"DAYZERO"` title & `"0"` reticle badge
  - `"PIXEL 9"` pill badge
  - Top refresh/sync icon button
- The screen will now transition smoothly from edge-to-edge directly into the preview and editor.

### Preview Card Header & Centering (`ui/components/WallpaperPreviewCard.kt`)
- **Remove Preview Header**: Delete the `"PIXEL 9 PREVIEW"` text label and the phone icon.
- **Center Overlay Controls**: Realign the `[ Pure | Lock | Home ]` overlay toggle row to `Arrangement.Center` directly above the phone frame.
- **Simplify Apply Button**: Change label from `"APPLY TO PIXEL 9"` to `"APPLY"` (and `"APPLYING..."` during wallpaper setting).
- **Remove Card Footer Text**: Remove the `"Target Home and Lock Screen Pixel Nine 1080 2424"` caption below the buttons.

### Wallpaper Canvas Bottom Stamp Removal (`wallpaper/WallpaperGenerator.kt`)
- Remove the bottom canvas footer drawing logic (`"AUTOMATIC DAILY UPDATE • PIXEL 9 ACTIVE"` / `"DAY ZERO REACHED • EXECUTION ACTIVE"`).
- The bottom area of the wallpaper on both home and lock screens will remain completely clean and empty, avoiding any interference with system navigation bars or gesture pills.

---

## 3. Architecture & Visual Hierarchy Diagram

```
┌──────────────────────────────────────────────────────────┐
│                   DAYZERO Screen                         │
│                                                          │
│     ┌──────────────────────────────────────────────┐     │
│     │            [ Pure | Lock | Home ]            │     │  <- Centered toggle
│     │                                              │     │
│     │              ┌────────────────┐              │     │
│     │              │   (Header)     │              │     │  <- Clean wallpaper
│     │              │    TITLE       │              │     │     without bottom
│     │              │     42         │              │     │     status text
│     │              │  DAYS TO GO    │              │     │
│     │              │   Quote...     │              │     │
│     │              └────────────────┘              │     │
│     │                                              │     │
│     │          [ APPLY ]    [ SAVE PNG ]           │     │  <- Simplified button
│     └──────────────────────────────────────────────┘     │
│                                                          │
│     ┌──────────────────────────────────────────────┐     │
│     │ 1. Top Target Header (Toggle, Text, Align)   │     │
│     │ 2. Goal Title (Text, Align)                  │     │
│     │ 3. Target Date (Calendar button, Quick chips)│     │
│     │ 4. Countdown Number & Label (Text, Align)    │     │
│     │ 5. Motivational Message (Text, Align)        │     │
│     │ 6. Minimalist Theme & Positioning            │     │
│     └──────────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────────┘
```

---

## 4. Verification & Testing
1. **Compilation Check**: Run `compile_applet` to ensure zero compilation or syntax errors.
2. **Unit Tests**: Run `gradle :app:testDebugUnitTest` to verify all domain countdown and goal formatting tests pass.
3. **Visual Verification**: Confirm that the top bar, preview headers, bottom wallpaper text, and technical labels are gone, the overlay toggle is centered, and the primary action button displays "APPLY".
