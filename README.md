MateyFindArr
============

A location sharing app for Android. Share your location with selected contacts, at certain times and locations.

This software uses version 1 of the Google maps API. This requires a Google Maps key otherwise the map will
not be rendered properly on the screen (it will just show as a grey background). If you have a map key, enter 
it in the appropriate place in the code. Version 1 of the API is now deprecated so you will not be able to 
obtain a new key if you don't already have one. In this case you will need to modify the source code to use 
version 2 of the Google maps API.

The app also requires a SQL server with PHP scripts to store and serve the location data. The PHP scripts 
and database schemas can be found in the Server Docs folder.
