Spark Version:

This spark program running with hadoop in docker.

# Put require files to hdfs
hadoop fs -put /src/PeakAmazonTime/data/access_log_sample 
hadoop fs -put /src/PeakAmazonTime/data/appmetadata.txt.gz

# remove the old output files
hadoop fs -rm -r output

# run spark program
spark-submit --class com.example.spark.MostShoppingHour --jars ../resource/geoip-api-1.3.1.jar --files ../resource/GeoLiteCity.dat target/mostshoppinghour-1.0-SNAPSHOT.jar

# check output 
hadoop fs -ls output 

# merge files 
hadoop fs -getmerge output output.txt

output.txt:

hour in 24h, the number of people access amazon.com at this hour. 
15,11938
16,8590
14,8088
17,2791
02,2400
01,2397
00,2375
23,2290
03,2122
21,2115
19,2083
18,2071
20,2064
22,2034
04,1508
13,1092
05,1003
12,734
06,625
11,452
07,422
08,267
09,252
10,247

Pig Version:

This Pig program running with hadoop in docker.

# remove the old output files
hadoop fs -rm -r output

# run pig program
pig mostshoppinghour.pig

# check output 
hadoop fs -ls output 

# merge files 
hadoop fs -getmerge output output.txt

output.txt

15	11938
16	8590
14	8088
17	2791
02	2400
01	2397
00	2375
23	2290
03	2122
21	2115
19	2083
18	2071
20	2064
22	2034
04	1508
13	1092
05	1003
12	734
06	625
11	452
07	422
08	267
09	252
10	247