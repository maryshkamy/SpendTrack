package ca.on.conestogac.spendtrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ca.on.conestogac.spendtrack.R;
import ca.on.conestogac.spendtrack.databinding.ActivityMainBinding;
import ca.on.conestogac.spendtrack.utils.BudgetUtils;

public class MainActivity extends AppCompatActivity {

    // Instance of the generated binding class.
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Instantiate the binding instance and load the view.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initial setup.
        setup();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Private method to set the initial state for any ui component.
    private void setup() {

        // Validation of an existing budget value.
        if (BudgetUtils.getBudgetValue(this) == 0L) {
            BudgetUtils.saveBudget((long) 5000, this);
        }

        setAddExpenseActionButtonListener();
        setExpensesListActionButtonListener();
    }

    // Private method to set the add expense action button listener.
    private void setAddExpenseActionButtonListener() {
        binding.buttonAddExpense.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    // Private method to set the expenses list action button listener.
    private void setExpensesListActionButtonListener() {
        binding.buttonExpensesList.setOnClickListener( view -> {
        });
    }

}