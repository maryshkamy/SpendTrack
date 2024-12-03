package ca.on.conestogac.spendtrack.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;

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

    @Override
    protected void onResume() {
        super.onResume();
        updateProgressIndicator();
    }

    // Private method to set the initial state for any ui component.
    private void setup() {

        // Validation of an existing budget value.
        if (BudgetUtils.getBudgetValue(this) == 0f) {
            BudgetUtils.saveBudget((float) 5000, this);
        }

        setupLayout();
        setSettingsActionButtonListener();
        setExpensesListActionButtonListener();
        setAddExpenseActionButtonListener();
        updateProgressIndicator();
    }

    // Private method to set the width and height for the buttons according to the device screen size.
    private void setupLayout() {
        WindowManager windowManager = getWindowManager();
        WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();

        int screenWidth = bounds.width();
        final int buttonSize = (int) (screenWidth * 0.15);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
        params.setMargins(8, 0, 8, 0);
        binding.buttonSettings.setLayoutParams(params);
        binding.buttonExpensesList.setLayoutParams(params);
        binding.buttonAddExpense.setLayoutParams(params);
    }

    // Private method to set the settings action button listener.
    private void setSettingsActionButtonListener() {
        binding.buttonSettings.setOnClickListener( view -> {
        });
    }

    // Private method to set the expenses list action button listener.
    private void setExpensesListActionButtonListener() {
        binding.buttonExpensesList.setOnClickListener( view -> {
        });
    }

    // Private method to set the add expense action button listener.
    private void setAddExpenseActionButtonListener() {
        binding.buttonAddExpense.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    // Private method to update the circular progress indicator.
    private void updateProgressIndicator() {
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);

        float progress = (currentExpenses / budget) * 100;

        binding.progressTextView.setText(String.format("%s%%", (int) progress));
        binding.circularProgressIndicator.setMax(100);
        binding.circularProgressIndicator.setProgress((int) progress);
    }
}