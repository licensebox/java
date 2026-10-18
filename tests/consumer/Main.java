import dev.licensebox.Client;
import java.nio.file.*;
import java.util.*;
public class Main {
    public static void main(String[] args) throws Exception {
        if(!Client.deriveBinding("demo","linux-machine-id","12345678123456789abc1234567890ab").startsWith("m1:"))throw new AssertionError();
        Path directory=Files.createTempDirectory("licensebox-consumer-");
        try {
            Client.Config config=new Client.Config("demo",Collections.singletonMap("test-key","A6EHv_POEL4dcN0Y50vAmWfk1jCbpQ1fHdyGZBJVMbg"));
            config.directory=directory.toString();config.server="http://127.0.0.1:1";
            try(Client client=new Client(config)) {
                try {client.heartbeat("core");throw new AssertionError("heartbeat accepted missing lease");}
                catch(Client.LicenseboxException expected) {if(!expected.code.equals("not_signed_in"))throw expected;}
            }
        } finally {
            try(java.util.stream.Stream<Path> paths=Files.walk(directory)) {
                for(Path path:(Iterable<Path>)paths.sorted(Comparator.reverseOrder())::iterator)Files.delete(path);
            }
        }
        System.out.println("PASS: packaged Java consumer and explicit heartbeat refusal");
    }
}
