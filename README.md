### HTTP4S Fin

#### Modules

The project is multi module project following the guidance of S.O.C principles.
The project includes the following modules

* Core services
* Users entity


#### Running the project


##### Run code

run the sbt console :

```bash
$ sbt -mem 2048
```

select the users project 

```sbtshell
sbt:services> project users
```

run 

```sbtshell
sbt:users> run
```

connect using 

```bash
$ curl -verbose  "http://localhost:8080/index" -H "Accept:application/json" -H "Origin:http://api.alpha.com"
```

#### Code coverage

```bash
$ sbt clean coverage test coverageReport coverageAggregate
```
