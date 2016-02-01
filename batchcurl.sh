#!/bin/bash
echo "POST greet " $1 " times" 

for i in $(eval echo {1..$1})
do  
  nohup curl -X GET -H "request-id: $i" "http://localhost:8080/greet/kamkor" >> /dev/null & disown
done
