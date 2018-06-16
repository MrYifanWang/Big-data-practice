Download Nutch from Github, change to version 1.12.
git clone https://github.com/apache/nutch
git checkout release-1.12

Apply path files
patch -p1 < ../fixskew.patch
patch -p1 < ../googleplaycrawler.patch

ant job
ant eclipse

Run googleplaycrawler locally
1. echo "https://play.google.com/store/apps/details?id=com.facebook.katana" > seed
2. hadoop jar build/apache-nutch-1.12.job org.apache.nutch.googleplay.GooglePlayCrawler -Dmapreduce.framework.name=local -Dfs.defaultFS=file:/// seed -numFetchers 10 -depth 1

Put nutch file to hadoop class path.
export HADOOP_CLASSPATH=/src/googleplaycrawler/nutch/build/apache-nutch-1.12.job

See the result.
hadoop fs -text file:///src/googleplaycrawler/nutch/nutchdb/segments/20180615140847/parse_data/part-00004/data

Run googleplaycrawler on Single Node Cluster
1. hadoop fs -put seed .
2. hadoop jar build/apache-nutch-1.12.job org.apache.nutch.googleplay.GooglePlayCrawler seed -numFetchers 10