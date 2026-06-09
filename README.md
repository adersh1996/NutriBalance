# NutriBalance 🥗

**AI-Powered Personal Nutrition Assistant for Indian Meals**

NutriBalance is an Android app that calculates your personalized daily calorie targets and uses the Gemini AI to plan exact meal portions from the Indian foods you have available — in grams (if you have a scale) or in cups and spoons.

---

## Features

- 📊 **BMR & TDEE Calculator** — Mifflin-St Jeor + Devine formula
- 🎯 **Smart Goal Detection** — Deficit / Surplus / Maintenance automatically
- 🤖 **Gemini AI Meal Planner** — Tells you exactly how much to eat
- 🍛 **Indian Food Aware** — Rice, dal, chapati, idli, dosa, sambar, curries...
- ⚖️ **Scale or No Scale** — Portions in grams or cups/tablespoons
- 📅 **Daily Logging** — Track all 3 meals per day
- 🔥 **Streak Tracker** — Stay motivated with consecutive day streaks
- 🌙 **Dark Mode** — Fully supported

---

## How to Add Your Gemini API Key

1. **Get a free API key** from [Google AI Studio](https://aistudio.google.com/app/apikey)

2. **Open** `local.properties` in the root of the project (same level as `build.gradle.kts`)

3. **Add your key:**
   ```
   gemini.api.key=YOUR_ACTUAL_KEY_HERE
   ```
   Replace `YOUR_ACTUAL_KEY_HERE` with the key you copied from AI Studio.

4. **Sync the project** in Android Studio (`File → Sync Project with Gradle Files`)

5. **Build and run!**

> ⚠️ **Never commit `local.properties` to version control.** It's already in `.gitignore`.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository Pattern |
| DI | Hilt |
| Database | Room |
| Preferences | DataStore |
| Networking | Retrofit + OkHttp |
| AI | Gemini 2.0 Flash API |

---

## Project Structure

```
app/src/main/java/com/smad/nutribalance/
├── MainActivity.kt
├── NutriBalanceApp.kt
├── data/
│   ├── local/
│   │   ├── dao/          ← Room DAOs
│   │   ├── db/           ← Room Database
│   │   ├── entity/       ← Room Entities
│   │   └── preferences/  ← DataStore
│   ├── remote/           ← Gemini API (Retrofit)
│   └── repository/       ← Repositories
├── di/                   ← Hilt Modules
├── domain/model/         ← Domain models + NutritionCalculator
├── ui/
│   ├── navigation/       ← NavGraph + Screen routes
│   ├── screens/          ← 6 screens + ViewModels
│   └── theme/            ← Colors, Typography, Theme
└── util/                 ← NetworkUtil
```

---

## Screens

| Screen | Description |
|--------|-------------|
| Welcome | App intro with "Get Started" |
| Profile Input | Weight / Height / Age / Gender |
| Scale Check | Do you have a weighing scale? |
| Dashboard | Daily summary + meal log buttons |
| Meal Input | Enter foods → Gemini AI plan |
| Daily Summary | Calories, progress bar, streak |

---

## Minimum Requirements

- Android 7.0+ (API 24)
- Internet connection for AI meal suggestions (offline for everything else)

---

## License

MIT — feel free to modify and use for personal projects.
