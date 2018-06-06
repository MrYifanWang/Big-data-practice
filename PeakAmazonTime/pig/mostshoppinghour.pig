rmf output

register target/pig-udf-0.0.1-SNAPSHOT.jar
register ../resource/geoip-api-1.3.1.jar

a1 = LOAD 'access_log_sample' AS (line:chararray);
a2 = filter a1 by line matches '.*&url=(https:|http:|https%3A|http%3A)//www.amazon.com.*';
a3 = FOREACH a2 GENERATE flatten(REGEX_EXTRACT_ALL(line, '(.*?) .*?\\[(.*?)\\].*?')) as (ip:chararray, dt:chararray);
a4 = FOREACH a3 GENERATE ip, flatten(REGEX_EXTRACT_ALL(dt, '.{11}:([0-9]{2}):.*')) as (hour:chararray);
a5 = FILTER a4 by ip is not null;
a6 = FOREACH a5 generate hour, com.example.pig.GetCountry(ip) as country;

a8 = FILTER a6 by country == 'US';

d = group a8 by hour;
e = foreach d generate group, COUNT(a8) as count;
f = order e by count desc;

store f into 'output';
