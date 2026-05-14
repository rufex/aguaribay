# Aguaribay

Lightweight screen time guard for Android. When you open a tracked app, an overlay confirmation pops up before letting you in. All data stays on-device — no network, no account.

## What it does

- Pick any installed app to track
- Opening a tracked app shows a confirmation screen with a countdown timer
- Some basic stats: attempts today plus time spent on the app
- Re asks for confirmation every certain period of time using the app
- Configuration options:
    - Delay before being able to access the up (default set to 2 second)
    - Intervals of time when a new confirmation is required (default set to 10 minutes)

## Requirements

- Android 12+ (API 31)
- Three permissions should be granted before being able to use the app
  - **Usage Access** — read time spent per app
  - **Display Over Other Apps** — show the confirmation overlay
  - **Accessibility Service** — detect which app enters the foreground

