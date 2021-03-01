### requirements

- `JDK 8+` installed
- `sbt` installed
- existence of the shell at `/bin/sh`
- blackbox binary (provided, mac os x version) should be located at `~/Downloads/blackbox`

### build & run

- `sbt run`
- or just click green triangle in the IntelliJ Idea

### behavior exposed

- gathered statistics exposed to 
  http://localhost:8080/state,
  can be viewed in pretty format in the Chrome browser
  with JSON Formatter extension installed  

### disclaimer

- no configuration file
- no tests
- no documentation

### notes

there are two implementations:
- Plain Scala command line app (without http server) with console printout
- Akka Streams + Akka Http Server
