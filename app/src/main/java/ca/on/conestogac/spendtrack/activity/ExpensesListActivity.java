package ca.on.conestogac.spendtrack.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import ca.on.conestogac.spendtrack.adapter.ExpensesRecyclerViewAdapter;
import ca.on.conestogac.spendtrack.databinding.ActivityExpensesListBinding;
import ca.on.conestogac.spendtrack.model.Expense;
import ca.on.conestogac.spendtrack.utils.ExpenseUtils;

public class ExpensesListActivity extends AppCompatActivity {

    private ActivityExpensesListBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityExpensesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve all expenses
        List<Expense> expenseList = ExpenseUtils.getAllExpenses(this);

        // Set up RecyclerView
        binding.recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewExpenses.setAdapter(new ExpensesRecyclerViewAdapter(expenseList, this));
    }
}