package com.smad.nutribalance.domain.model

object NutritionCalculator {

    /**
     * Ideal body weight using Devine Formula.
     * Height must be in centimeters.
     */
    fun calculateIdealWeight(heightCm: Double, isMale: Boolean): Double {
        val inchesAbove5Feet = (heightCm - 152.4) / 2.54
        return if (isMale) {
            50.0 + 2.3 * inchesAbove5Feet
        } else {
            45.5 + 2.3 * inchesAbove5Feet
        }
    }

    /**
     * Basal Metabolic Rate using Mifflin-St Jeor equation.
     */
    fun calculateBMR(weightKg: Double, heightCm: Double, ageYears: Int, isMale: Boolean): Double {
        return if (isMale) {
            (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) - 161
        }
    }

    /**
     * Total Daily Energy Expenditure (sedentary multiplier = 1.2).
     */
    fun calculateTDEE(bmr: Double): Double = bmr * 1.2

    /**
     * Determine goal type based on current vs ideal weight.
     */
    fun determineGoalType(currentWeight: Double, idealWeight: Double): GoalType {
        val diff = currentWeight - idealWeight
        return when {
            diff > 1.0  -> GoalType.DEFICIT
            diff < -1.0 -> GoalType.SURPLUS
            else        -> GoalType.MAINTENANCE
        }
    }

    /**
     * Target daily calories based on goal type.
     */
    fun calculateTargetCalories(tdee: Double, goalType: GoalType): Double {
        return when (goalType) {
            GoalType.DEFICIT     -> tdee - 300
            GoalType.SURPLUS     -> tdee + 300
            GoalType.MAINTENANCE -> tdee
        }
    }

    /**
     * Full calculation returning a UserProfile with all fields populated.
     */
    fun calculate(
        weightKg: Double,
        heightCm: Double,
        ageYears: Int,
        isMale: Boolean,
        hasScale: Boolean = false
    ): UserProfile {
        val idealWeight = calculateIdealWeight(heightCm, isMale)
        val bmr = calculateBMR(weightKg, heightCm, ageYears, isMale)
        val tdee = calculateTDEE(bmr)
        val goalType = determineGoalType(weightKg, idealWeight)
        val targetCalories = calculateTargetCalories(tdee, goalType)

        return UserProfile(
            weight = weightKg,
            height = heightCm,
            age = ageYears,
            isMale = isMale,
            idealWeight = idealWeight,
            bmr = bmr,
            tdee = tdee,
            goalType = goalType,
            targetCalories = targetCalories,
            breakfastCalories = targetCalories * 0.33,
            lunchCalories = targetCalories * 0.40,
            dinnerCalories = targetCalories * 0.27,
            hasScale = hasScale
        )
    }
}
