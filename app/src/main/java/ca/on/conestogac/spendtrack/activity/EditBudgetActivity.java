package ca.on.conestogac.spendtrack.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

import ca.on.conestogac.spendtrack.R;
import ca.on.conestogac.spendtrack.databinding.ActivityEditbudgetBinding;
import ca.on.conestogac.spendtrack.utils.BudgetUtils;
import ca.on.conestogac.spendtrack.utils.MessageUtils;

public class EditBudgetActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityEditbudgetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Instantiate the binding instance and load the view.
        binding = ActivityEditbudgetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // initial setup
        setup();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setup() {
        setListeners();
        setupBudgetEditText();
        setupLayout();
    }

    // set up related
    private void setupLayout () {

        // set hint to current value
        float currentBudget = BudgetUtils.getBudgetValue(this);

        String currentBudgetStr = formatBudgetAmountDisplay(currentBudget);
        binding.budgetEditText.setHint(currentBudgetStr);
        binding.currentBudgetTextView.setText(String.format("Current total budget: %s", currentBudgetStr));
    }

    private void setListeners() {
        binding.setNewBudgetButton.setOnClickListener(this);
    }

    private void setupBudgetEditText() {
        binding.budgetEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String text = v.getText().toString();

                // validate if text is empty
                if (!TextUtils.isEmpty(text)) {
                    v.setText("");

                    v.clearFocus();

                    return true;
                }
            }
            return false;
        });
    }

    // Set display format for the new budget
    private String formatBudgetAmountDisplay (float amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return "$" + formatter.format(amount);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setNewBudgetButton) {
            binding.budgetEditText.clearFocus();

            String newBudgetText = String.valueOf(binding.budgetEditText.getText()).trim();
            Float newBudget = 0f;

            if (!TextUtils.isEmpty(newBudgetText)) {
                try {
                    newBudget = Float.parseFloat(newBudgetText);
                } catch (NumberFormatException e) {
                    MessageUtils.showErrorMessage(getString(R.string.invalid_budget_error_message)
                            , this);
                    return;
                }
            }

            // Set new budget
            BudgetUtils.saveBudget(newBudget,this);

            String formattedAmount = formatBudgetAmountDisplay(newBudget);

            // Display success message
            MessageUtils.showSuccessMessage(getString(R.string.new_budget_set)
                    + " " + formattedAmount, this);

            binding.budgetEditText.setText("");
            finish();
        }
    }
}