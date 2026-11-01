package minibank.util;

import minibank.model.annotation.MaxLength;
import minibank.model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection-based runtime validator that inspects object fields for declarative
 * constraints such as @Positive and @MaxLength.
 */
public class AnnotationValidator {

    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Object to validate cannot be null."};
        }

        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();

        // Traverse class hierarchy to check declared fields in subclasses and superclasses
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);

                    // Check @Positive annotation
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive = field.getAnnotation(Positive.class);
                        if (value instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add("Field '" + field.getName() + "' with value " + num + " " + positive.message());
                            }
                        }
                    }

                    // Check @MaxLength annotation
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength maxLength = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str) {
                            if (str.length() > maxLength.value()) {
                                errors.add("Field '" + field.getName() + "' length (" + str.length() + ") exceeds maximum of " + maxLength.value());
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Unable to inspect field '" + field.getName() + "': " + e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
