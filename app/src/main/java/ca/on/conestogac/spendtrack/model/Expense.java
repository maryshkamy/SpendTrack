package ca.on.conestogac.spendtrack.model;

public class Expense {
    private String id;
    private float amount;
    private String description;


    public Expense(String id, float amount, String description) {
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

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
