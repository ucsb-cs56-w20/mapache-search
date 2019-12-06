# mapache-search

A project to:
* Wrap a custom Google search for programming-related queries
* Add features like upvoting good results

### Before running:
* Copy `localhost.json.SAMPLE` into a new file `localhost.json`
* Fill in the stubs in `localhost.json`
* At the terminal, type `source env.sh`

| Type this | to get this result |
|-----------|------------|
| `mvn package` | to make a jar file|
| `mvn spring-boot:run` | to run the web app|
| in browser: `http://localhost:8080/` | to see search page |


NOTE:
What's really weird is I have both "All Users" and "Settings" in my drop-down when selecting the user drop-down menu. For some reason, if I include both, even if I click "Settings" with explicit th:action=/user/settings, it will keep directing me to /admin/users
I can't resolve this so I'll be taking it out for now


### Getting `stackapps_key`

In order to get Stack Exchange link previews, you will need a StackApps key.
We use this key to raise the API usage limit from 300 requests/day to 10000 requests/day.

In order to get a StackApps key, you must have a Stack Exchange account.
Fill out [this form](https://stackapps.com/apps/oauth/register) and copy the `key`.

### If you are getting the error `IllegalStateException: google.search.api.key is not defined.`

* Ensure that `google.search.api.key` is defined in `localhost.json`
* Run `source env.sh` in the terminal instance you are running the web app in
