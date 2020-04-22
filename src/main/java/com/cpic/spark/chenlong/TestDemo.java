package com.cpic.spark.chenlong;

import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructType;
import scala.Function1;
import scala.reflect.ClassTag;
import static org.apache.spark.sql.functions.col;

public class TestDemo {
    public static void main(String[] args) {
        SparkSession sparkSession = SparkSession.
                builder().
                appName("L-Journal").
                config("spark.some.config.option", "some-value").
                getOrCreate();


        Encoder<FinancialDataEntity> financialDataEncoder = Encoders.bean(FinancialDataEntity.class);
        //查询财务汇总表
        Dataset<FinancialDataEntity> financail_data = sparkSession.sql("select * from I17_AE_0304.I17_FINANCIAL_DATA_SUMMARY_CA").as(financialDataEncoder);
        financail_data.show();
        financail_data.map(new MapFunction<FinancialDataEntity,FinancialDataEntity>() {
            @Override
            public FinancialDataEntity call(FinancialDataEntity value) throws Exception {
                value.setAccount_no("123");
                return value;
            }
        },financialDataEncoder).show();

//       financail_data.foreach(new ForeachFunction<FinancialDataEntity>() {
//            @Override
//            public void call(FinancialDataEntity financialDataEntity) throws Exception {
//                financialDataEntity.setAccount_no("123");
//            }
//        });

        financail_data.show();
        financail_data.write().format("Hive").mode(SaveMode.Append).partitionBy("company_id").saveAsTable("I17_AE_0304.i17_journal_temp_table");
    }
}
