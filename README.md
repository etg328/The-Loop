# The-Loop
Stay in the loop with quick and easy access to world news at your fingertips.
Access brief bullet points on recent news from NYT, Wall Street Journal, AP, and ESPN




How to run: 

To update the database: 

cd my-app
mvn clean compile exec:java -Dexec.mainClass="com.example.bedrockdemo.App"

To run the backend:

On the AWS server VS Code
mvn clean compile exec:java

To run front end: 

cd web
cd tmp
npx http-server -c-1
