package Practise

import scala.collection.immutable
import scala.util.control.Breaks._
import scala.io.StdIn._

object BasicSyntax {

  // Syntax for while loop
  def whileLoop(): Unit = {
    var i: Int = 0
    while (i <= 10) {
      println(i)
      i += 1
    }
  }

  def doWhileLoop(): Unit = {
    var i: Int = 0
    do {
      println(i)
      i += 1
    } while (i <= 10)
  }

  def forLoop(): Unit = {
    // for loop to iterate through Integer
    for (i <- 0 to 5)
      println(i)

    // for loop to iterate through String
    val randomLetters: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    for (i <- 0 until randomLetters.length)
      println(randomLetters(i))

    // for loop to iterate through List
    val newList: List[Int] = List(1, 2, 3, 4, 5)
    for (i <- newList)
      println("List items " + i)

    // for loop with if-conditions
    val evenList: immutable.IndexedSeq[Int] = for {i <- 1 to 10 if (i % 2) == 0} yield i
    println(evenList)
    //yield purpose : "yield" will reassign the for loop result to the variable itself
    // for eg: result of this above statement will be "Vector[2 4 6 8 10]"


    // Nested for loop
    for (i <- 1 to 5; j <- 6 to 10) println("value of i:j is --> " + i + ":" + j)

  }

  def breaksInLoops(): Unit = {
    val primeList: List[Int] = List(1, 2, 3, 5, 7, 11)

    // using "return" as break statements
    for (i <- primeList) {
      if (i == 5) return // Loop will get breaked if the value of i is equal to 5.
      print(i + ",")
      // result of this loop will be 1,2,3
    }

    // using variabe to break the loop
    var a: Int = 0
    var done: Boolean = false
    while (a < 10 && !done) {
      if (a == 6) done = true
      print(a + ",")
      a += 1
    }

    // Explicitly using breakable statements
    // import scala.util.control.Breaks._
    breakable {
      for (i <- primeList) {
        if (i == 7) {
          break
        } else {
          println(i)
        }
      }
    }

  }

  def readInputFromTerminal(): Unit = {
    println("Enter the number ....")
    val a: Int = readInt
    // if we directly use readInt we will be getting warning as "readInt method is depricated."
    // To avoid this warning we are using "import scala.io.StdIn._"
    println("Number what u have entered is : " + a)


    /*
      Similar other methods are
        readLine - to read the entire line as string
        readDouble - to read double value from terminal
        readByte - to read byte value from terminal
        readShort - to read short value from terminal
        readLong - to read long value from terminal
     */

  }

  def differentPrintStatements(): Unit ={
    val name: String = "Krishna"
    val age: Int = 25
    val weight: Double = 70.0

    // Traditional way
    println("My name is : " + name)

    // Other ways
    println(s"Hello $name")
    println(f"I am ${age + 1} and weight $weight%.2f") // Doing manipulation and triming the decimal with 2 digits
    /*
      %c --> char
      %d --> double
      %f --> float
      %s --> strings
     */

    // Alignment/justification in println statements
    printf("'%5d'\n", 5)         // '    5'
    printf("'%-5d'\n", 5)        // '5    '
    printf("'%10s'\n", "right")  // '     right'

    // default values before the statements
    printf("'%05d'\n", 5)        // '00005'
    printf("'%.5f'\n", 3.14)     // '3.14000'

    /*
      Other special characters used instead of \n are
        \b --> backslash
        \\ --> introduce slash
        \a --> alerts
     */
  }

  def StringOperations(): Unit ={
    val randSent: String = "I saw a dragon flying"

    // Print the specific index of a string
    println("2nd Index : " + randSent(2))

    // print the String length
    println("String Length : " + randSent.length)

    // Concatenate two string
    println(randSent.concat(" towards North direction"))

    // compare two string
    println("Comparison output of two strings : " + "I saw a dragon flying".equals(randSent))

    // find index position for match for the pattern inside the string
    println("Dragon starts at index : " + randSent.indexOf("dragon"))

    // string to array
    val randSentArray: Array[Char] = randSent.toArray

  }

  def functionDeclerations(): Unit ={
    /*
    Method to return value and how it can be called
    -----------------------------------------------
      def getSum(num1: Int = 1, num2: Int = 2): Int = {
        num1+num2
      }
      println("5 + 4 = " + getSum()) --> this type will return the sum with default values initialized inside the method, for eg: 5 + 4 = 3
      println("5 + 4 = " + getSum(5,4)) --> this type will return the sum with the argumes passed to this method, for eg: 5 + 4 = 9
      println("5 + 4 = " + getSum(num2=4, num1=5)) --> in this type we can explicitly mention the values for each argumets of that method in any order

     */

    /*
    Method without return type
    --------------------------
      def sayHi(): Unit = {
        println("Hi how are you")
      }
      sayHi
     */

    /*
    Method which receives bunch of arguments of same type
    -----------------------------------------------------
      def getSum(args: Int*): Int ={
        var sum: Int = 0
        for (num <- args){
          sum += num
        }
        sum
      }
      println("Get Sum of 1,2,3,4,5,6,7 : " + getSum(1,2,3,4,5,6,7))
     */

    /*
    Method with recursion
    ---------------------
      def factorial(num: BigInt): BigInt = {
        if(num <= 1) 1 else num*factorial(num-1)
      }
      println("Factorial of 4 is : " + factorial(4))

     */

    /*
      Implicit Approach
      -----------------
      val getSum = (num1: Int, num2: Int) => num1 + num2
      println(getSum(5,4))

      Explicit approach
      -----------------
      val getSum: (Int, Int) => Int = (num1,num2) => num1 + num2
      println(getSum(5,4))

      other examples
      --------------
      val f: (Int) => Boolean = i => { i % 2 == 0 }
      val f: Int => Boolean = i => { i % 2 == 0 }
      val f: Int => Boolean = i => i % 2 == 0
      val f: Int => Boolean = _ % 2 == 0

      println(f(4))
     */

    /*
    Using a method like an anonymous function
    ------------------------------------------
    Scala is very flexible, and just like you can define an anonymous function and assign it to a variable, you can also define a method and then pass it around like an instance variable. Again using a modulus example, you can define a method in any of these ways
    def modMethod(i: Int) = i % 2 == 0
    def modMethod(i: Int) = { i % 2 == 0 }
    def modMethod(i: Int): Boolean = i % 2 == 0
    def modMethod(i: Int): Boolean = { i % 2 == 0 }

    def modMethod(i: Int) = i % 2 == 0
    val list = List.range(1, 10)
    list.filter(modMethod)

    In REPL :
       scala> def modMethod(i: Int) = i % 2 == 0
          modMethod: (i: Int)Boolean
       scala> val list = List.range(1, 10)
          list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
       scala> list.filter(modMethod)
          res0: List[Int] = List(2, 4, 6, 8)
     */

  }




  def main(args: Array[String]): Unit = {
    //breaksInLoops() - Need to debug this method, some ways to print elements are not working

  }

}
