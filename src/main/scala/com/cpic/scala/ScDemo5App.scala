package com.cpic.scala

/**
 * match 模式匹配
 */
object ScDemo5App {


  def main(args: Array[String]): Unit = {
    //    test1()
    //    test2()

    //    test3()

    test4()
  }

  def test1(): Unit = {
    var index = 1

    val sign = "="

    sign match {
      case "+" => index = 10
      case "-" => index = 20
      case _ if index > 0 => index = 100
      case _ => index = 200
    }

    println(index)
  }

  /**
   * 注意是单引号 匹配字符
   */
  def test2(): Unit = {
    val str = "Hello World"

    str(2) match {
      case 'w' => println("this is w")
      case 'e' => println("this is e")
      case 'l' => println("this is l")
      case _ => println("Nothing")
    }
  }

  def test3(): Unit = {
    val tt: Any = 4d

    tt match {
      case x: String => println("this is String" + x)
      case s: Double => println("this is Double" + s)
      case y: Int => println("this is Int" + y)
      case _ => println("this is anything")
    }
  }

  def test4(): Unit = {
    val list = List(1, 2, 3)

    list match {
      case List(0) => println("list 0")
      case List(x, y) => println("two:" + (x + y))
      case List(x, y, z) => println("three:" + (x + y + z))
      case _ => println("this is nothing")
    }
  }
}
