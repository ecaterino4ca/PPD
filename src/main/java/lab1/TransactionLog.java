package lab1;

import lombok.Data;

@Data
public class TransactionLog {

    private int id;
    private BankAccount from;
    private BankAccount to;
    private int amount;

    public TransactionLog(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        return "lab1.TransactionLog{" +
                "id=" + id +
                ", from=" + from.getId() +
                ", to=" + to.getId() +
                ", amount=" + amount +
                '}';
    }
}
