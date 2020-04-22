package com.cpic.scala

/**
 * 演示下Scala中的一些常用的高阶函数
 */
object ScDemo3App {

  def main(args: Array[String]): Unit = {

    val list = List(1,2,3,4,5,6,7,8,9)

//    list.map(x => x*2).foreach(println)

//    list.filter(x => x%2 == 0).foreach(pr
    /**
     * 变成元组 (1,4)(2,5)...
     * 位数不够的就省略了
     */
    List(1,2,3,6).zip(List(4,5,6,7)).foreach(println)

    /**
     * 分成两块了
     * (List(2, 4, 6, 8),List(1, 3, 5, 7, 9))
     */
    println(list.partition(x => x % 2 == 0))

    /**
     * Some(2)
     * 第一个匹配到的
     */
    println(list.find(_ % 2 == 0))

    /**
     * List(1, 2, 3, 5, 6, 7, 8)
     */
    println(List(List(1, 2, 3), List(5, 6, 7, 8)).flatten.map(_*2))

    val myList = List(List(1,2,3),List(4,5,6,7))

    /**
     * List(2, 4, 6, 8, 10, 12, 14)
     */
    println(myList.flatMap(x => x.map(_ * 2)))
  }

}
