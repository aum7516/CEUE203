interface Switchable {
    void on();
    void off();
    boolean isOn();

    default void toggle() {
        if (isOn()) {
            off();
        } else {
            on();
        }
    }
}

class Fan implements Switchable {
    private boolean state = false;

    @Override
    public void on() {
        state = true;
        System.out.println("Fan is now spinning (ON).");
    }

    @Override
    public void off() {
        state = false;
        System.out.println("Fan has stopped (OFF).");
    }

    @Override
    public boolean isOn() {
        return state;
    }
}

class Light implements Switchable {
    private boolean state = false;

    @Override
    public void on() {
        state = true;
        System.out.println("Light is glowing (ON).");
    }

    @Override
    public void off() {
        state = false;
        System.out.println("Light is dark (OFF).");
    }

    @Override
    public boolean isOn() {
        return state;
    }
}

@FunctionalInterface
interface DeviceScheduler {
    boolean canSwitchOn(Switchable device, int hour);
}

public class RemoteControl {
    public static void main(String[] args) {
        Switchable[] devices = { new Fan(), new Light() };

        System.out.println("--- Toggling all devices ---");
        for (Switchable device : devices) {
            device.toggle(); // turns on
        }

        System.out.println("\n--- Toggling again ---");
        for (Switchable device : devices) {
            device.toggle(); // turns off
        }

        // Functional interface implementation 1: Anonymous Inner Class
        // Rule: Only allow lights during evening hours (18 to 23)
        DeviceScheduler anonymousScheduler = new DeviceScheduler() {
            @Override
            public boolean canSwitchOn(Switchable device, int hour) {
                if (device instanceof Light) {
                    return hour >= 18 && hour <= 23;
                }
                return true; // other devices permitted anytime
            }
        };

        // Functional interface implementation 2: Lambda Expression
        // Rule: Energy-saving mode - only allow switching on between 6 AM and 22 PM
        DeviceScheduler lambdaScheduler = (device, hour) -> (hour >= 6 && hour <= 22);

        System.out.println("\n--- Testing Scheduler (Anonymous Class) ---");
        int testHour1 = 14; // 2 PM
        int testHour2 = 20; // 8 PM
        System.out.println("Can Light turn on at " + testHour1 + ":00? " + 
            anonymousScheduler.canSwitchOn(devices[1], testHour1));
        System.out.println("Can Light turn on at " + testHour2 + ":00? " + 
            anonymousScheduler.canSwitchOn(devices[1], testHour2));

        System.out.println("\n--- Testing Scheduler (Lambda Expression) ---");
        int nightHour = 2; // 2 AM
        System.out.println("Can Fan turn on at " + nightHour + ":00? " + 
            lambdaScheduler.canSwitchOn(devices[0], nightHour));
        System.out.println("Can Fan turn on at 10:00? " + 
            lambdaScheduler.canSwitchOn(devices[0], 10));
    }
}
