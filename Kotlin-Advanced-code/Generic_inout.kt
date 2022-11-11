fun main() {
        //당연히 에러 나는 코드
//    val obj1: MyClass1<A> = MyClass1<B>()
//    val obj1: MyClass1<A> = MyClass1<B>()

    //Covariance: out 출력으로만 사용하면 문제 없다. (일반적)
    //               과일           사과
    val obj2: MyClass2<A> = MyClass2<B>()

    //Contravariance
    //               사과           과일
    val obj3: MyClass3<B> = MyClass3<A>()
}

open class A {}

class B : A() {}

class MyClass1<T> {}

class MyClass2<out T> {}

class MyClass3<in T> {}