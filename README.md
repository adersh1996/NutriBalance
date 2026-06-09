# NutriBalance 🥗

**AI-Powered Personal Nutrition Assistant for Indian Meals**

NutriBalance is a fully **offline-first** Android app that calculates your personalized daily calorie targets and uses **Gemini AI** to plan exact meal portions from the Indian foods you have — in grams (if you have a scale) or in cups and spoons. All your data stays on your device. No backend. No accounts.

> 🤖 **Vibe coded with AI assistance** — Clean enough to use as a learning reference for Android development from scratch.

---

## Features

- 📊 **BMR & TDEE Calculator** — Mifflin-St Jeor + Devine formula
- 🎯 **Smart Goal Detection** — Deficit / Surplus / Maintenance, auto-calculated
- 🤖 **Gemini AI Meal Planner** — Exact portions for foods you have
- 🍛 **Indian Food Aware** — Rice, dal, chapati, idli, dosa, sambar, Kerala dishes...
- ⚖️ **Scale or No Scale** — Portions in grams or cups/tablespoons
- 📅 **Multi-Day Meal Logging** — Full history of every meal, every day
- 🔥 **Consecutive Streak Tracker** — Tracks days where all 3 meals are logged
- 📈 **Weight History Chart** — Visualize your weight trend over time
- 🔔 **15-Day Weight Reminder** — WorkManager notification to update your weight
- 📉 **Calorie Rollover** — See if you were over or under your goal each day
- 🌙 **Dark Mode** — Fully supported

---

## Screenshots

> _Add your screenshots here_

---

## How to Add Your Gemini API Key

1. **Get a free key** from [Google AI Studio](https://aistudio.google.com/app/apikey)

2. **Open** `local.properties` in the root of the project

3. **Add your key:**
   ```
   gemini.api.key=YOUR_ACTUAL_KEY_HERE
   ```

4. **Sync** the project in Android Studio (`File → Sync Project with Gradle Files`)

5. **Build and run!**

> ⚠️ **Never commit `local.properties` to version control.** It's already in `.gitignore`.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture + Repository Pattern |
| Dependency Injection | Hilt |
| Local Database | Room (with schema migrations) |
| Preferences | DataStore |
| Background Tasks | WorkManager + HiltWorker |
| Networking | Retrofit + OkHttp |
| Charts | Vico (Compose-native) |
| Async | Kotlin Coroutines + StateFlow |
| AI | Gemini 2.0 Flash API |

---

## Project Structure

```
app/src/main/java/com/smad/nutribalance/
├── MainActivity.kt
├── NutriBalanceApp.kt            ← App class, WorkManager config, notification channel
├── data/
│   ├── local/
│   │   ├── dao/                  ← MealLogDao, UserProfileDao, WeightHistoryDao
│   │   ├── db/                   ← NutriDatabase (Room v2 + migrations)
│   │   ├── entity/               ← MealLogEntity, UserProfileEntity, WeightHistoryEntity
│   │   └── preferences/          ← DataStore (onboarding, dates, scale preference)
│   ├── remote/                   ← Gemini API (Retrofit)
│   └── repository/               ← MealLogRepository, UserProfileRepository, WeightHistoryRepository
├── di/                           ← Hilt modules (Database, Network, Worker)
├── domain/model/                 ← Domain models + NutritionCalculator
├── ui/
│   ├── navigation/               ← NavGraph + Screen routes
│   ├── screens/
│   │   ├── welcome/
│   │   ├── profile/
│   │   ├── scale/
│   │   ├── dashboard/
│   │   ├── meal/
│   │   ├── history/              ← Multi-day history + Vico chart
│   │   ├── weightupdate/         ← Weight update + before/after comparison
│   │   └── summary/
│   └── theme/                    ← Colors, Typography, Theme
├── util/                         ← StreakCalculator, NetworkUtil
└── worker/                       ← WeightReminderWorker, WorkManagerScheduler
```

---

## Screens

| Screen | Description |
|--------|-------------|
| Welcome | App intro with "Get Started" |
| Profile Input | Weight / Height / Age / Gender → calculates goals |
| Scale Check | Do you have a weighing scale? |
| Dashboard | Daily calorie summary, meal log buttons, streak badge |
| Meal Input | Enter foods → Gemini AI gives portion plan |
| History | All past days, expandable meal details, calorie rollover, weight chart |
| Weight Update | Update weight → recalculates all goals, shows comparison |

---

## Architecture Overview

```
UI (Compose) ──► ViewModel ──► Repository ──► Room / DataStore / Retrofit
                                    │
                               WorkManager (background reminders)
```

- **Single source of truth**: all state flows from Room/DataStore via `StateFlow`
- **Offline-first**: every feature works without internet except the AI meal suggestion
- **DB migrations**: Room handles schema upgrades without data loss

---

## Minimum Requirements

- Android 7.0+ (API 24)
- Internet connection for AI meal suggestions only (all tracking is fully offline)

---

## For Learners 🎓

This project covers the **core patterns used in professional Android development**:

- ✅ MVVM with proper separation of concerns
- ✅ Hilt DI from top to bottom (including HiltWorker)
- ✅ Room with DAOs, entities, and migrations
- ✅ WorkManager for background tasks
- ✅ Multi-screen Compose navigation with bottom nav
- ✅ Coroutines + Flow for reactive state
- ✅ Retrofit for API calls with error handling
- ✅ Clean architecture layers (data / domain / ui)

Clone it, run it, break it, learn from it. 🚀

---

## License

MIT — free to use, modify, and build on top of.
