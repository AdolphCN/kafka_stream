package com.cpic.scala

/**
 * 上界与下界  <:    >:
 * 视图界定 <%
 */
object ScDemo7App {
  def main(args: Array[String]): Unit = {
    sparkStr("Hello", "World")

    sparkStr2(100, 200)
  }

  def sparkStr[U <: String](x: U, y: U): Unit = {
    println(x + "~~~~~~~" + y)
  }

  def sparkStr2[U <% String](x: U, y: U): Unit = {
    println(s"$x~~~~~~~~2~~~~~~~$y")
  }

  implicit def intToStr(x: Int): String = {
    x.toString
  }
}
