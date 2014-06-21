package net.fehmicansaglam.elasticsearch.repl

import org.parboiled2._

trait Command
case class Connect(host: String, port: Int) extends Command
case object Disconnect extends Command
case class Get(index: String, id: String)

class CommandParser(val input: ParserInput) extends Parser {

  def CommandLine = rule { WhiteSpace ~ Command ~ EOI }

  def Command = rule { DisconnectCommand | ConnectCommand | GetCommand }

  def ConnectCommand: Rule1[Connect] = rule {
    "connect" ~ (HostDefinition | (push("localhost") ~ push(9300))) ~> Connect
  }

  def DisconnectCommand: Rule1[Disconnect.type] = rule { "disconnect" ~ push(Disconnect) }

  def GetCommand: Rule1[Get] = rule {
    "get" ~ Id ~ "from" ~ IndexDefinition ~> ((id: String, index: String) => Get(index, id))
  }

  def HostDefinition: Rule2[String, Int] = rule { (Host ~ Port) | (Host ~ push(9300)) }

  def Host: Rule1[String] = rule { capture(oneOrMore(CharPredicate.AlphaNum)) ~ WhiteSpace }

  def Port: Rule1[Int] = rule {
    capture(oneOrMore(CharPredicate.Digit)) ~> ((port: String) => port.toInt) ~ WhiteSpace
  }

  def Id: Rule1[String] = rule { capture(oneOrMore(CharPredicate.Visible)) ~ WhiteSpace }

  def IndexDefinition: Rule1[String] = rule { capture(oneOrMore(CharPredicate.Visible)) ~ WhiteSpace }

  def WhiteSpace = rule { zeroOrMore(' ') }

  implicit def wspStr(s: String): Rule0 = rule {
    str(s) ~ WhiteSpace
  }

}
