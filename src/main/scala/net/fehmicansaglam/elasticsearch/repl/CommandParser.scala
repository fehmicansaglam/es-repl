package net.fehmicansaglam.elasticsearch.repl

import org.parboiled2._

class CommandParser(val input: ParserInput) extends Parser {

  def CommandLine = rule {
    WhiteSpace ~ Command ~ EOI
  }

  def Command = rule {
    DisconnectCommand | ConnectCommand | GetCommand | IndexCommand | DeleteCommand | SearchCommand | CountCommand | GetMappingsCommand | ClusterHealthCommand | ReindexCommand
  }

  def ConnectCommand: Rule1[Connect] = rule {
    "connect" ~ (HostDefinition | (push("localhost") ~ push(9200))) ~> Connect
  }

  def DisconnectCommand: Rule1[Disconnect.type] = rule {
    "disconnect" ~ push(Disconnect)
  }

  def GetCommand: Rule1[Get] = rule {
    "get" ~ Id ~ "from" ~ IndexDefinition ~> ((id: String, index: String) => Get(index, id))
  }

  def IndexCommand: Rule1[Index] = rule {
    "index" ~ "into" ~ IndexDefinition ~ JsonDefinition ~> Index
  }

  def DeleteCommand: Rule1[Delete] = rule {
    "delete" ~ Id ~ "from" ~ IndexTypeDefinition ~> ((id: String, index: String, typ: String) => Delete(index, typ, id))
  }

  def SearchCommand: Rule1[Search] = rule {
    "search" ~ "in" ~ IndexDefinition ~ QueryDefinition.? ~> Search
  }

  def CountCommand: Rule1[Count] = rule {
    "count" ~ IndexDefinition ~ QueryDefinition.? ~> Count
  }

  def GetMappingsCommand: Rule1[GetMappings] = rule {
    "mappings" ~ IndexDefinition ~> GetMappings
  }

  def ClusterHealthCommand: Rule1[ClusterHealth.type] = rule {
    "health" ~ push(ClusterHealth)
  }

  def ReindexCommand: Rule1[Reindex] = rule {
    "reindex" ~ IndexDefinition ~ "to" ~ IndexDefinition ~> Reindex
  }

  def HostDefinition: Rule2[String, Int] = rule {
    (Host ~ Port) | (Host ~ push(9200))
  }

  def Host: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Visible)) ~ WhiteSpace
  }

  def Port: Rule1[Int] = rule {
    capture(oneOrMore(CharPredicate.Digit)) ~> ((port: String) => port.toInt) ~ WhiteSpace
  }

  def Id: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Visible)) ~ WhiteSpace
  }

  def IndexDefinition: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Visible -- '/')) ~ WhiteSpace
  }

  def TypeDefinition: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Visible -- '/')) ~ WhiteSpace
  }

  def IndexTypeDefinition: Rule2[String, String] = rule {
    IndexDefinition ~ "/" ~ TypeDefinition
  }

  def JsonDefinition: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Printable)) ~ WhiteSpace
  }

  def QueryDefinition: Rule1[String] = rule {
    capture(oneOrMore(CharPredicate.Visible)) ~ WhiteSpace
  }

  def WhiteSpace = rule {
    zeroOrMore(WhiteSpaceChar)
  }

  val WhiteSpaceChar = CharPredicate(" \n\r\t\f")

  implicit def wspStr(s: String): Rule0 = rule {
    str(s) ~ WhiteSpace
  }

}
