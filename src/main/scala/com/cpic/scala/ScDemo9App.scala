package com.cpic.scala

object ScDemo9App {

  class Father

  class Son extends Father

  class GrantSon extends Son

  class People[+T](val name:String)

  def makeMoney(people: People[Son]): Unit ={
    println(s"i am ${people.name},and will go to work")
  }

  def main(args: Array[String]): Unit = {
    val father = new People[Father]("father")
    val son = new People[Son]("son")
    val grantSon = new People[GrantSon]("grandson")

//    makeMoney(father)
    makeMoney(son)
    makeMoney(grantSon)
  }

}
