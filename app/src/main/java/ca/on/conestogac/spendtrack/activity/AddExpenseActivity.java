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

    // Private method to set the initial state for any ui component.
    private void setup() {
        setListeners();
        setupAmountEditText();
        setupDescriptionEditText();
    }

    // Private method to set all the listeners.
    private void setListeners() {
        binding.saveButton.setOnClickListener(this);
    }

    // Private method to setup the edit text for the amount.
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

    // Private method to setup the edit text for the description.
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

    // Private method to save expense and show confirmation view.
    private void saveTransaction(Expense expense, int action) {
        if (action == Constants.ACTION_ADD) {
            ExpenseUtils.save(expense, this);
            MessageUtils.showSuccessMessage(getString(R.string.expense_saved), this);

            binding.amountEditText.setText("");
            binding.descriptionEditText.setText("");

            finish();
        }
    }
}