package net.fehmicansaglam.elasticsearch.repl

trait InterpretationResult {
  def result: String
  def color: Ansi.Color
  def print: String = Ansi.format(result, color)
}

case class Success(result: String, color: Ansi.Color = Ansi.Color.GREEN) extends InterpretationResult
case class Warning(result: String, color: Ansi.Color = Ansi.Color.YELLOW) extends InterpretationResult
case class Error(result: String, color: Ansi.Color = Ansi.Color.RED) extends InterpretationResult

