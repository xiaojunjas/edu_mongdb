curl ${CFGURL} -o application.yml
java -jar edu-file.jar server application.yml
