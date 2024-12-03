package ca.on.conestogac.spendtrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import ca.on.conestogac.spendtrack.databinding.ExpenseListRowBinding;
import ca.on.conestogac.spendtrack.model.Expense;

public class ExpensesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Instance of the generated binding class.
    ExpenseListRowBinding rowBinding;

    // Instance of the context.
    Context context;

    // Private instance of the expenses list.
    private final List<Expense> expenses;

    // Class constructor.
    public ExpensesRecyclerViewAdapter(List<Expense> expenses, Context context) {
        super();
        this.expenses = expenses;
        this.context = context;
    }

    // View Interface Method for recycler view adapter.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        rowBinding = ExpenseListRowBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ExpenseListRowBinding recyclerRowBinding;

        public ViewHolder(ExpenseListRowBinding listRowBinding) {
            super(listRowBinding.getRoot());
            this.recyclerRowBinding = listRowBinding;
        }

        void bindView(
                final long amount,
                final String description,
                final int position
        ) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String formattedAmount = "$" + formatter.format(amount / 100.0);

            recyclerRowBinding.descriptionTextView.setText(description);
            recyclerRowBinding.amountTextView.setText(formattedAmount);
        }
    }
}
