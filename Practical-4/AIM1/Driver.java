public class Driver {
    public static void main(String[] args) {
        String[] passwords = {
            "abc",
            "abcdefg",
            "Abcdefgh",
            "abcdefg1",
            "Abcd1234!",
            "Password1"
        };

        for (String pw : passwords) {
            System.out.println("Password: " + pw);
            System.out.println("Length rule: " + (PasswordChecker.hasLength(pw) ? "Passed" : "Failed"));
            System.out.println("Uppercase rule: " + (PasswordChecker.hasUppercase(pw) ? "Passed" : "Failed"));
            System.out.println("Digit rule: " + (PasswordChecker.hasDigit(pw) ? "Passed" : "Failed"));
            System.out.println("Special character rule: " + (PasswordChecker.hasSpecial(pw) ? "Passed" : "Failed"));
            System.out.println("Strength: " + PasswordChecker.strength(pw));
            System.out.println();
        }
    }
}
