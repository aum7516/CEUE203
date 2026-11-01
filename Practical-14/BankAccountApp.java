import java.util.ArrayList;
import java.util.List;

// Model Layer
class Account {
    private String id;
    private double balance;

    public Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() { return id; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        balance -= amount;
    }
}

// Service Layer
class AccountService {
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    public int getCount() {
        return accounts.size();
    }

    public Account getAccount(String id) {
        for (Account a : accounts) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }
}

// Presentation Layer
public class BankAccountApp {
    public static void main(String[] args) {
        AccountService service = new AccountService();

        service.addAccount(new Account("A101", 500.0));
        service.addAccount(new Account("A102", 1000.0));
        System.out.println("Total accounts created: " + service.getCount());

        Account a = service.getAccount("A101");
        a.deposit(200.0);
        System.out.println("A101 balance after deposit: " + a.getBalance());

        a.withdraw(100.0);
        System.out.println("A101 balance after withdrawal: " + a.getBalance());
    }
}
