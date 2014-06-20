package net.fehmicansaglam.elasticsearch.repl

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.index.IndexResponse

object DefaultConsole extends AbstractConsole with Interpreter {

  var client: Option[ElasticClient] = None

  var remote: String = "disconnected"

  val connectPattern1 = "connect (\\S+)$".r
  val connectPattern2 = "connect (\\S+) ([0-9]+)$".r
  val indexPattern1 = "index into (\\S+) (\\S+),(\\S+)$".r

  def prompt: String = Ansi.format("[", Ansi.Color.BLUE) + Ansi.format("es", Ansi.Color.GREEN) + Ansi.format(":", Ansi.Color.BLUE) + Ansi.format(remote, Ansi.Color.CYAN) + Ansi.format("] $ ", Ansi.Color.BLUE)

  def interpreter(): Interpreter = this

  def connect(host: String, port: Int): String = {
    try {
      client = Some(ElasticClient.remote(host, port))
      val response = client.get.admin.cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(5000)
      remote = s"${host}:${port}:${response.getClusterName}"
      updatePrompt()
      Ansi.format(s"Connected to $remote", Ansi.Color.GREEN)
    } catch {
      case t: Throwable => {
        client = None
        Ansi.format(t.getMessage, Ansi.Color.RED)
      }
    }
  }

  def disconnect(): String = {
    client = None
    val result = Ansi.format(s"Disconnected from ${remote}", Ansi.Color.GREEN)
    remote = "disconnected"
    updatePrompt()
    result
  }

  def interpret(input: String): String = {
    input.trim match {
      case connectPattern1(host) => {
        connect(host, 9300)
      }
      case connectPattern2(host, port) => {
        connect(host, port.toInt)
      }
      case "disconnect" => {
        disconnect()
      }
      case indexPattern1(indexName, key, value) => {
        client match {
          case Some(client) => {
            val response = client.sync.execute {
              index into indexName fields key -> value
            }
            Ansi.format(response.asInstanceOf[IndexResponse].getId, Ansi.Color.GREEN)
          }
          case None => Ansi.format("Not connected", Ansi.Color.RED)
        }
      }
      case _ => {
        Ansi.format("Unknown command", Ansi.Color.RED)
      }
    }
  }

}

object Console extends App {

  DefaultConsole.start()

}

