package dev.licensebox;
import com.google.gson.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
public class Adapter {
    static String s(JsonObject r,String name){return r.has(name)?r.get(name).getAsString():"";}
    static JsonElement run(JsonObject r) {
        String op=s(r,"op"),product=s(r,"product");Map<String,String> keys=new LinkedHashMap<>();
        if(r.has("keys"))r.getAsJsonObject("keys").entrySet().forEach(e->keys.put(e.getKey(),e.getValue().getAsString()));
        if(op.equals("derive"))return new JsonPrimitive(Client.deriveBinding(product,s(r,"source"),s(r,"raw_id")));
        if(op.equals("native"))return new JsonPrimitive(Client.nativeBinding(product));
        if(op.equals("verify"))return Client.verifyLease(s(r,"token"),product,keys,r.get("now").getAsDouble());
        Client.Config cfg=new Client.Config(product,keys);cfg.server=s(r,"server");cfg.directory=s(r,"directory");cfg.version="conformance";cfg.clock=()->r.get("now").getAsDouble();cfg.machineBinding=()->s(r,"binding");cfg.onSignIn=(u,c)->{};
        try(Client c=new Client(cfg)) {
            switch(op) {
                case "lease":return c.lease();case "status":return c.status();case "sign_in":return c.signIn(3);
                case "require":return c.require(r.has("module")?s(r,"module"):"core");
                case "repeat":c.require("core");return c.require("core");
                case "heartbeat":return c.heartbeat(r.has("module")?s(r,"module"):"core");
                case "heartbeat-repeat":c.heartbeat("core");return c.heartbeat("core");
                case "heartbeat-after-require":c.require("core");return c.heartbeat("core");
                case "logout":c.logout();return JsonNull.INSTANCE;
                case "modules":return c.modules();case "requests":return c.requests();
                case "request":return c.requestAccess(Collections.singletonList("export"),"café – test");
                case "access-link":return new JsonPrimitive(c.accessLink());
                case "record":c.record("export",true,.125);c.record("export",false,.025);return JsonNull.INSTANCE;
                default:throw new IllegalArgumentException("unknown operation");
            }
        }
    }
    public static void main(String[] args)throws Exception {
        JsonObject r=JsonParser.parseString(new String(System.in.readAllBytes(),StandardCharsets.UTF_8)).getAsJsonObject();JsonObject answer=new JsonObject();
        try{answer.add("value",run(r));answer.addProperty("ok",true);}catch(Client.LicenseboxException e){answer.addProperty("ok",false);answer.addProperty("error",e.code);}
        System.out.println(answer);
    }
}
