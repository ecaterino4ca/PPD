package lab1;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BankAccount {

    private int id;
    private volatile int balance;
    private List<TransactionLog> transactions;

    BankAccount(int id, int balance) {
        this.id = id;
        this.balance = balance;
        transactions = new ArrayList<>();
    }

    void addTransaction(TransactionLog transactionLog) {
        transactions.add(transactionLog);
    }

    @Override
    public String toString() {
        return "lab1.BankAccount{" +
                "id=" + id +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }
}
