package com.cpic.scala


/**
 * 上下文界定
 */
object ScDemo8App {

  def main(args: Array[String]): Unit = {
    implicit val str = new Stringer[String]
    foo("ab", "ss")
//    foo2("ab","sss")
  }

  class Stringer[T] {
    def printStr(x: T, y: T): Unit = {
      println(s"$x~~~~~~~~~$y")
    }
  }

  def foo(x: String, y: String)(implicit stt: Stringer[String]): Unit = {
    stt.printStr(x, y)
  }

  def foo2[T: Stringer](x: T, y: T): Unit = {
    val stt: Stringer[T] = implicitly[Stringer[T]]
    stt.printStr(x,y)
  }


}
