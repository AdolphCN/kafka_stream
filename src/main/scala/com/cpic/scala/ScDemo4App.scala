package com.cpic.scala

/**
 * 集合 Map
 * 列表 List
 * 序列 Vector Range
 * 集 Set
 */
object ScDemo4App {

  def main(args: Array[String]): Unit = {
    //    mapMethod()

            listMethod()

    //    vectorMethod()

    setMethod()
  }

  def mapMethod(): Unit = {

    //不可变集合
    val map = scala.collection.immutable.Map("zhangsan" -> "23", "lisi" -> "22")

    println(map.getOrElse("zhangsan", -1))

  }

  def listMethod(): Unit = {
    //不可变列表
    val list = List(1, 2, 3, 4)
    println(list.head) //1
    println(list.tail) //List(2, 3, 4)
    println(list.isEmpty) //false

    println(list.mkString("||"))
    //可变列表 不推荐使用！
    val myList = scala.collection.mutable.LinkedList(1, 2, 3, 4)
    var cur = myList
    while (cur != Nil) {
      cur.elem = cur.elem * 2
      cur = cur.next
    }
    println(myList)

  }

  /**
   * 序列
   */
  def vectorMethod(): Unit = {
    val vec = Vector(1, 3, 5, 6)
    println(vec.find(_ % 2 == 0)) //Some(6)

    println(vec.updated(2, 100)) //Vector(1, 3, 100, 6)

    println("first" + Range(0, 5)) //fristRange(0, 1, 2, 3, 4)

    println("second:" + (0 to 5)) //second:Range(0, 1, 2, 3, 4, 5)

    println((0 until 5)) //Range(0, 1, 2, 3, 4)
  }

  def setMethod(): Unit = {
    val ss = Set(1, 2, 3)
    println(ss + 1)
    println(ss + 4)

    println(ss.contains(1))

    val ss2 = Set(2)
    println(ss2.subsetOf(ss))

    println(ss.intersect(ss2))    //Set(2)
    println(ss.drop(2))
  }

}
