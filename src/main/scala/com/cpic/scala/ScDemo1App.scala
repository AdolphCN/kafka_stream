package com.cpic.scala

object ScDemo1App {

  def main(args: Array[String]): Unit = {
    def myfun1(name :String) = "Hello" +name

    def myfun2() = "Hello World"

    println(myfun1("TOM"))

    val mm = myfun1("Tom")

    println("myfun1(mm)  ："+myfun1(mm))


    Array(1,2,3).map(x=>x*2).foreach(println)

    val tt = """
      |ABC
      |SDF
      |SF
      |""".stripMargin

    println(tt)

    println(s"woshi : $tt")
  }

}
