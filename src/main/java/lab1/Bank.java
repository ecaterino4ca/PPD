package lab1;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Bank {

    private List<BankAccount> accounts;
    private List<TransactionLog> transactionLogs;

    public Bank(){
        accounts = new ArrayList<>();
        transactionLogs = new ArrayList<>();
    }

    public void addAccount(BankAccount bankAccount) {
        accounts.add(bankAccount);
    }
}
