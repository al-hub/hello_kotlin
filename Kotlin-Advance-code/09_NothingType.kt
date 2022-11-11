package Generics_NothingType

fun main() {
    //TODO()
    error("File not found!")
}

interface Marshaller<out T> {
    fun marshall(json: String): T?
}

//Nothing 비어있는 것 ( 모든것의 subclass )
//Type으로만 사용, 객체가 있는것은 아니다. (다른 모든 것과 호환된다.)
object NoopMarshaller : Marshaller<Nothing> {
    override fun marshall(json: String) = null
}
