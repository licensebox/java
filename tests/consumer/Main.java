import dev.licensebox.Client;
public class Main {
    public static void main(String[] args){if(!Client.deriveBinding("demo","linux-machine-id","12345678123456789abc1234567890ab").startsWith("m1:"))throw new AssertionError();System.out.println("PASS: packaged Java consumer");}
}
