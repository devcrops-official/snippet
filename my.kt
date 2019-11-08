fun main() {
    val rewritedUrl = "/cervedApiB2B/v1/purchase/request/1234/format/json".rewriteUrl(
        	"/cervedApiB2B/v1/purchase/request/*/format/*",
            "/apiEconomyB2b/v1_0/b2b/purchase/request/*/format/*")
    println(rewritedUrl)
}

fun String.rewriteUrl(sourceTemplate: String, destTemplate: String): String {
    var result = destTemplate
    val regex = sourceTemplate.replace("*", "([^/]*)").toRegex()
    val paramsList = (regex.find(this)?.groupValues)?.drop(1) ?: throw IllegalArgumentException("Check your template!!!")
    for (p in paramsList)
      result=result.replaceFirst("*",p)	    
    return result
}
