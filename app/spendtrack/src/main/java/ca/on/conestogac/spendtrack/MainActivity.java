package ca.on.conestogac.spendtrack;

import androidx.annotation.NonNull;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ca.on.conestogac.spendtrack.adapter.ExpensesListRecyclerViewAdapter;
import ca.on.conestogac.spendtrack.databinding.ActivityMainBinding;
import ca.on.conestogac.spendtrack.model.Expense;
import ca.on.conestogac.spendtrack.utils.Constants;
import ca.on.conestogac.spendtrack.utils.DataUtils;

public class MainActivity extends AppCompatActivity implements MessageClient.OnMessageReceivedListener {

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
    protected void onStart() {
        super.onStart();
        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgressIndicator();
        updateExpensesList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Wearable.getMessageClient(this).removeListener(this);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(Constants.DATA_PATH)) {
            byte[] data = messageEvent.getData();
            processReceivedData(data);
        }
    }

    // Private method to set the initial state for any ui component.
    private void setup() {
        setupLayout();
    }

    // Private method to set the width and height for the buttons according to the device screen size.
    private void setupLayout() {
        binding.recyclerView.setHasFixedSize(true);
    }

    // Private method to process the received data.
    private void processReceivedData(byte[] data) {
        try {
            String jsonData = new String(data, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(jsonData);
            JSONObject budgetData = jsonArray.getJSONObject(0);

            float budget = (float) budgetData.getDouble("budget");
            float currentExpenses = (float) budgetData.getDouble("current_expenses");

            List<Expense> expenseList = new ArrayList<>();
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject expenseData = jsonArray.getJSONObject(i);
                String expenseId = expenseData.getString("expense_id");
                String expenseDescription = expenseData.getString("expense_description");
                float expenseAmount = (float) expenseData.getDouble("expense_amount");

                Expense expense = new Expense(expenseId, expenseAmount, expenseDescription);
                expenseList.add(expense);
            }

            DataUtils.saveData(budget, currentExpenses, expenseList, this);
            updateProgressIndicator();
            updateExpensesList();
        } catch (Exception e) {
            Log.e("MobileApp", "Error parsing received data", e);
        }
    }

    // Private method to update the circular progress indicator.
    private void updateProgressIndicator() {
        float budget = DataUtils.getBudgetValue(this);
        float currentExpenses = DataUtils.getCurrentTotalExpenseValue(this);

        float progress = (currentExpenses / budget) * 100;

        if (progress >= 0 && progress <= 50) {
            @ColorInt int greenColor = getResources().getColor(R.color.green, getTheme());
            binding.circularProgressIndicator.setIndicatorColor(greenColor);
        } else if (progress > 50 && progress <= 75) {
            @ColorInt int yellowColor = getResources().getColor(R.color.yellow, getTheme());
            binding.circularProgressIndicator.setIndicatorColor(yellowColor);
        } else if (progress > 75 && progress < 95) {
            @ColorInt int orangeColor = getResources().getColor(R.color.orange, getTheme());
            binding.circularProgressIndicator.setIndicatorColor(orangeColor);
        } else {
            @ColorInt int redColor = getResources().getColor(R.color.red, getTheme());
            binding.circularProgressIndicator.setIndicatorColor(redColor);
        }

        binding.progressTextView.setText(String.format("%s%%", (int) progress));
        binding.circularProgressIndicator.setMax(100);
        binding.circularProgressIndicator.setProgress((int) progress);
    }

    // Private method to update the expenses list.
    private void updateExpensesList() {
        List<Expense> expenseList = DataUtils.getAllExpenses(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new ExpensesListRecyclerViewAdapter(expenseList));
    }
}