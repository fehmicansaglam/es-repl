elasticsearch-repl
==================

Elasticsearch REPL built on top of Jest

[![Codacy Badge](https://www.codacy.com/project/badge/44c69ed6a4cf424181fae338785d0cf9)](https://www.codacy.com/app/fehmicansaglam/es-repl)
![Ready](http://progressed.io/bar/85?title=ready)
[![Licence](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Installation
Download the latest release from [here](https://github.com/fehmicansaglam/es-repl/releases/latest). `es-repl` script is a shell script with a couple of jars embedded. Open a terminal and run `es-repl` by using the command below:

```
$ ./es-repl
```

## Available REPL commands

#### connect

Connects to a Elasticsearch cluster. Default host is `localhost` and default port is `9200`. You can also specify host and port information.

```
connect myhost
```

```
connect myanotherhost 9200
```

#### disconnect

Disconnects from the cluster.

#### health

Prints the health of the cluster.

#### count

Counts the documents in an index.

```
count users
```

```
counts users query
```

#### get

Gets a document with the given id.

```
get AU01vx8NLBXsJPBUOC5C from users
```

#### delete

Deletes a document with the given type and id.

```
delete AU01vx8NLBXsJPBUOC5C from users/user
```

#### drop

Drops an index.

```
drop users
```

#### search

Searchs for documents in an index.

```
search in users query
```

#### quit

Quits from the REPL. `q` and `exit` are aliases for `quit`.

#### clear

Clears the REPL screen. `cl` is an alias for `clear`.

## Screenshots

![Screenshot 1](guide/images/1.png "Screenshot 1")

![Screenshot 2](guide/images/2.png "Screenshot 2")

![Screenshot 3](guide/images/3.png "Screenshot 3")
