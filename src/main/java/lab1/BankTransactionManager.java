package lab1;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BankTransactionManager implements Runnable {

    private static int atomicInteger = 0;
    //use the list to choose from the accounts, to perform the required operation
    private List<BankAccount> accounts;
    private List<TransactionLog> transactionLogs;

    BankTransactionManager(Bank bank) {
        this.accounts = bank.getAccounts();
        transactionLogs = bank.getTransactionLogs();
    }

    //here need to generate the uuid for lab1.TransactionLog (pb it will be atomicInteger)
    @Override
    public void run() {

        ThreadLocalRandom current = ThreadLocalRandom.current();

        for (int i = 0; i < 5; i++) {
            TransactionLog transactionLog = new TransactionLog(getIncrementedAtomicInt());
            //obtain two accounts to perform operation
            int pos1 = current.nextInt(0, accounts.size());
            int pos2 = current.nextInt(0, accounts.size());

            while (pos1 == pos2) {
                pos2 = current.nextInt(0, accounts.size());
            }

            BankAccount transferFrom = accounts.get(pos1);
            BankAccount transferTo = accounts.get(pos2);

            //critical section, need to be synchronize
            synchronized (BankTransactionManager.class) {
                //we have two accounts to perform operation
                int sumToTransfer = current.nextInt(0, transferFrom.getBalance());
                transactionLog.setAmount(sumToTransfer);
                transferFrom.setBalance(transferFrom.getBalance() - sumToTransfer);
                transferTo.setBalance(transferTo.getBalance() + sumToTransfer);
                transactionLogs.add(transactionLog);
                transferFrom.addTransaction(transactionLog);
                transferTo.addTransaction(transactionLog);
            }

            transactionLog.setFrom(transferFrom);
            transactionLog.setTo(transferTo);
        }

    }

    private int getIncrementedAtomicInt() {
        synchronized (BankTransactionManager.class) {
            return ++atomicInteger;
        }
    }
}
