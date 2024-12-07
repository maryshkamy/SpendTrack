package ca.on.conestogac.spendtrack.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import ca.on.conestogac.spendtrack.R;
import ca.on.conestogac.spendtrack.databinding.ActivityMainBinding;
import ca.on.conestogac.spendtrack.model.Expense;
import ca.on.conestogac.spendtrack.utils.BudgetUtils;
import ca.on.conestogac.spendtrack.utils.Constants;
import ca.on.conestogac.spendtrack.utils.ExpenseUtils;
import ca.on.conestogac.spendtrack.utils.NotificationHelper;

import java.util.Calendar;

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

        // Handle system UI insets for padding.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Check budget and send notifications
        checkNotificationPermission();  // Request notification permission if needed

        // Schedule notification to trigger at 9 AM
        scheduleDailyNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgressIndicator();
        sendDataToMobile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(
                        this,
                        getString(R.string.notification_permission_granted),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                // Permission denied
                Toast.makeText(
                        this,
                        getString(R.string.notification_permission_denied),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // Private method to set the initial state for any UI component.
    private void setup() {
        // Redirects to edit budget activity screen
        if (BudgetUtils.getBudgetValue(this) == 0f) {
            setEditBudgetIntent();
        }

        setupLayout();
        setSettingsActionButtonListener();
        setExpensesListActionButtonListener();
        setAddExpenseActionButtonListener();
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

    // Private method to set the EditBudgetActivity screen.
    private void setEditBudgetIntent () {
        Intent intent = new Intent(MainActivity.this, EditBudgetActivity.class);
        startActivity(intent);
    }


    // Private method to set the settings action button listener (to change budget).
    private void setSettingsActionButtonListener() {
        binding.buttonSettings.setOnClickListener(view -> {
            setEditBudgetIntent();
        });
    }

    // Private method to set the expenses list action button listener.
    private void setExpensesListActionButtonListener() {
        binding.buttonExpensesList.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpensesListActivity.class);
            startActivity(intent);
        });
    }

    // Private method to set the add expense action button listener.
    private void setAddExpenseActionButtonListener() {
        binding.buttonAddExpense.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    // Private method to check if permission for notifications is granted
    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    Constants.REQUEST_CODE_PERMISSION);
        }
    }

    // Private method to update the circular progress indicator.
    private void updateProgressIndicator() {
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);

        if (budget <= 0) return; // Avoid division by zero

        float progress = (currentExpenses / budget) * 100;

        // Update the progress indicator color and value
        updateProgressIndicatorColor(progress);
        binding.progressTextView.setText(String.format("%s%%", (int) progress));
        binding.circularProgressIndicator.setMax(100);
        binding.circularProgressIndicator.setProgress((int) progress);
    }

    // Private method to update the circular progress indicator.
    private void updateProgressIndicatorColor(float progress) {
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
    }
    
    // Utility method to check if it's time for the daily summary
    private boolean isTimeForDailySummary() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour == 9; // 9 AM check
    }

    // Method to calculate time in milliseconds until the next 9 AM
    private long getNext9AMTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Move to the next day
        calendar.set(Calendar.HOUR_OF_DAY, 9);  // Set to 9 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    // Method to schedule the notification to be triggered at 9 AM
    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Check if the current time is 9 AM, if yes, trigger the notification immediately
        if (hour == 9) {
            sendDailyBudgetNotification();
        } else {
            // Otherwise, calculate the time until 9 AM tomorrow
            long currentTime = System.currentTimeMillis();
            long targetTime = getNext9AMTimeInMillis();
            long delay = targetTime - currentTime;

            // Schedule the notification to be triggered at the calculated delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> sendDailyBudgetNotification(), delay);
        }
    }

    // This method sends the notification about the daily budget status
    private void sendDailyBudgetNotification() {
        // Get current budget and expenses
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);

        // Calculate remaining balance
        float remainingBalance = budget - currentExpenses;

        // Format the message
        String message = getString(
                R.string.daily_budget_details_message,
                currentExpenses,
                remainingBalance,
                budget
        );

        // Send the notification with the dynamic message
        NotificationHelper.sendNotification(
                this,
                getString(R.string.daily_budget_summary),
                message
        );
    }

    // Method to be called after saving an expense, triggering the notifications
    public void triggerNotificationsAfterExpenseSaved() {
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);

        if (budget <= 0) return;

        float progress = (currentExpenses / budget) * 100;
        // Reminder for expenses after 3 days
        if (currentExpenses > 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                NotificationHelper.sendNotification(
                        this,
                        getString(R.string.reminder),
                        getString(R.string.dont_forget_to_track_your_expenses)
                );
            }, 1000 * 60 * 60 * 24 * 3); // 3 days delay
        }

        // Daily summary notification (e.g., every day at 9 AM)
        if (isTimeForDailySummary()) {
            NotificationHelper.sendNotification(
                    this,
                    getString(R.string.daily_budget_summary),
                    getString(R.string.your_current_expenses_and_remaining_budget_status)
            );
        }
    }

    // Private method for sending data to the mobile device.
    private void sendDataToMobile() {
        float budget = BudgetUtils.getBudgetValue(this);
        float currentExpenses = BudgetUtils.getCurrentTotalExpenseValue(this);
        List<Expense> expenseList = ExpenseUtils.getAllExpenses(this);

        JSONArray jsonArray = createJSONArrayData(budget, currentExpenses, expenseList);
        byte[] data = jsonArray.toString().getBytes();
        Wearable.getMessageClient(this)
                .sendMessage("", "/budget_data", data)
                .addOnSuccessListener(statusInfo ->
                        Log.d("Wearable", "Data sent successfully: " + statusInfo))
                .addOnFailureListener(e ->
                        Log.e("Wearable", "Failed to send data", e));
    }

    // Private method for the creation of a json array of data.
    private JSONArray createJSONArrayData(float budget, float currentExpenses, List<Expense> expenseList) {
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject budgetData = new JSONObject();
            budgetData.put("budget", budget);
            budgetData.put("current_expenses", currentExpenses);

            jsonArray.put(budgetData);

            for (Expense expense : expenseList) {
                JSONObject expenseData = new JSONObject();
                expenseData.put("expense_id", expense.getId());
                expenseData.put("expense_description", expense.getDescription());
                expenseData.put("expense_amount", expense.getAmount());

                jsonArray.put(expenseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
