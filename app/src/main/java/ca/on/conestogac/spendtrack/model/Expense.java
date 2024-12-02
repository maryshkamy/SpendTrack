package ca.on.conestogac.spendtrack.model;

public class Expense {
    private String id;
    private long amount;
    private String description;


    public Expense(String id, long amount, String description) {
        this.id = id;
        this.amount = amount;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
