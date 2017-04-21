import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.By

import java.io.File
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream

import scala.util.matching._

import scala.collection.JavaConversions._

/********************************
// TODO
日付情報の追加
 ************************/

object ScrapePref {
  // 各工事の結果ページのリストページから、各結果へのリンクを取り出す
  def getResults(url: String) = {
    val driver = new ChromeDriver
    driver.get(url)
//    driver.executeScript("gotoNextPage(5)")
    val parentHandle = driver.getWindowHandle()
    var results: List[(String,String,Long,Long,Double)] = List()
    val doboku_item_num = 83
    val juutaku_item_num = 72

    for(i <- (0 to 10)) {
      // 土木事務所
      //driver.findElement(By.linkText("工事( 84件 )")).click()
      // 住宅管理部
      driver.findElement(By.linkText("工事( " + (juutaku_item_num+1).toString + "件 )")).click()
      Thread.sleep(200)
      //　新しいタブに対応するhandleを抜き出す
      val lastWinHandle = driver.getWindowHandles.filter{x => x != parentHandle}.head
      driver.switchTo().window(lastWinHandle)

      //driver.findElement(By.linkText("詳細")).click()
      driver.executeScript("getdetail(" + i.toString + ")")
      Thread.sleep(200)
//      println(getResult(driver)._1 + " " + getResult(driver)._2)
      val result = getResult(driver)
      if (result._3 != 0 && result._4 != 0) // 落札が成立していない時0
        results = results :+  result
      driver.close()
      driver.switchTo().window(parentHandle)
    }

    //driver.executeScript("getdetail(10)")

     // driver.findElement(By.cssSelector("a")).click()
      //driver.close()
    //driver.findElement(By.xpath("//a[contains(text(),'詳')]"))
    //river.executeScript("document.getElementsByName('omeKeyNo')[0].setAttribute('type', 'text');")
//    driver.findElement(By.name("omeKeyNo"))
    //driver.findElement(By.xpath("//input[@name='omeKeyNo']")).clear();
    //    val detailform = driver.findElement(By.name("detailform"))
    driver.close()
    results
  }

  def getResult(driver: ChromeDriver): (String, String, Long, Long, Double) = {
    val name = driver.findElements(By.cssSelector("tr"))(5).findElements(By.cssSelector("font"))(1).getText
    val renamed_name = name.replaceAll("（.*?号.*?）", "")
    val place = driver.findElements(By.cssSelector("tr"))(6).findElements(By.cssSelector("font"))(1).getText
    val expected_price = parsePrice(driver.findElements(By.cssSelector("tr"))(10).findElements(By.cssSelector("font"))(1).getText)
    val min_price = parsePrice(driver.findElements(By.cssSelector("tr"))(11).findElements(By.cssSelector("font"))(1).getText)
    val ratio = (min_price.toDouble) / (expected_price.toDouble)
    (place, renamed_name, expected_price, min_price, ratio)

  }

  def parsePrice(price: String): Long = {
    val yenR = """.*?円""".r
    val yen_string = try {
      yenR.findFirstMatchIn(price).get.group(0).tail.reverse.tail.reverse
    } catch {
      case e: Exception => "0"
    }
    Scrape.parseToInt(yen_string)
  }
}

object Main {
  import ScrapePref._
  def main(args: Array[String]) = {
    val results = getResults("https://gprime-ebid.jp/26000/CALS/PPI_P/pages/PPI_P/PiCtBrFi01/PiCtBrFi01start.vm")

    // 結果の書き込み
//    val file = Paths.get("result_pref86")
//    if(Files.notExists(file)) Files.createFile(file)
    //val file = new File("result_pref86.csv")
    //val fw = new FileWriter(file)
    val fo = new FileOutputStream("result_pref86.csv")
    val writer = new OutputStreamWriter(fo, "Shift_JIS")

    writer.write("場所,案件名称,予定価格(税込み),最低制限価格(税込み),最低価格比率(最低制限価格/予定価格)\n")
    results
      .sortWith{_._5 > _._5}
      .map{ r =>
      writer.write(r._1 + "," + r._2 + "," + r._3 + "," + r._4 + "," + r._5.toString + "\n")
    }
    writer.close
    fo.close
  }
}
