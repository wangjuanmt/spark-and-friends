package com.amuos.spark;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

public class SimpleExample {
    public static void main(String[] args) {
        String logFile = "/Library/spark-2.3.1-bin-hadoop2.7/README.md";
        SparkSession spark = SparkSession.builder()
                .appName("Simple Application")
                .master("spark://localhost:7077")
                .config("spark.jars", "target/spark-and-friends-1.0-SNAPSHOT.jar")
                .getOrCreate();
        Dataset<String> logData = spark.read().textFile(logFile).cache();

        long numAs = logData.filter(s -> s.contains("a")).count();
        long numBs = logData.filter(s -> s.contains("b")).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

        spark.stop();
    }
}
