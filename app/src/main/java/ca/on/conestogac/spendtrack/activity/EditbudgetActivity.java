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

public class EditbudgetActivity extends AppCompatActivity implements View.OnClickListener {

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
    }

    private void setListeners() {
        binding.setNewBudgetButton.setOnClickListener(this);
    }

    private void setupBudgetEditText() {
        binding.budgetEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String text = v.getText().toString();

                if (!TextUtils.isEmpty(text)) {
                    v.setText("");

                    v.clearFocus();

                    return true;
                }
            }
            return false;
        });
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

            // Set display format for the new budget
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String formattedAmount = "$" + formatter.format(newBudget);

            // Display success message
            MessageUtils.showSuccessMessage(getString(R.string.new_budget_set)
                    + " " + formattedAmount, this);

            binding.budgetEditText.setText("");

            finish();
        }
    }
}