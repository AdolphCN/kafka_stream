package com.cpic.scala

import scala.math._

/**
 * 初识Scala中的高阶函数
 */
object ScDemo2App {


  def main(args: Array[String]): Unit = {
//    println(someAction(sqrt))
    println(highTest(mytest,1,2.6))
  }

  def someAction(f: Double => Double) = f(10).toInt

  def mytest(x: Int, y: Double) = (x * y + 100).toInt

  /**
   *
   * @param f  函数名
   * @param x
   * @param y
   * @return
   *         f: (Int, Double) => Int
   *         函数 两个参数        返回类型
   */
  def highTest(f: (Int, Double) => Int, x: Int, y: Double) = f(x, y)
}
