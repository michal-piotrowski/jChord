# jChord
Small program for creating image of tab based on dot separated fret input

In order for it to become more useful
* Layout modification must be introduced

![alt text](https://github.com/michal-piotrowski/jChord/blob/master/przykladUzyciaJChord.JPG)

Compile using (invokes the jar goal of maven-jar-plugin plugin)
```bash
mvn clean package
```

run using
- the interactive mode 
```bash
 java -jar .\target\jChord.jar addNew
```
- or one-command mode
```bash
  java -jar .\target\jChord.jar addNew E7#5 0.7.9.9.9.8
``` 
