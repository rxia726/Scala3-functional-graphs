package com.functionalgraphs.app

import zio.ExitCode

import zio.test._
import zio.test.Assertion._

object AppSpec extends ZIOSpecDefault {
  def spec = suite("AppSpec")(
    test("demo ne plante pas") {
      assertZIO(Main.demo.exitCode)(equalTo(ExitCode.success))
    }
  )
}
