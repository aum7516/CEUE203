package minibank.model;

import java.io.Serializable;

/**
 * Modern Java Record representing immutable bank branch metadata.
 */
public record BankInfo(String name, String branch) implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "========================================================\n"
             + "   " + name.toUpperCase() + " (" + branch + " BRANCH)\n"
             + "========================================================";
    }
}
