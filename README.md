
# Register Trust Tax Liability Frontend

This service is responsible for collecting details about any tax liability the trust has when registering a trust.

### Running the service

To run locally using the micro-service provided by the service manager:

```
sm2 --start TRUSTS_ALL
```

or

```
sm2 --start REGISTER_TRUST_ALL
```

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8838 but is defaulted to that in build.sbt):

`sbt run`


### Testing the service
To test the service locally use the following command, this will run the unit tests, scalastyle and check the coverage of the tests.

```
./run_all_tests.sh
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
