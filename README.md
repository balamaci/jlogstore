JLogStore
=====================
JLogStore is sort of Logstash written in Java but for a very specific usecase - that of JSON logs-. 

It can be viewed as a simple combination of Logstash and Kafka in the way that the log entries can be buffered locally in a very fast
data structure similar to the Kafka log which means it's very fast while still not loosing log events.  

JLogStore is composed of a Server part which listens for JSON log data being sent by the client. 

We built on top of to send the data directly from the Logback appender(for smaller latency when the log event is produced till it's processed), . 
As long as you've got a JSON log source that you can ship to the server you're not locked in to using the logback appender.
We plan in the future to also be able to tail and send log files. 


The reasons for the existence of **JLogStore** are:
 - Simpler which also means easy to customize for a Java dev. Because we just assume logs are already coming in a JSON format and are being written. 
Logstash is the swiss army knife of Log manipulation(having many plugins for input and output), JLogStore is just the corkscrew - but that's great if you just drink wine- 
Writing Logstash GROK patterns is not everyones cup of tea. For Java devs it should trivial in JLogStore to mutate a JSON into another before sending it to ElasticSearch or to hack away and tweak the whole logs processing flow directly in a Java framework
than digging through a combination of Ruby and Java code(Logstash).   

 - Durable storage that allows to ingest large stream of data. For Logstash in order to deal with a large stream of log data, it's sometimes advised to use Kafka in front of Logstash to store and buffer the events and let Logstash forwards to ES at it's own pace. 
If you stack already relies on Kafka, great. But Kafka itself is quite big to setup and justify for logging. 
JLogStore is using and embedded in the form of [ChronicleQueue](https://github.com/OpenHFT/Chronicle-Queue). 
While were missing the advantage of a distributed log system like Kafka for distributed backups of the log events, we assume most of projects don't need it.   

 - Fast and scalable. Because we're using RSocket for streaming the log events from the client and the server is using Netty as an implementation so we can serve many clients with very few resources. 
It's also fast because we're offloading the log events immediately on receive from the client to a blazing fast storage [ChronicleQueue](https://github.com/OpenHFT/Chronicle-Queue) which is also used for financial systems. 
So instead of the client waiting for the server to send the data to ElasticSearch it can proceed to sending the next event, so we're acting like a buffer between the client and ElasticSearch, freeing up the client to send data at it's own pace. We have persisted the events so none are lost. 
The same setup can be achieved by frontending Logstash with Kafka, but at the cost of a much more complex setup and servers.  

 
```bash
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch-oss:6.5.4
```
