import org.junit.Test;
import static org.junit.Assert.*;

public class AccountServiceTest {

    @Test
    public void testAddAccountIncreasesCount() {
        AccountService service = new AccountService();
        assertEquals(0, service.getCount());
        service.addAccount(new Account("A1", 100.0));
        assertEquals(1, service.getCount());
    }

    @Test
    public void testDepositIncreasesBalance() {
        Account a = new Account("A1", 100.0);
        a.deposit(50.0);
        assertEquals(150.0, a.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOverdraftThrowsException() {
        Account a = new Account("A1", 100.0);
        a.withdraw(200.0); // Throws IllegalArgumentException
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("AccountServiceTest");
    }
}
