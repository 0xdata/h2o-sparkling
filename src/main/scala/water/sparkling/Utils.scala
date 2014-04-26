package water.sparkling

import org.apache.spark.rdd.RDD
import water.Key
import java.io.File

object Utils {
  def rddKey(rdd: RDD[_]): Key = Key.make(rdd.name+"_"+rdd.id)
  def tmpFile(rdd: RDD[_]): File = {
    val f = new File(new File(System.getProperty("java.io.tmpdir")), "rdd_"+rdd.name+"_"+rdd.id+".csv")
    if (f.exists()) rm(f)
    f
  }
  def partFile(f:File): File = new File(f, "part-00000") // FIXME: find definition of this part file name
  /** Recursive delete */
  def rm(file: File) {
    if(file == null) {
      return
    } else if(file.isDirectory) {
      val files = file.listFiles()
      if(files != null) {
        for(f <- files)
          rm(f)
      }
      file.delete()
    } else {
      file.delete()
    }
  }

}