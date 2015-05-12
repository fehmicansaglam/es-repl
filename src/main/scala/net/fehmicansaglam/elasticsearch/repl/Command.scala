package net.fehmicansaglam.elasticsearch.repl

sealed trait Command

case class Connect(host: String, port: Int) extends Command

case object Disconnect extends Command

case class Get(index: String, id: String) extends Command

case class Index(index: String, json: String) extends Command

case class Delete(index: String, typ: String, id: String) extends Command

case class Drop(index: String) extends Command

case class Search(index: String, query: Option[String]) extends Command

case class Count(index: String, query: Option[String]) extends Command

case class GetMappings(index: String) extends Command

case object ClusterHealth extends Command

case class Reindex(sourceIndex: String, targetIndex: String) extends Command
