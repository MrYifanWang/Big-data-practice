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
1,1202
0,1186
21,1139
23,1136
2,1130
20,1111
19,1097
18,1096
22,1083
17,1031
3,1002
16,962
15,846
14,804
4,745
13,556
5,500
12,411
6,337
11,250
7,217
10,134
8,124
9,119