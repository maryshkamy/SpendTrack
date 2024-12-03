package ca.on.conestogac.spendtrack.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import ca.on.conestogac.spendtrack.databinding.ExpenseListRowBinding;
import ca.on.conestogac.spendtrack.model.Expense;

public class ExpensesRecyclerViewAdapter extends RecyclerView.Adapter<ExpensesRecyclerViewAdapter.ViewHolder> {

    // Private instance of the expenses list.
    private final List<Expense> expenses;
    private final Context context;

    // Class constructor.
    public ExpensesRecyclerViewAdapter(List<Expense> expenses, Context context) {
        this.expenses = expenses;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use ViewBinding to inflate the layout
        ExpenseListRowBinding binding = ExpenseListRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the data to the ViewHolder
        Expense expense = expenses.get(position);
        holder.bindView(
                (long) (expense.getAmount() * 100), // Assuming `Expense.getAmount()` returns a float
                expense.getDescription(),
                position
        );
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ExpenseListRowBinding binding;

        public ViewHolder(ExpenseListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(final long amount, final String description, final int position) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String formattedAmount = "$" + formatter.format(amount / 100.0);

            Log.d("Description", description);

            binding.descriptionTextView.setText(description);
            binding.amountTextView.setText(formattedAmount);
        }
    }
}