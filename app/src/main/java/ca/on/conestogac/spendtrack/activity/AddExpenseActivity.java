package ca.on.conestogac.spendtrack.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ca.on.conestogac.spendtrack.R;
import ca.on.conestogac.spendtrack.adapter.ExpensesRecyclerViewAdapter;
import ca.on.conestogac.spendtrack.databinding.ActivityAddExpenseBinding;
import ca.on.conestogac.spendtrack.model.Expense;
import ca.on.conestogac.spendtrack.utils.Constants;
import ca.on.conestogac.spendtrack.utils.MessageUtils;
import ca.on.conestogac.spendtrack.utils.ExpenseUtils;
import ca.on.conestogac.spendtrack.utils.BudgetUtils;
import ca.on.conestogac.spendtrack.utils.NotificationHelper;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    // Instance of the generated binding class.
    ActivityAddExpenseBinding binding;

    // Instance of the adapter class.
    private ExpensesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Instantiate the binding instance and load the view.
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
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

    // View Interface Method for click actions.
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveButton) {
            binding.amountEditText.clearFocus();
            binding.descriptionEditText.clearFocus();

            String amountText = String.valueOf(binding.amountEditText.getText()).trim();
            Float amount = 0f;

            if (!TextUtils.isEmpty(amountText)) {
                try {
                    amount = Float.parseFloat(amountText);
                } catch (NumberFormatException e) {
                    MessageUtils.showErrorMessage(getString(R.string.invalid_amount_error_message), this);
                    return;
                }
            }

            String description = String.valueOf(binding.descriptionEditText.getText()).trim();

            if (amount != 0L && !description.isEmpty()) {
                Expense expense = createTransaction(null, amount, description);
                saveTransaction(expense, Constants.ACTION_ADD);
            } else {
                MessageUtils.showErrorMessage(getString(R.string.add_expense_error_message), this);
            }
        }
    }

    // Private method to set the initial state for any UI component.
    private void setup() {
        setListeners();
        setupAmountEditText();
        setupDescriptionEditText();
    }

    // Private method to set all the listeners.
    private void setListeners() {
        binding.saveButton.setOnClickListener(this);
    }

    // Private method to setup the amount edit text.
    private void setupAmountEditText() {
        binding.amountEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String text = v.getText().toString();

                    if (!TextUtils.isEmpty(text)) {
                        v.setText("");
                        v.clearFocus();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // Private method to setup the description edit text.
    private void setupDescriptionEditText() {
        binding.descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String text = v.getText().toString();

                    if (!TextUtils.isEmpty(text)) {
                        v.setText("");
                        v.clearFocus();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // Private method to create a new instance of the transaction.
    private Expense createTransaction(String id, Float amount, String description) {
        if (id == null) {
            id = String.valueOf(System.currentTimeMillis());
        }
        return new Expense(id, amount, description);
    }

    // Private method to save the expense and show confirmation view.
    private void saveTransaction(Expense expense, int action) {
        if (action == Constants.ACTION_ADD) {
            ExpenseUtils.save(expense, this);
            MessageUtils.showSuccessMessage(getString(R.string.expense_saved), this);

            // Trigger notifications after saving the expense
            triggerNotifications();

            binding.amountEditText.setText("");
            binding.descriptionEditText.setText("");

            finish();
        }
    }

    // Method to trigger notifications after saving an expense
    private void triggerNotifications() {
        // Get the current budget and expenses
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);

        // Check if the user has exceeded the budget (over 100%)
        if (currentExpenses > budget) {
            // Calculate the amount over the budget
            float overAmount = currentExpenses - budget;

            // Send a notification showing how much over the budget they are
            NotificationHelper.sendNotification(
                    this,
                    getString(R.string.budget_exceeded),
                    getString(R.string.budget_exceeded_message, overAmount)
            );
        }
        // Check if the user is between 95% and 100% of the budget
        else if (currentExpenses >= 0.95 * budget) {
            NotificationHelper.sendNotification(
                    this,
                    getString(R.string.budget_warning),
                    getString(R.string.budget_warning_message, (budget - currentExpenses))
            );
        }
        // Check if the user has reached 50% of the budget
        else if (currentExpenses >= 0.5 * budget) {
            NotificationHelper.sendNotification(
                    this,
                    getString(R.string.budget_progress),
                    getString(R.string.budget_progress_message, (budget - currentExpenses))
            );
        }
    }
}
