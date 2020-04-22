package com.cpic.scala

/**
 * 泛型 的简单练习
 * 按照我的理解 []中是定义泛型的类型 可以写成[U:ClassTag]
 * 有些时候这种类型会被JVM擦去 所以定义ClassTag
 * 可以不传
 */


import scala.reflect.ClassTag


object ScDemo6App {


  def main(args: Array[String]): Unit = {
    //    mkStringMethod(1, 2, 3).foreach(println)
    //
    //    mkStringMethod2("a","b").foreach(println)

    //    mkArrMethod(1, 2, 3, "A").foreach(println)

//    val ss = func[String]("ss")
val ss = mkArrMethod2(func, "hhhh", "yyy")
    println(ss)

  }


  def mkStringMethod(x: Int*) = {
    Array(x: _*)
  }

  def mkStringMethod2(x: String*) = {
    Array(x: _*)
  }


  def mkArrMethod[U: ClassTag](x: U*) = {
    Array(x: _*)
  }

  def mkArrMethod2(f :String =>String, x: String, y: String) = {
    f(x).toString + ":abc:" + x + ",y:" + y
  }

  def func[U](name: U) = {
    "Hello ,I am func :" + name.toString
  }

}
