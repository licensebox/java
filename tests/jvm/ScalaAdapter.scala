import dev.licensebox.Client
import com.google.gson._
import scala.jdk.CollectionConverters._
object ScalaAdapter {
  def main(args:Array[String]):Unit = {
    val r=JsonParser.parseString(scala.io.Source.stdin.mkString).getAsJsonObject
    def s(n:String):String=if(r.has(n))r.get(n).getAsString else ""
    def run():JsonElement={
      val product=s("product")
      val keys=new java.util.LinkedHashMap[String,String]()
      if(r.has("keys"))r.getAsJsonObject("keys").entrySet.asScala.foreach(e=>keys.put(e.getKey,e.getValue.getAsString))
      s("op") match {
        case "derive" => return new JsonPrimitive(Client.deriveBinding(product,s("source"),s("raw_id")))
        case "native" => return new JsonPrimitive(Client.nativeBinding(product))
        case "verify" => return Client.verifyLease(s("token"),product,keys,r.get("now").getAsDouble)
        case _ => ()
      }
      val cfg=new Client.Config(product,keys)
      cfg.server=s("server");cfg.directory=s("directory");cfg.version="conformance"
      cfg.clock=()=>r.get("now").getAsDouble;cfg.machineBinding=()=>s("binding");cfg.onSignIn=(_,_)=>()
      val c=new Client(cfg)
      try s("op") match {
        case "lease" => c.lease();case "status" => c.status();case "sign_in" => c.signIn(3)
        case "require" => c.require(if(r.has("module"))s("module") else "core")
        case "repeat" => c.require("core");c.require("core")
        case "heartbeat" => c.heartbeat(if(r.has("module"))s("module") else "core")
        case "heartbeat-repeat" => c.heartbeat("core");c.heartbeat("core")
        case "heartbeat-after-require" => c.require("core");c.heartbeat("core")
        case "logout" => c.logout();JsonNull.INSTANCE
        case "modules" => c.modules();case "requests" => c.requests()
        case "request" => c.requestAccess(java.util.Collections.singletonList("export"),"café – test")
        case "access-link" => new JsonPrimitive(c.accessLink())
        case "record" => c.record("export",true,.125);c.record("export",false,.025);JsonNull.INSTANCE
        case _ => throw new IllegalArgumentException("unknown operation")
      } finally c.close()
    }
    val answer=new JsonObject()
    try {answer.add("value",run());answer.addProperty("ok",true)}
    catch {case e:Client.LicenseboxException => answer.addProperty("ok",false);answer.addProperty("error",e.code)}
    println(answer)
  }
}
