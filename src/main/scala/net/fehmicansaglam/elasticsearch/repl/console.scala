package net.fehmicansaglam.elasticsearch.repl

import com.google.gson.{GsonBuilder, JsonElement}
import io.searchbox.client.config.HttpClientConfig.Builder
import io.searchbox.client.{JestClient => Client, JestClientFactory}
import io.searchbox.cluster.Health
import io.searchbox.core.Count.{Builder => CountBuilder}
import io.searchbox.core.Delete.{Builder => DeleteBuilder}
import io.searchbox.core.Get.{Builder => GetBuilder}
import io.searchbox.core.Search.{Builder => SearchBuilder}
import io.searchbox.indices.DeleteIndex.{Builder => DeleteIndexBuilder}

import scala.concurrent.duration._

object DefaultConsole extends AbstractConsole with Interpreter {

  implicit val timeout = 5.seconds

  private var __client: Option[Client] = None

  var remote: String = "disconnected"

  //  val mapper = new ObjectMapper

  def prompt: String = Ansi.format("[", Ansi.Color.BLUE) + Ansi.format("es", Ansi.Color.GREEN) + Ansi.format(":", Ansi.Color.BLUE) + Ansi.format(remote, Ansi.Color.CYAN) + Ansi.format("] $ ", Ansi.Color.BLUE)

  def interpreter(): Interpreter = this

  def shutdown(): Unit = __client.foreach(_.shutdownClient())

  def connected(body: Client => InterpretationResult): InterpretationResult = {
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
      shutdown() //shutdown if already connected

      val factory = new JestClientFactory()
      factory.setHttpClientConfig(new Builder(s"http://$host:$port").multiThreaded(false).build())
      __client = Some(factory.getObject)
      val clusterName = __client.get.execute(new Health.Builder().build()).getJsonObject.get("cluster_name").getAsString
      remote = s"$host:$port:$clusterName"
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
    client.shutdownClient()
    __client = None
    val result = Ansi.format(s"Disconnected from ${remote}", Ansi.Color.GREEN)
    remote = "disconnected"
    updatePrompt()
    Success(result)
  }

  //  def doIndex(_index: String, _json: String): InterpretationResult = connected { client =>
  //    val response = client.sync.execute {
  //      index into _index doc (new {
  //        val json = _json
  //      } with DocumentSource)
  //    }
  //    Success(response.asInstanceOf[IndexResponse].getId)
  //  }

  def doGet(_index: String, _id: String): InterpretationResult = connected { client =>
    val builder = new GetBuilder(_index, _id)
    val response = client.execute(builder.build()).getJsonObject
    if (!response.get("found").getAsBoolean) {
      Warning("Not found")
    } else {
      Success(prettyPrint(response))
    }
  }

  def doDelete(_index: String, _type: String, _id: String): InterpretationResult = connected { client =>
    val builder = new DeleteBuilder(_id).index(_index).`type`(_type)
    val response = client.execute(builder.build()).getJsonObject

    if (!response.get("found").getAsBoolean) {
      Warning("Not found")
    } else {
      Success("Deleted")
    }
  }

  def doDrop(_index: String): InterpretationResult = connected { client =>
    val builder = new DeleteIndexBuilder(_index)
    val response = client.execute(builder.build())

    if (response.isSucceeded) Success("Dropped")
    else Warning(response.getErrorMessage)
  }

  def doSearch(_index: String, _query: Option[String]): InterpretationResult = connected { client =>
    val builder = new SearchBuilder(
      _query.map(q => s"""{ "query": { "query_string": { "query": "$q" } } }""").getOrElse(""))
      .addIndex(_index)
    val response = client.execute(builder.build())

    if (response.isSucceeded) Success(prettyPrint(response.getJsonObject))
    else Warning(response.getErrorMessage)
  }

  def doCount(_index: String, _query: Option[String]): InterpretationResult = connected { client =>
    val builder = new CountBuilder().addIndex(_index)
    _query.foreach(q => builder.query( s"""{ "query": { "query_string": { "query": "$q" } } }"""))

    val response = client.execute(builder.build())

    if (response.isSucceeded) Success(response.getCount.toLong.toString)
    else Warning(response.getErrorMessage)
  }

  //  def doGetMappings(_index: String): InterpretationResult = connected { client =>
  //    val builder = new Mapping
  //
  //    val types = response.getField(_index).iterator
  //    Success(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(types))
  //  }

  def doClusterHealth(): InterpretationResult = connected { client =>
    val response = client.execute(new Health.Builder().build()).getJsonObject
    Success(prettyPrint(response))
  }

  //  def doReindex(_sourceIndex: String, _targetIndex: String): InterpretationResult = connected { client =>
  //    val response = client.sync.reindex(_sourceIndex, _targetIndex)
  //    Success(s"Reindexed ${_sourceIndex} to ${_targetIndex}")
  //  }

  def interpret(input: String): String = {
    val parser = new CommandParser(input)

    parser.CommandLine.run().map {
      case Disconnect => doDisconnect()
      case Connect(host, port) => doConnect(host, port)
      case Get(index, id) => doGet(index, id)
      //      case Index(index, json) => doIndex(index, json)
      case Delete(index, typ, id) => doDelete(index, typ, id)
      case Drop(index) => doDrop(index)
      case Search(index, query) => doSearch(index, query)
      case Count(index, query) => doCount(index, query)
      //      case GetMappings(index) => doGetMappings(index)
      case ClusterHealth => doClusterHealth()
      //      case Reindex(sourceIndex, targetIndex) => doReindex(sourceIndex, targetIndex)
      case _ => Warning("Not supported yet:(")
    }.recover {
      case pe: org.parboiled2.ParseError => Warning(parser.formatError(pe))
      case t: Throwable =>
        t.printStackTrace()
        Error(t.getMessage)
    }.get.print
  }

  private def prettyPrint(json: JsonElement): String = {
    val gson = new GsonBuilder().setPrettyPrinting().create()
    gson.toJson(json)
  }

}

object Console extends App {

  DefaultConsole.start()

}

