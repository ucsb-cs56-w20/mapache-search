# mapache-search

A project to:
* Wrap a custom Google search for programming-related queries
* Add features like upvoting good results

### Get a Google Search API Key
- Navigate to [here](https://developers.google.com/custom-search/v1/overview).
- Scroll down to the "API key" header and click the blue button that says, "Get a Key".
- Create a new "project"; you can name it whatever you like, something like "mapache-search" is fine.
- Then, you should have generated an API key. Copy it. 

### Before running:
* Copy `localhost.json.SAMPLE` into a new file `localhost.json`
* Fill in the stubs in `localhost.json`
* At the terminal, type `source env.sh`

| Type this | to get this result |
|-----------|------------|
| `mvn package` | to make a jar file|
| `mvn spring-boot:run` | to run the web app|
| in browser: `http://localhost:8080/` | to see search page |


### If you are getting the error `IllegalStateException: google.search.api.key is not defined.`

* Ensure that `google.search.api.key` is defined in `localhost.json`
* Run `source env.sh` in the terminal instance you are running the web app in
