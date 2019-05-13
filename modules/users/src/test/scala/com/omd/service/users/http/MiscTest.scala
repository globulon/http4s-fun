package com.omd.service.users.http

import org.scalatest.{MustMatchers, WordSpecLike}

final class MiscTest extends WordSpecLike with MustMatchers {
  "banner" should {
    "be defined" in {
      banner must be(
        List(
          """
            | ____ ___                       _________                  .__
            ||    |   \______ ___________   /   _____/ ______________  _|__| ____  ____
            ||    |   /  ___// __ \_  __ \  \_____  \_/ __ \_  __ \  \/ /  |/ ___\/ __ \
            ||    |  /\___ \\  ___/|  | \/  /        \  ___/|  | \/\   /|  \  \__\  ___/
            ||______//____  >\___  >__|    /_______  /\___  >__|    \_/ |__|\___  >___  >
            |             \/     \/                \/     \/                    \/    \/
            |
   """.stripMargin
        ))
    }
  }
}
