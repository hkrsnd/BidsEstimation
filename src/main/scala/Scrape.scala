import org.jsoup._
import collection.JavaConverters._
import java.io.File
import java.io.FileWriter

object Scrape {
  val urlstr = "http://www2.nyusatsu.city.kyoto.lg.jp/keiyaku/ebid/kouji/kekka_kouji2017a.htm"

  // 各工事の結果ページのリストページから、各結果へのリンクを取り出す
  def getResultUrls(url: String) = {
    val doc = Jsoup.connect(url).get
    val urls = doc
      .getElementsByAttributeValue("align","left")
      .select("a[target]").asScala.map{x => x.attr("abs:href")}
    urls
  }

  // 結果ページから予定価格、最低制限価格、ランダム係数を取り出す
  def getResult(url: String) = {
    val doc = Jsoup.connect(url).get()
    val place = doc
      .getElementsByTag("table")
      .first.select("tr").asScala(2).select("td").asScala(1).text
    val name = doc
      .getElementsByTag("table")
      .first.select("tr").asScala(3).select("td").asScala(1).text
    val price = doc
      .getElementsByTag("table")
      .first.select("tr").asScala(4).select("td").asScala(1).text
    val min_price = doc
      .getElementsByTag("table")
      .first.select("tr").asScala(4).select("td").asScala(3).text
    val random_coefficient = doc
      .getElementsByTag("table")
      .first.select("tr").asScala(5).select("td").asScala(1).text
    // 造園か土木工事かのフラグ
    val event = doc.getElementsByTag("table").first.select("tr").asScala(1)
      .select("td").asScala(1).text
    println(event)
    val flag = event == "造園工事" || event == "土木工事"
    println(flag)

    val min_basis = parseToInt(min_price) / parseToDouble(random_coefficient)

    val ratio = min_basis / parseToInt(price)


    (parseToInt(price), parseToInt(min_price), parseToDouble(random_coefficient), flag, place, name, ratio)
  }
  // abc,def円をIntにパース
  def parseToInt(yen: String) = {
    try {
      yen.filter{x => x >= '0' && x <= '9'}.toLong
    } catch {
      case e: Exception => 0
    }
  }
  def parseToDouble(r: String) = {
    try {
      r.toDouble
    } catch {
      case e: Exception => 0
    }
  }
  def checkResult(pmrf: (Long, Long, Double, Boolean, String, String, Double)) = {
    try {
      if(pmrf._1 >= 1000.0 && pmrf._2 >= 1000.0 && pmrf._3 >= 1.0 && pmrf._4)
        true
      else
        false
    } catch {
      case e: Exception => false
    }
  }
}
/*
object Main {
  import Scrape._

  val list_urls = List(
    "http://www2.nyusatsu.city.kyoto.lg.jp/keiyaku/ebid/kouji/kekka_kouji2016a.htm",
    "http://www2.nyusatsu.city.kyoto.lg.jp/keiyaku/ebid/kouji/kekka_kouji2016b.htm",
    "http://www2.nyusatsu.city.kyoto.lg.jp/keiyaku/ebid/kouji/kekka_kouji2016c.htm"
  )


  def main(args: Array[String]) = {
    val urls = list_urls.par.map{url => getResultUrls(url)}.flatten
    val results = urls.par.map{url => getResult(url)}
      .filter{x => checkResult(x)}
    val file = new File("result2016_name.csv")
    val filewriter = new FileWriter(file, true)

    results
    .toList
      .sortWith(_._7 > _._7)
      .map{ pmr =>
      filewriter.write(pmr._5 + "," + pmr._6 + "," + pmr._1.toString + "," + pmr._2.toString + "," + pmr._3.toString + "," + pmr._7 + "\n")
    }
    filewriter.close()
    results
      .map{ pmr =>
      println(pmr._1.toString + " ; " + pmr._2.toString + " ; " + pmr._3.toString)
    }
    println()
  }

}
 */
