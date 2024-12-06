package ca.on.conestogac.spendtrack.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import ca.on.conestogac.spendtrack.databinding.ExpenseListRowBinding;
import ca.on.conestogac.spendtrack.model.Expense;

public class ExpensesListRecyclerViewAdapter extends RecyclerView.Adapter<ExpensesListRecyclerViewAdapter.ViewHolder> {

    // Private instance of the expenses list.
    private final List<Expense> expenseList;

    // Class constructor.
    public ExpensesListRecyclerViewAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpenseListRowBinding binding = ExpenseListRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bindView(
                (long) (expense.getAmount() * 100),
                expense.getDescription(),
                position
        );
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

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
