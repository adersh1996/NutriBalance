package com.smad.nutribalance.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object StreakCalculator {

    /**
     * Computes the current consecutive-day streak given a list of dates
     * where ALL 3 meals were logged (in any order, may have duplicates).
     *
     * Algorithm:
     * - Sort dates descending
     * - Starting from today, count how many consecutive days match
     * - A streak is broken if any day is missing from the sequence
     * - Future dates are ignored
     */
    fun compute(datesWithAllMeals: List<String>): Int {
        if (datesWithAllMeals.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = formatter.format(Date())

        // Deduplicate and sort newest first
        val sortedDates = datesWithAllMeals.toSortedSet(compareByDescending { it }).toList()

        var streak = 0
        val cal = Calendar.getInstance()
        cal.time = formatter.parse(today) ?: return 0

        for (date in sortedDates) {
            // Skip future dates
            if (date > today) continue

            val expectedDate = formatter.format(cal.time)
            if (date == expectedDate) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else if (date < expectedDate) {
                // Gap found — streak ends
                break
            }
            // date > expectedDate means it was already counted or future (already skipped)
        }

        return streak
    }
}
