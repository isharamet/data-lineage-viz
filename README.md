# data-lineage-viz

This is an attempt at defining arbitrary data lineage graph via small composable specification files which can describe data (`:data`), transformations (`:data-transformations`) and data types (`:data-types`) in any combination and then visualize it in some meaningful way. Then for each piece of data you can trace all incoming and outcoming nodes, which can help you understand the lineage better. For now graph is renderd using [GraphViz](https://www.graphviz.org/), but maybe I'll come with proper visualization (D3, maybe) in future.

## Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser. The server will be available at [http://localhost:3449](http://localhost:3449) once Figwheel starts up. 

Figwheel also starts `nREPL` using the value of the `:nrepl-port` in the `:figwheel`
config found in `project.clj`. By default the port is set to `7002`.

The figwheel server can have unexpected behaviors in some situations such as when using
websockets. In this case it's recommended to run a standalone instance of a web server as follows:

```
lein do clean, run
```

The application will now be available at [http://localhost:3000](http://localhost:3000).


### Optional development tools

Start the browser REPL:

```
$ lein repl
```
The Jetty server can be started by running:

```clojure
(start-server)
```
and stopped by running:
```clojure
(stop-server)
```


## Building for release

```
lein do clean, uberjar
```

