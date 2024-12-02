package ca.on.conestogac.spendtrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.on.conestogac.spendtrack.model.Expense;

public class ExpenseUtils {

    // Public method to save the expense data into shared preferences.
    public static void save(Expense expense, Context context) {
        if (expense != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "sharedpref_expenseList",
                    Context.MODE_PRIVATE
            );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(
                    expense.getId(),
                    expense.getAmount() + "-" + expense.getDescription()
            );
            editor.commit();
            BudgetUtils.saveCurrentExpense(expense.getAmount(), context);
        }
    }

    // Public method to get all expenses from shared preferences.
    public static List<Expense> getAllExpenses(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "sharedpref_expenseList",
                Context.MODE_PRIVATE
        );
        List<Expense> expenseList = new ArrayList<>();
        Map<String, ?> map = sharedPreferences.getAll();

        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String savedTransaction = (String) entry.getValue();

            if (savedTransaction != null) {
                String[] info = savedTransaction.split("-");
                Expense task = new Expense(
                        entry.getKey().toString(),
                        Long.parseLong(info[0]),
                        info[1]
                );
                expenseList.add(task);
            }
        }

        return expenseList;
    }
}
