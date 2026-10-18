import dev.licensebox.Client
import com.google.gson.*
fun main() {
    val r=JsonParser.parseString(System.`in`.bufferedReader().readText()).asJsonObject
    fun s(n:String)=r[n]?.asString ?: ""
    fun run(): JsonElement {
        val product=s("product")
        val keys=r.getAsJsonObject("keys")?.entrySet()?.associate { it.key to it.value.asString } ?: emptyMap()
        when(s("op")) {
            "derive" -> return JsonPrimitive(Client.deriveBinding(product,s("source"),s("raw_id")))
            "native" -> return JsonPrimitive(Client.nativeBinding(product))
            "verify" -> return Client.verifyLease(s("token"),product,keys,r["now"].asDouble)
        }
        val cfg=Client.Config(product,keys).apply {
            server=s("server"); directory=s("directory"); version="conformance"
            clock=java.util.function.DoubleSupplier { r["now"].asDouble }
            machineBinding=java.util.function.Supplier { s("binding") }
            onSignIn=java.util.function.BiConsumer { _,_ -> }
        }
        Client(cfg).use { c ->
            return when(s("op")) {
                "lease" -> c.lease(); "status" -> c.status(); "sign_in" -> c.signIn(3.0)
                "require" -> c.require(if(r.has("module")) s("module") else "core")
                "repeat" -> {c.require("core");c.require("core")}
                "logout" -> {c.logout();JsonNull.INSTANCE}
                "modules" -> c.modules(); "requests" -> c.requests()
                "request" -> c.requestAccess(listOf("export"),"café – test")
                "access-link" -> JsonPrimitive(c.accessLink())
                "record" -> {c.record("export",true,.125);c.record("export",false,.025);JsonNull.INSTANCE}
                else -> error("unknown operation")
            }
        }
    }
    val answer=JsonObject()
    try {answer.add("value",run());answer.addProperty("ok",true)}
    catch(e:Client.LicenseboxException) {answer.addProperty("ok",false);answer.addProperty("error",e.code)}
    println(answer)
}
