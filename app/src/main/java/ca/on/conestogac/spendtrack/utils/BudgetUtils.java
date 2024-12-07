package ca.on.conestogac.spendtrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BudgetUtils {

    // Public method to save the budget data into shared preferences.
    public static void saveBudget(Float budget, Context context) {
        if (budget != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "sharedpref_budget",
                    Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("budget", budget);
            editor.commit();
        }
    }

    // Public method to save the current expense data into shared preferences.
    public static void saveCurrentExpense(Float expense, Context context) {
        if (expense != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "sharedpref_budget",
                    Context.MODE_PRIVATE
            );
            float currentTotalExpense = sharedPreferences.getFloat(
                    "current_total_expense",
                    0f
            );
            float updatedTotalExpense = currentTotalExpense + expense;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("current_total_expense", updatedTotalExpense);
            editor.commit();
        }
    }

    // Public method to get the budget value stored into shared preferences.
    public static float getBudgetValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "sharedpref_budget",
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getFloat("budget", 0f);
    }

    // Public method to get the current total expense value stored into shared preferences.
    public static float getCurrentTotalExpenseValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "sharedpref_budget",
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getFloat("current_total_expense", 0f);
    }
}
