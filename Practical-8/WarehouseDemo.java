import java.util.HashMap;
import java.util.Map;

class OutOfStockException extends Exception {
    private final int shortfall;

    public OutOfStockException(String item, int shortfall) {
        super("Insufficient stock for '" + item + "'. Shortfall: " + shortfall);
        this.shortfall = shortfall;
    }

    public int getShortfall() {
        return shortfall;
    }
}

class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}

class Warehouse {
    private final Map<String, Integer> inventory = new HashMap<>();

    public void addStock(String item, int qty) {
        inventory.put(item, inventory.getOrDefault(item, 0) + qty);
    }

    public void issue(String item, int qty) throws OutOfStockException, InvalidQuantityException {
        if (qty <= 0) {
            throw new InvalidQuantityException("Requested quantity must be positive. Provided: " + qty);
        }

        int available = inventory.getOrDefault(item, 0);
        if (available < qty) {
            int shortfall = qty - available;
            throw new OutOfStockException(item, shortfall);
        }

        inventory.put(item, available - qty);
        System.out.println("Issued " + qty + " units of '" + item + "'. Remaining stock: " + (available - qty));
    }
}

public class WarehouseDemo {
    record Request(String item, int qty) {}

    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        warehouse.addStock("Laptops", 10);
        warehouse.addStock("Monitors", 4);
        warehouse.addStock("Keyboards", 5);

        System.out.println("Initial Warehouse Inventory initialized.\n");

        Request[] requests = {
            new Request("Laptops", 3),     // Valid
            new Request("Monitors", -2),    // InvalidQuantityException (qty <= 0)
            new Request("Monitors", 6),     // OutOfStockException (available: 4, shortfall: 2)
            new Request("Printers", 1),     // OutOfStockException (available: 0, shortfall: 1)
            new Request("Keyboards", 5),    // Valid (takes all 5)
            new Request("Laptops", 0)       // InvalidQuantityException (qty <= 0)
        };

        for (Request req : requests) {
            System.out.println("Processing request: " + req.qty() + " of '" + req.item() + "'");
            try {
                warehouse.issue(req.item(), req.qty());
            } catch (InvalidQuantityException e) {
                System.out.println("   [Error - Invalid Quantity] " + e.getMessage());
            } catch (OutOfStockException e) {
                System.out.println("   [Error - Out of Stock] " + e.getMessage() + 
                    " (Please reorder " + e.getShortfall() + " more units)");
            }
            System.out.println();
        }

        System.out.println("All order requests processed without interrupting the run.");
    }
}
