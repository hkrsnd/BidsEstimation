import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.By

import scala.collection.JavaConversions._

object ScrapePref {
  // 各工事の結果ページのリストページから、各結果へのリンクを取り出す
  def getResultUrls(url: String) = {
    val driver = new ChromeDriver
    driver.get(url)
//    driver.executeScript("gotoNextPage(5)")
    val parentHandle = driver.getWindowHandle()
    driver.findElement(By.linkText("工事( 84件 )")).click()
    Thread.sleep(3000)
    //　新しいタブに対応するhandleを抜き出す
    val lastWinHandle = driver.getWindowHandles.filter{x => x != parentHandle}.head
    driver.switchTo().window(lastWinHandle)

    //driver.findElement(By.linkText("詳細")).click()
    driver.findElement(By.cssSelector("a")).click()
    //driver.findElement(By.xpath("//a[contains(text(),'詳')]"))
    //river.executeScript("document.getElementsByName('omeKeyNo')[0].setAttribute('type', 'text');")
//    driver.findElement(By.name("omeKeyNo"))
    //driver.findElement(By.xpath("//input[@name='omeKeyNo']")).clear();
//    val detailform = driver.findElement(By.name("detailform"))
  }

}

object Main {
  import ScrapePref._
  def main(args: Array[String]) = {
    val result = getResultUrls("https://gprime-ebid.jp/26000/CALS/PPI_P/pages/PPI_P/PiCtBrFi01/PiCtBrFi01start.vm")
    println()
  }
}
