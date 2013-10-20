MateyFindArr
============

A location sharing app for Android. Share your location with selected contacts, at certain times and locations.

This software uses version 1 of the Google maps API. This requires a Google Maps key otherwise the map will
not be rendered properly on the screen (it will just show as a grey background). If you have a map key, enter 
it in the appropriate place in the code (LocationActivity.java). Version 1 of the API is now deprecated so you 
will not be able to obtain a new key if you don't already have one. In this case you will need to modify the source code to use version 2 of the Google maps API. To set up your Android project, make sure that you are using the Google API build target (e.g. in Eclipse right click the project package -> Properties -> Android,  select Google APIs).

The app requires a SQL server with PHP scripts to store and serve the location data and carry out the Google Cloud
Messaging (GCM) communications. The PHP scripts and database schemas can be found in the Server Docs folder. In the Android code, references to the locations of our php scripts have been removed and replaced with your-server-goes-here.com. Replace this with the address of your server.

You will need some GCM codes too - a sender ID for your app which should be inserted in RegisterThread.java. This can be obtained from the Google Developer console. http://developer.android.com/google/gcm/gs.html
The individual GCM codes for each user will be saved in shared preferences on the devices, 
and uploaded to the SQL database on your server so you don't need to know what these are.





