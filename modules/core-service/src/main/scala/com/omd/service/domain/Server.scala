package com.omd.service.domain

final case class Cors(origin: String)

final case class Bindings(host: String, port: Int)

final case class Server(bindings: Bindings, cors: Cors)
