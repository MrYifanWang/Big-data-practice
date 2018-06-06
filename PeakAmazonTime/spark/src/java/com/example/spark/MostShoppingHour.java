package com.example.spark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;


public class MostShoppingHour {
    @SuppressWarnings("serial")
	static class Transformer implements Function<String, Row> {
    	Pattern linePattern1 = Pattern.compile("(.*?) .*?\\[(.*?)\\].*");
        Pattern linePattern2 = Pattern.compile(".{11}:([0-9]{2}):.*");
        static LookupService cl;
        static Object lock = new Object();
        @Override
        public Row call(String line) throws Exception {
            Matcher m1 = linePattern1.matcher(line);
           
            String ip = null;
            String hour = null;
            String dt = null;
            if (m1.find()) {
                ip = m1.group(1);
                dt = m1.group(2);
                Matcher m2 = linePattern2.matcher(dt);
                if (m2.find()) {
                    hour = m2.group(1);
                }
                
            }
            synchronized(lock) {
                if (cl == null) {
                    cl = new LookupService(SparkFiles.get("GeoLiteCity.dat"),
                            LookupService.GEOIP_MEMORY_CACHE );
                }
            }
            Location loc = cl.getLocation(ip);
            return RowFactory.create(loc!=null?loc.countryCode:null, hour);
        }
    }
    
	public static void main(String[] args) throws IOException {
		SparkSession spark = SparkSession
                .builder()
                .appName(MostShoppingHour.class.getName())
                .getOrCreate();

		JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext());
		JavaRDD<Row> accessLogRDD = context.textFile("access_log_sample")
		         .filter(line -> line.matches(".*&url=(https:|http:|https%3A|http%3A)//www.amazon.com.*"))
		         .map(new Transformer());

		List<StructField> accessLogFields = new ArrayList<>();
		accessLogFields.add(DataTypes.createStructField("country", DataTypes.StringType, true));
		accessLogFields.add(DataTypes.createStructField("hour", DataTypes.StringType, true));
        StructType accessLogType = DataTypes.createStructType(accessLogFields);
        
        Dataset<Row> accessLogDf = spark.createDataFrame(accessLogRDD, accessLogType)
                .where("country = 'US'");

        accessLogDf.groupBy("hour")
                .agg(functions.count("*").as("h"))
                .sort(functions.desc("h"))
                .write()
                .csv("output");
	}
}
