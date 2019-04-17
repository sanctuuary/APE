#!/bin/sh

mvn clean install
rm /home/vedran/ownCloud/PhD/All Use Cases/Test_UseCases/Demo_UseCase/APE-0.1.jar
cp /home/vedran/git/APE/target/APE-0.1-jar-with-dependencies.jar home/vedran/ownCloud/PhD/All Use Cases/Test_UseCases/Demo_UseCase/APE-0.1.jar
