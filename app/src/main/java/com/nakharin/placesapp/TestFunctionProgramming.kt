package com.nakharin.placesapp

class TestFunctionProgramming {

    private val firstClassFunction: () -> Unit = {
        println("Hello Function")
    }

    val lambdaFunction: (Int, String) -> String = { i, s ->
        "2 parameter, Int = $i, String = $s"
    }

    public fun main() {

        /**
         * First Class Function
         */

        firstClassFunction

        println(firstClassFunction)

        /**
         * Lambda Function
         */

        val message = lambdaFunction(50, "Apple")
        println(message)

        println(lambdaFunction.invoke(18, "Mango"))

        /**
         * Higher order function
         */

        fun getStringFromNetwork(callback: (String) -> Unit) {
            val string = "String from network"
            callback(string)
        }

        getStringFromNetwork {
            println(it)
        }

        getStringFromNetwork {
            println(it)
        }

        fun introduce(name: String, sentence: (String) -> String) : String {
            return sentence(name)
        }

        val intro = introduce("Nakharin") {
            "Hi, my name is $it."
        }

        println(intro)

        fun introduce2(name: String, lastName: String, sentence: (String, String) -> String) : String {
//            return sentence(name, "")
            return sentence(name, lastName)
        }

        val intro2 = introduce2("Nakharin", "Sanguansom") { s0, s1->
            "Hi, $s0 $s1"
        }

        println(intro2)

        /**
         * Pure function
         */

        fun add(i: Int, j: Int) : Int {
            return i + j
        }

        val k = add(5, 50)
        println(k)

        /**
         * Non Pure function
         */
        val nonPure = NonPure()
        nonPure.number = 10
        val pi = nonPure.add(5, 10)
        println(pi)
    }

    class NonPure {
        var number: Int = 0

        fun add(i: Int, j: Int): Double {
            return number + i + j + Math.PI
        }
    }
}