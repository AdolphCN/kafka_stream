package com.cpic.spark.transformation

import org.apache.log4j.{Level, Logger}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

object Demo1 extends Logging{

  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.ERROR)
    val spark = SparkSession.builder().master("local[2]").appName("Demo1").getOrCreate()

//    InteroperationWithRdd(spark)
    InteroperationWithRdd2(spark)
  }

  /**
   * RDD 隐式转化 与显示转化
   * @param spark
   */
  def InteroperationWithRdd (spark :SparkSession): Unit ={

    val textStr = "zhangsan 22,lisi 25,wangwu 32"

    //这个时全部拆散
//    spark.sparkContext.parallelize(textStr).foreach(println)

    import spark.implicits._
    val dff2 = spark.sparkContext.parallelize(textStr.split(","))
      .map(_.split(" ")).map(x => People(x(0),x(1).toLong)).toDF()

    dff2.printSchema()
    dff2.show()

  }

  /**
   * 显示转化
   * @param spark
   */
  def InteroperationWithRdd2 (spark :SparkSession): Unit ={

    val schemaStr = "name,age"
    val fields = schemaStr.split(",").map(x => StructField(x ,StringType,nullable=true))
    val schema = StructType(fields)


    val textStr = "zhangsan 22,lisi 25,wangwu 32"

    val rowRdd = spark.sparkContext.parallelize(textStr.split(","))
      .map(_.split(" ")).map(x => Row(x(0),x(1)))

    val dff2 = spark.createDataFrame(rowRdd,schema)


    dff2.printSchema()
    dff2.show()

  }


  case class People(name :String ,age :Long)

}
