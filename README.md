# Visual Web Builder

A native Android application for visually building websites using HTML, CSS, and JavaScript. Drag and drop HTML elements onto a canvas, configure their CSS properties, add JavaScript animations, and export production-ready HTML/CSS/JS files.

## Tech Stack

- **Language:** Kotlin
- **Min SDK:** API 24, **Target SDK:** API 34
- **Architecture:** Single Activity + Multiple Fragments + Jetpack Navigation
- **Pattern:** MVVM with ViewModel + StateFlow
- **Persistence:** Room Database (Projects, Components, Animations, Templates)
- **Dependency Injection:** Hilt
- **JSON:** Moshi
- **UI:** Material Design 3 (Material You)
- **Async:** Kotlin Coroutines + Flow
- **ViewBinding:** Enabled

## Project Structure

```
app/src/main/java/com/uibuilder/app/
  VisualUIBuilderApp.kt
  data/
    db/
      AppDatabase.kt
      dao/ (ProjectDao, ComponentDao, AnimationDao, HistoryDao)
      entity/ (ProjectEntity, ComponentEntity, AnimationEntity, ExportHistoryEntity, VersionHistoryEntity)
    repository/ (ProjectRepository, TemplateProvider, ColorThemeProvider, TypographyProvider)
  di/AppModule.kt
  domain/
    model/ (ComponentType, ComponentProperties, UiComponent, AnimationConfig, Project, Template, ColorTheme, DevicePreset)
    usecase/ (CreateProjectUseCase, AddComponentUseCase, ApplyTemplateUseCase, ExportProjectUseCase)
  presentation/
    main/ (MainActivity, MainViewModel)
    canvas/ (CanvasFragment, CanvasView, CanvasViewModel, ComponentViewWrapper)
    palette/ (ComponentPaletteBottomSheet, PaletteAdapter)
    properties/ (PropertiesBottomSheet, PropertiesPagerAdapter, PropertiesViewModel, tabs/)
    templates/ (TemplatesFragment, TemplatesAdapter, TemplatesViewModel)
    export/ (ExportFragment, ExportViewModel)
    settings/ (SettingsFragment, SettingsViewModel)
    common/ (ColorPickerDialog)
  util/ (HtmlGenerator, CssGenerator, JavaScriptGenerator, JsonUtils, Memento)
```

## HTML Element Palette

The builder supports 24 HTML element types:

- **Text:** Heading (h1), Paragraph (p), Span
- **Form:** Button, Input, Textarea, Select, Checkbox, Radio, Toggle, Form
- **Layout:** Card, Flex Row, Flex Column, Grid, Scroll Container, Section
- **Navigation:** Nav, Header, Footer, Link
- **Media:** Image, List, Carousel, Progress

## Properties Panel

Each element exposes 6 tabs of CSS-style properties:

1. **Text** - content, font family, font size, color, weight, style
2. **Background** - solid color, linear gradient (with orientation), image
3. **Dimensions** - width, height, min-width, min-height (px or %)
4. **Padding/Margin** - 4-sided insets in px
5. **Corners & Effects** - border-radius (per-corner), box-shadow, rotation, opacity, scale, visibility
6. **Animation** - 14 presets with duration, delay, easing, repeat, sequence mode

## Code Generation

The export produces three files:

### index.html
- Semantic HTML5 markup with id and class attributes
- Self-closing tags for void elements (img, input, progress)
- Proper attributes (href, src, alt, placeholder, type)
- Links to styles.css and script.js

### styles.css
- CSS custom properties (`:root` variables) derived from theme color
- Universal reset and base body styles
- Per-element rules with all configured CSS properties
- Flexbox and Grid layout properties
- `:hover` and `:focus` interactive states
- Responsive `@media (max-width: 768px)` breakpoint

### script.js
- DOMContentLoaded initialization
- Click handlers for interactive elements
- Animation player supporting single and sequenced animations
- Parallel and sequential playback modes
- Form submit handlers
- Utility functions (debounce, animation queue)

## Animation Easings

Mapped to CSS cubic-bezier curves:

- Linear: `linear`
- Accelerate: `ease-in`
- Decelerate: `ease-out`
- AccelerateDecelerate: `ease-in-out`
- Bounce: `cubic-bezier(0.68, -0.55, 0.265, 1.55)`
- Overshoot: `cubic-bezier(0.175, 0.885, 0.32, 1.275)`
- Anticipate: `cubic-bezier(0.36, 0, 0.66, -0.56)`
- AnticipateOvershoot: `cubic-bezier(0.68, -0.55, 0.265, 1.55)`

## Templates

50+ pre-built page templates across 12 categories:

- Login (10 variants), Signup (5), Dashboard (5), Profile (5)
- Settings (3), Chat (3), E-Commerce (5), News (3)
- Onboarding (4), Forms (3), Sidebar Nav (2), Top Nav (2)

Each template builds a list of HTML elements with default properties ready to be customized.

## Responsive Preview

Five device presets for testing responsive layouts:

- Phone Small (320 x 480)
- Phone Medium (360 x 640)
- Phone Large (411 x 731)
- Tablet 7" (600 x 960)
- Tablet 10" (800 x 1280)

## Setup

1. Copy `local.properties.template` to `local.properties`
2. Set `sdk.dir` to your Android SDK path
3. Open in Android Studio (Hedgehog or later)
4. Gradle sync, then Run on API 24+ device or emulator

## Build

```
./gradlew assembleDebug
./gradlew installDebug
```

## Features Overview

- Drag-and-drop canvas with 8 resize handles
- Multi-select with Ctrl+Click grouping
- Undo/redo with 100-step history
- Cut/copy/paste clipboard
- Duplicate and z-order controls
- 30 color themes and 20 typography presets
- RTL layout support
- Grid snapping
- Live preview
- Figma JSON export (skeleton)
- Offline-first with Room persistence
- Dark mode (Material You)
