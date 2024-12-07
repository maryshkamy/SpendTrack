package ca.on.conestogac.spendtrack.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.on.conestogac.spendtrack.model.Expense;

public class DataUtils {

    // Public method to save the budget data into shared preferences.
    public static void saveData(Float budget, Float currentExpense, List<Expense> expensesList, Context context) {
        if (budget != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    Constants.SHAREDPREF_SPEND_TRACK,
                    Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("budget", budget);
            editor.putFloat("current_total_expense", currentExpense);

            JSONArray expenseArray = new JSONArray();
            try {
                for (Expense expense : expensesList) {
                    JSONObject expenseObject = new JSONObject();
                    expenseObject.put("expense_id", expense.getId());
                    expenseObject.put("expense_description", expense.getDescription());
                    expenseObject.put("expense_amount", expense.getAmount());
                    expenseArray.put(expenseObject);
                }
                editor.putString("expense_list", expenseArray.toString());
            } catch (Exception e) {
                Log.e("SharedPreferences", "Error converting expense list to JSON", e);
            }

            editor.commit();
        }
    }

    // Public method to get the budget value stored into shared preferences.
    public static float getBudgetValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.SHAREDPREF_SPEND_TRACK,
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getFloat("budget", 0f);
    }

    // Public method to get the current total expense value stored into shared preferences.
    public static float getCurrentTotalExpenseValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.SHAREDPREF_SPEND_TRACK,
                Context.MODE_PRIVATE
        );

        return sharedPreferences.getFloat("current_total_expense", 0f);
    }

    // Public method to get all expenses from shared preferences.
    public static List<Expense> getAllExpenses(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.SHAREDPREF_SPEND_TRACK,
                Context.MODE_PRIVATE
        );
        String expenseListJson = sharedPreferences.getString("expense_list", "[]");
        List<Expense> expenseList = new ArrayList<>();

        try {
            JSONArray expenseArray = new JSONArray(expenseListJson);
            for (int i = 0; i < expenseArray.length(); i++) {
                JSONObject expenseObject = expenseArray.getJSONObject(i);
                String expenseId = expenseObject.getString("expense_id");
                String expenseDescription = expenseObject.getString("expense_description");
                float expenseAmount = (float) expenseObject.getDouble("expense_amount");
                expenseList.add(new Expense(expenseId, expenseAmount, expenseDescription));
            }
        } catch (Exception e) {
            Log.e("DataUtils", "Error parsing expenses JSON", e);
        }

        return expenseList;
    }
}
