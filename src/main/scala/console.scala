package net.fehmicansaglam.elasticsearch.repl

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.index.IndexResponse

object DefaultConsole extends AbstractConsole with Interpreter {

  var __client: Option[ElasticClient] = None

  var remote: String = "disconnected"

  val connectPattern1 = "connect (\\S+)$".r
  val connectPattern2 = "connect (\\S+) ([0-9]+)$".r
  val indexPattern1 = """index into (\S+) ("\S+?"->"\S+?")(,"\S+?"->"\S+?")*$""".r
  val getPattern = "get (\\S+) from (\\S+)$".r

  def prompt: String = Ansi.format("[", Ansi.Color.BLUE) + Ansi.format("es", Ansi.Color.GREEN) + Ansi.format(":", Ansi.Color.BLUE) + Ansi.format(remote, Ansi.Color.CYAN) + Ansi.format("] $ ", Ansi.Color.BLUE)

  def interpreter(): Interpreter = this

  def connected(body: ElasticClient => InterpretationResult): InterpretationResult = {
    if (!__client.isDefined) {
      Error("Not connected")
    } else try {
      body(__client.get)
    } catch {
      case t: Throwable => Error(t.getMessage)
    }
  }

  def doConnect(host: String, port: Int): InterpretationResult = {
    try {
      if (__client.isDefined) __client.get.close
      __client = Some(ElasticClient.remote(host, port))
      val response = __client.get.admin.cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(5000)
      remote = s"${host}:${port}:${response.getClusterName}"
      updatePrompt()
      Success(s"Connected to $remote")
    } catch {
      case t: Throwable => {
        __client = None
        remote = "disconnected"
        updatePrompt()
        Error(t.getMessage)
      }
    }
  }

  def doDisconnect(): InterpretationResult = connected { client =>
    client.close
    __client = None
    val result = Ansi.format(s"Disconnected from ${remote}", Ansi.Color.GREEN)
    remote = "disconnected"
    updatePrompt()
    Success(result)
  }

  def doIndex(_index: String, _fields: (String, String)*): InterpretationResult = connected { client =>
    val response = client.sync.execute {
      index into _index fields (_fields: _*)
    }
    Success(response.asInstanceOf[IndexResponse].getId)
  }

  def doGet(_index: String, _id: String): InterpretationResult = connected { client =>
    val response = client.sync.execute {
      get id _id from _index
    }
    if (!response.isExists) {
      Warning("Not found")
    } else {
      Success(response.getSource.toString)
    }
  }

  def interpret(input: String): String = {
    new CommandParser(input).CommandLine.run().map {
      case Disconnect => doDisconnect()
      case Connect(host, port) => doConnect(host, port)
      case Get(index, id) => doGet(index, id)
      case _ => Warning("Not supported yet:(")
    }.recover {
      case pe: org.parboiled2.ParseError => Warning(pe.formatTraces)
      case t: Throwable => Error(t.getMessage)
    }.get.print
  }

}

object Console extends App {

  DefaultConsole.start()

}

