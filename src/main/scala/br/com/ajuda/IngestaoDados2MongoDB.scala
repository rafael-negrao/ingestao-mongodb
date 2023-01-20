package br.com.ajuda

import com.mongodb.spark.config.ReadConfig
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.bson.Document

import scala.collection.mutable.ListBuffer


case class Pessoa(_id: Int, nome: String, telefone: String)

case object IngestaoDados2MongoDB extends App {

  val user = "dev"
  val password = "dev"
  val host = "localhost"
  val database = "ingestao-dados"
  val collection = "colecao-dados"
  val inputUriMongodb = s"mongodb://${user}:${password}@${host}:27017/${database}.${collection}?connectTimeoutMS=10000&authSource=admin&authMechanism=SCRAM-SHA-1"
  val outputUriMongodb = s"mongodb://${user}:${password}@${host}:27017/${database}.${collection}?connectTimeoutMS=10000&authSource=admin&authMechanism=SCRAM-SHA-1"

  implicit lazy val spark: SparkSession = {
    val builderSession = SparkSession
      .builder()
      .appName("LeituraMongoDB")
      .config("spark.master", "local") // TODO: Tirar esta configuracao para rodar em producao
      .config("spark.driver.allowMultipleContexts", true)
      .config("spark.sql.shuffle.partitions", 10)
      .config("spark.mongodb.input.uri", inputUriMongodb)
      .config("spark.mongodb.output.uri", outputUriMongodb)
      .enableHiveSupport()

    builderSession.getOrCreate()
  }

  implicit lazy val sqlContext: SQLContext = spark.sqlContext

  implicit lazy val sc: SparkContext = spark.sparkContext

  import com.mongodb.spark._
  import spark.implicits._

  gravarDados()

  lerDados()

  private def gravarDados(): Unit = {

    val df = List(
        (121, "nome teste 12", "99111-0121"),
        (131, "nome teste 13", "99111-0131")
      ).toDF("_id", "nome", "telefone")
      .map(row => Pessoa(row.getAs("_id"), row.getAs("nome"), row.getAs("telefone")))

    df.show(10, false)

    MongoSpark.save(df)
  }

  private def lerDados(): Unit = {
    val mongoRDD = MongoSpark.load(sc, ReadConfig(
      Map(
        "spark.mongodb.input.uri" -> inputUriMongodb,
        "spark.mongodb.output.uri" -> outputUriMongodb
      ) ++ spark.conf.getAll))

    val aggregatePipeline = new ListBuffer[Document]()

    // TODO definir aggregate
    //  aggregatePipeline += Document.parse(
    //    """
    //      |
    //      |""".stripMargin)

    val aggregateRDD = mongoRDD.withPipeline(aggregatePipeline)

    val schema = StructType(Seq(
      StructField("_id", IntegerType),
      StructField("nome", StringType)
    ))

    val aggregateDF = aggregateRDD.toDF()
    aggregateDF.printSchema()

    aggregateDF.show(10, false)
  }

}
