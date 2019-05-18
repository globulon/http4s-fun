#!/usr/bin/env bash

curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Andrea" }'
curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Marc" }'
curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Julien" }'
curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Filippo" }'


siege -u "http://localhost:8080/users"  -d1 -r25 -c100