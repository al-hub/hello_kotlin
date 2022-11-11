import java.io.Serializable

fun main() {

    //val a = minSerializable(Year(1969), Year(2001)) //error

    val b = minSerializable(SerializableYear(1969), SerializableYear(1802))
    val obj = MultipleBoundedClass<SerializableYear>()
}

fun <T> minSerializable(first: T, second: T): T
        where T: Comparable<T>, T: java.io.Serializable {
            val k = first.compareTo(second)
            return if (k <= 0) first else second
        }

class MultipleBoundedClass<T>
        where T: Comparable<T>, T: java.io.Serializable

class Year(val value: Int): Comparable<Year> {
    override fun compareTo(other: Year): Int =
        this.value.compareTo(other.value)
}

class SerializableYear(val value: Int): Comparable<SerializableYear>, Serializable {
    override fun compareTo(other: SerializableYear): Int =
        this.value.compareTo(other.value)
}