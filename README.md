### HTTP4S Fun

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

### Route to service 
```bash
curl -verbose  "http://localhost:8080/index" -H "Accept:application/json"
```

```bash
curl -verbose "http://localhost:8080/users" -H "Accept:application/json" 
```

```bash
curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Andrea" }' 
```

```bash
curl -verbose "http://localhost:8080/user/1" -H "Accept:application/json"
```

#### Code coverage

```bash
$ sbt clean coverage test coverageReport coverageAggregate
```
