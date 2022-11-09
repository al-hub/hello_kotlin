
## TIP  
- 주석  : shift + / 

- 타입  : ctrl + shift + p : 타입확인  
- 도움말 : ctrl + q 

- 탐색   : ctrl + b ( declare 함수 내부로 분석, ctrl + left/right 이전 이후 )  

- fun   : f + enter (자동생성) 
- 자동코드 : ctrl + i

## 개념
kotlin class 만들면 무조건 final class를 만든다.  
함수를 호출하는 느낌으로 class 사용한다.  

decompile 방법 
kotlin Bytecode  
public final class 기본  


val: value (읽기 전용)  
var: variable  


data class  
decompile 시, main 쪽은 거의 비슷하나   
component1, component2, copy, toString, hashCode, equals 이 미리 정의되어 있다.(효과)     
println(p) 등을 했을때, 알아서 깔끔하게 정의&출력이 된다.  

## 설정
추가라이브러리 설치(android studio, kotlin) kotlin-coroutines-android:1.6.4   

## 특징
즉, boilerplate code를 알아서 만들어 준다.  

함수끝에 lambda 는 후행처리로 하는게 낫다.  
(coroutine과 연관)

? -> null 이 될 수 있다.  


## class 상속  
kotlin의 상속은 : 을 사용함  (java Extends )

private class 만들고, 코드 안에서  
anroid studio : code -> generator (Alt + Ins)  
secondary constructor로 전부선택 후, 생성  

private fun  
코틀린은 탑레벨도 private method 로 될 수 도 있다.  


## higher-order function  
하나의 함수를 여러가지 시나리오로 써 먹을수 있다.  
-> 실행시에 함수 결정 (functional program)  -> 즉 개발자가 전달하는 순간에 결정됨   


## 객체지향 프로그래밍  
객체중심  

## 함수형 프로그래밍  
함수중심  


## Function Literal
- Lambda, Anonymous Function (개념: 함수가 필요하면 지금 당장 만들어 쓴다 )
- FutionReference 기존함수를 그대로 써 먹고 싶다.  
