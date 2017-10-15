package lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 200; i++) {
            test();
        }
        System.out.println("SUCCESS");
    }

    private static void test() throws InterruptedException {
        Bank bank = initBankWithRandomAccounts();
        List<Thread> runningThreads = new ArrayList<>();

        int sumBefore = sumOfAllAccounts(bank);
        System.out.println("sum before " + sumBefore);

        for (int i = 0; i < 150; i++) {
            Thread thread = new Thread(new BankTransactionManager(bank));
            runningThreads.add(thread);
            thread.start();
        }

        synchronized (BankTransactionManager.class) {
            for (int i = 0; i < 10; i++) {
                runningThreads.get(i).join();
                runningThreads.remove(i);
            }
            int intermediatesSum = sumOfAllAccounts(bank);
            if (sumBefore != intermediatesSum) {
                System.out.println("ERROR");
            }
            System.out.println("intermediate check " + intermediatesSum);
        }

        for (Thread runningThread : runningThreads) {
            runningThread.join();
        }

        int finalSum = sumOfAllAccounts(bank);
        if (sumBefore != finalSum) {
            System.out.println("ERROR");
        }
        System.out.println("sum after " + finalSum);
    }

    private static int sumOfAllAccounts(Bank bank) {
        int i = 0;
        for (BankAccount ba : bank.getAccounts()) {
            i += ba.getBalance();
        }
        return i;
    }

    /**
     * Accounts have balance between 500 - 1001
     *
     * @return bank with initialized accounts
     */
    private static Bank initBankWithRandomAccounts() {
        Bank bank = new Bank();
        int id = 0;
        ThreadLocalRandom current = ThreadLocalRandom.current();
        //init accounts between 10 and 21
        for (int i = 0; i < current.nextInt(10, 21); i++) {
            bank.addAccount(new BankAccount(++id, current.nextInt(500, 1001)));
        }
        return bank;
    }

}
