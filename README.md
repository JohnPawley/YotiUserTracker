==========
= README =
==========

This is my solution to the Yoti unattended coding test. It is a DropWizard Service accessible via a Rest-API.
It uses an in memory data store to store and retrieve Users. This is meant data is lost when the app is restarted,
is maintained between calls.

========================
= RUNNING INSTRUCTIONS =
========================

This solution was coded on a Mac, and I have not been able to confirm that it works correctly on other OSs. However
so long as Maven is being used, then the project should package and run correctly elsewhere. Maven is necessary for
running the application, on a mac it can be obtained through 'HomeBrew' by using the command:

    brew install maven

Once you have a working copy of maven, then navigate to the root folder of the project. From here you can run the
following commands:

    - 'mvn compile' => Compile the code to check for any errors.
    - 'mvn verify' => Runs all of the test suite.
    - 'mvn package' => Creates a fat jar for running the DropWizard app. Inside the target folder.

Once you have ran 'mvn package' it is possible to deploy the DropWizard application locally by using the following
command, ran from the root directory.

    'java -jar target/YotiUserTracker-1.0-SNAPSHOT.jar server conf.yml'

Assuming that the default config file provided hasn't been changed, this will start the application running on port
8080.

=======
= API =
=======

The following endpoints are available on the application:

    - POST => /UserTracker/users/{userId}
      This creates a user on the system, with the userId specified. Examples with responses below:
      	curl -X POST localhost:8080/UserTracker/users/ef98579c-0c7f-4bde-886a-62d60e7320ae => 201
      	curl -X POST localhost:8080/UserTracker/users/INVALID => 400
      	A 200 response can also be received if the user has already been created.

    - POST => /UserTracker/admins/{adminId}
      This creates an admin on the system, with the adminId specified. Examples with responses below:
      	curl -X POST localhost:8080/UserTracker/admins/98dbee24-54f3-4fc0-a617-a65515ddd19b => 201
      	curl -X POST localhost:8080/UserTracker/admins/INVALID => 400
      	A 200 response can also be received if the admin has already been created.

    - PUT => /UserTracker/users/{userId}/connections/{connectionId}
      This creates a connection between two users. Both must be registered users. Examples with responses below:
      	curl -X PUT localhost:8080/UserTracker/users/ef98579c-0c7f-4bde-886a-62d60e7320ae/connections/57cfa481-bac5-4b8e-a615-f4b6e42b82f7 => 200
      	curl -X PUT localhost:8080/UserTracker/users/INVALID/connections/INVALID => 400
      	A 401 response can be received if the UUIDs are valid but not registered.

    - GET => /UserTracker/users => with header userId = {userId}
      	curl -X GET -H "User-Id":ef98579c-0c7f-4bde-886a-62d60e7320ae localhost:8080/UserTracker/users => 200
      	curl -X GET -H "User-Id":INVALID localhost:8080/UserTracker/users => 400
      	A 401 response can be received if the user id provided is valid but not registered.

    - GET => /UserTracker/users/{userId}/connections
      	=> with header adminId = {adminId}
      	curl -X GET -H "Admin-Id":98dbee24-54f3-4fc0-a617-a65515ddd19b localhost:8080/UserTracker/users/ef98579c-0c7f-4bde-886a-62d60e7320ae/connections => 200
      	curl -X GET -H "Admin-Id":98dbee24-54f3-4fc0-a617-a65515ddd19b localhost:8080/UserTracker/users/INVALID/connections => 400
      	curl -X GET -H "Admin-Id":INVALID localhost:8080/UserTracker/users/ef98579c-0c7f-4bde-886a-62d60e7320ae/connections => 400
      	A 401 response can also be received if either the admin or user id are valid but not registered.

==============
= EXTENSIONS =
==============

Due to time restrictions, I was not able to create an integration test suite to support my unit tests. These would have
been responsible for testing the applications fully, using a HttpClient to access a running application.

In order to maintain the data store over restart, i would have replaced the current UserRepositoryImpl with an
implementation that used a database. I probably would have used MongoDB because of its quick startup times.