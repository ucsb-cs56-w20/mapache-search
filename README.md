# mapache-search

A project to:
* Wrap a custom Google search for programming-related queries
* Add features like upvoting good results

### Before running:
* Copy `localhost.json.SAMPLE` into a new file `localhost.json`
* Fill in the stubs in `localhost.json`
* At the terminal (Linux/Mac), type `source env.sh`, or on Command Prompt (Windows) type `env.bat`

| Type this | to get this result |
|-----------|------------|
| `mvn package` | to make a jar file|
| `mvn -P localhost spring-boot:run` | to run the web app|
| in browser: `http://localhost:8080/` | to see search page |


NOTE:
What's really weird is I have both "All Users" and "Settings" in my drop-down when selecting the user drop-down menu. For some reason, if I include both, even if I click "Settings" with explicit th:action=/user/settings, it will keep directing me to /admin/users
I can't resolve this so I'll be taking it out for now


### Setting up keys & localhost.json:

* Setting up the Google Search API Key:

Nagivate to `https://developers.google.com/custom-search/v1/overview` and click the blue button `Get a Key`. Follow the steps to produce your key and insert it into `localhost.json`.

* Set up your Github application and insert the client ID and secret into `localhost.json`.

### Getting `stackapps_key`

In order to get Stack Exchange link previews, you will need a StackApps key.
We use this key to raise the API usage limit from 300 requests/day to 10000 requests/day.

In order to get a StackApps key, you must have a Stack Exchange account.
Fill out [this form](https://stackapps.com/apps/oauth/register) and copy the `key`.

### If you are getting the error `IllegalStateException: google.search.api.key is not defined.`

* Ensure that `google.search.api.key` is defined in `localhost.json`
* Run `source env.sh` (Linux/Mac) or `env.bat` (Windows) in the terminal/command prompt instance you are running the web app in

* NOTE: After deploying to heroku, you must go to heroku postgresql on its website and reset it. Then re-deploy and it should work.
