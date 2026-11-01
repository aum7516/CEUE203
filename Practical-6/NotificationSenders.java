@FunctionalInterface
interface Notifier {
    void send(String message);
}

// Marker interface with no methods
interface Urgent {}

public class NotificationSenders {
    public static void main(String[] args) {
        // Lambda for Email sender (marked Urgent via intersection type cast)
        Notifier urgentEmailSender = (Notifier & Urgent) (msg) -> 
            System.out.println("[EMAIL] Dispatching: " + msg);

        // Lambda for regular SMS sender
        Notifier smsSender = (msg) -> 
            System.out.println("[SMS] Sending text: " + msg);

        // Lambda for Push notification sender (marked Urgent)
        Notifier urgentPushSender = (Notifier & Urgent) (msg) -> 
            System.out.println("[PUSH NOTIFICATION] Alert: " + msg);

        Notifier[] notifiers = { urgentEmailSender, smsSender, urgentPushSender };

        String broadcastMessage = "System maintenance starts at 11:00 PM tonight.";
        System.out.println("Broadcasting message: \"" + broadcastMessage + "\"\n");

        for (Notifier notifier : notifiers) {
            notifier.send(broadcastMessage);
            // Check for the Urgent marker interface
            if (notifier instanceof Urgent) {
                System.out.println("   -> [URGENT] Repeating transmission:");
                notifier.send(broadcastMessage);
            }
            System.out.println();
        }
    }
}
