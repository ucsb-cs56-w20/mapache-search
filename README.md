# mapache-search

A project to:
* Wrap a custom Google search for programming-related queries
* Add features like upvoting good results

# Before you run anything:
* Create new file localhost.json, and copy data from localhost.json.SAMPLE
* DO NOT DELETE localhost.json.sample
* Change the stubs in localhost.json
* In terminal, type . env.sh

| Type this | to get this result |
|-----------|------------|
| `mvn package` | to make a jar file|
| `mvn spring-boot:run` | to run the web app|
| in browser: `http://localhost:8080/` | to see search page |


NOTE:
What's really weird is I have both "All Users" and "Settings" in my drop-down when selecting the user drop-down menu. For some reason, if I include both, even if I click "Settings" with explicit th:action=/user/settings, it will keep directing me to /admin/users
I can't resolve this so I'll be taking it out for now

