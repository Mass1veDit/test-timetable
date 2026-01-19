#complilaye project
javac -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

#start project
java -cp "out;lib/postgresql-42.6.0.jar" App