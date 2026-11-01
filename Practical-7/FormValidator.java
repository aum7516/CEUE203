import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@interface NotBlank {}

@Retention(RetentionPolicy.RUNTIME)
@interface MaxLength {
    int value();
}

class SignupForm {
    @NotBlank
    @MaxLength(10)
    String username;

    @NotBlank
    String email;

    public SignupForm(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

public class FormValidator {
    public static List<String> validate(Object obj) throws Exception {
        List<String> errors = new ArrayList<>();
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object value = f.get(obj);
            String str = (value == null) ? "" : value.toString();

            if (f.isAnnotationPresent(NotBlank.class) && str.trim().isEmpty()) {
                errors.add(f.getName() + " cannot be blank");
            }
            if (f.isAnnotationPresent(MaxLength.class)) {
                int max = f.getAnnotation(MaxLength.class).value();
                if (str.length() > max) {
                    errors.add(f.getName() + " exceeds max length of " + max);
                }
            }
        }
        return errors;
    }

    public static void main(String[] args) throws Exception {
        SignupForm validForm = new SignupForm("alice", "alice@test.com");
        System.out.println("Valid form errors: " + validate(validForm));

        SignupForm invalidForm = new SignupForm("superlongusername123", "");
        System.out.println("Invalid form errors: " + validate(invalidForm));
    }
}
