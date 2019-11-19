# mapache-search

A project to:
* Wrap a custom Google search for programming-related queries
* Add features like upvoting good results

### Before running:
* Copy `localhost.json.SAMPLE` into a new file `localhost.json`
* Fill in the stubs in `localhost.json`

| Type this | to get this result |
|-----------|------------|
| `mvn package` | to make a jar file|
| `mvn spring-boot:run` | to run the web app|
| in browser: `http://localhost:8080/` | to see search page |


### If you are getting the error `IllegalStateException: google.search.api.key is not defined.`

Ensure that `google.search.api.key` is defined in `localhost.json`
