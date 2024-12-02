package ca.on.conestogac.spendtrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BudgetUtils {

    // Public method to save the budget data into shared preferences.
    public static void saveBudget(Long budget, Context context) {
        if (budget != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "sharedpref_budget",
                    Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("budget", budget);
            editor.commit();
        }
    }

    // Public method to save the current expense data into shared preferences.
    public static void saveCurrentExpense(Long expense, Context context) {
        if (expense != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "sharedpref_budget",
                    Context.MODE_PRIVATE
            );
            long currentTotalExpense = sharedPreferences.getLong(
                    "current_total_expense",
                    0L
            );
            long updatedTotalExpense = currentTotalExpense + expense;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("current_total_expense", updatedTotalExpense);
            editor.commit();
        }
    }

    // Public method to get the budget value stored into shared preferences.
    public static long getBudgetValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "sharedpref_budget",
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getLong("budget", 0L);
    }

    // Public method to get the current total expense value stored into shared preferences.
    public static long getCurrentTotalExpenseValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "sharedpref_budget",
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getLong("current_total_expense", 0L);
    }
}
