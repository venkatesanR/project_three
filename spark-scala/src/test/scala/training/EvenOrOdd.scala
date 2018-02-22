package training

import scala.io.StdIn._


object EvenOrOdd {

  def evenOrOdd(): Unit = {
    println("Enter the number ... ")
    if (readInt() % 2 == 0) println("Even") else println("odd"

      // bit shifting
  }

  def factorial(num : Int): BigInt = num match{
    /*
    Traditional approach
    --------------------
    if (num == 0) 1 else num * factorial(num-1)

    Note: This method can handle big int also if we change the argument type
     */

    /*
    Using Switch Case
    -----------------
     */
    case 0 => 1
    case _ => num * factorial(num-1)

  }

  def primeNumber(num : Int): Unit ={
    for (i <- 1 to num if i*i < num) {
        if (num%i != 0){
          println("prime")
        }
    }
  }


  def main(args: Array[String]): Unit = {
    primeNumber(5)
  }
}
