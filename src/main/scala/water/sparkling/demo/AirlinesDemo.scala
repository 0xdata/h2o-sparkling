package water.sparkling.demo

import hex.gbm.GBM
import water.api.dsl.H2ODsl._
import water.fvec.Frame
import water.util.Log


object AirlinesDemo extends Demo {

  override def run(conf: DemoConf): Unit = {
    // Prepare data
    // Dataset
    val dataset   = "data/allyears2k_headers.csv"
    // Row parser
    val rowParser = AirlinesParser
    // Table name for SQL
    val tableName = "airlines_table"
    // Select all flights with destination == SFO
    val query = """SELECT * FROM airlines_table WHERE dest="SFO" """

    // Connect to shark cluster and make a query over prostate, transfer data into H2O
    val frame:Frame = executeSpark[Airlines](dataset, rowParser, conf.extractor, tableName, query, sparkMaster= if (conf.local) null else conf.sparkMaster)
    Log.info("Extracted frame from Spark: ")
    Log.info(if (frame!=null) frame.toString + "\nRows: " + frame.numRows() else "<nothing>")

    // Now make a blocking call of GBM directly via Java API
    val params = (p:GBM) => { import p._
      ntrees = 100
      classification = true
      p
    }
    val gbmModel = gbm(frame, null, 0 to 12, 14, params)
    Log.info("Model built!")
  }

  override def name: String = "airlines"
}
