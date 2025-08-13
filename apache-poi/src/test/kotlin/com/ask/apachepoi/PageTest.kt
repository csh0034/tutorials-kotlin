package com.ask.apachepoi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

val HOME: String = System.getProperty("user.home")

class PageTest : FunSpec({
  test("pptx") {
    val file = File("$HOME/Documents/sample/pptx/input.pptx")

    var size = FileInputStream(file).use {
      XMLSlideShow(it).use { pptx -> pptx.slides.size }
    }

    size shouldBe 4
  }

  test("xls") {
    val file = File("$HOME/Documents/sample/xls/input.xls")

    var size = FileInputStream(file).use {
      HSSFWorkbook(it).use { workbook -> workbook.numberOfSheets }
    }

    size shouldBe 4
  }

  test("xlsx") {
    val file = File("$HOME/Documents/sample/xlsx/input.xlsx")

    var size = FileInputStream(file).use {
      XSSFWorkbook(it).use { workbook -> workbook.numberOfSheets }
    }

    size shouldBe 4
  }
})
