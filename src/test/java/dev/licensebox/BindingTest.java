package dev.licensebox;
import java.util.Collections;
public class BindingTest {
    public static void main(String[] args) {
        Client.Config cfg=new Client.Config("demo",Collections.singletonMap("test","A6EHv_POEL4dcN0Y50vAmWfk1jCbpQ1fHdyGZBJVMbg"));
        cfg.server="http://127.0.0.1:1";cfg.machineBinding=()->{throw new IllegalStateException("synthetic provider error");};
        Client c=new Client(cfg);System.gc();
        try{c.require("core");throw new AssertionError("callback error allowed protected work");}
        catch(Client.LicenseboxException error){if(!error.code.equals("callback_error"))throw error;}
        finally{c.close();}
        c.close();
        try{c.status();throw new AssertionError("closed client used native handle");}catch(IllegalStateException expected){}
        System.out.println("PASS: callback errors, garbage collection and explicit disposal");
    }
}
