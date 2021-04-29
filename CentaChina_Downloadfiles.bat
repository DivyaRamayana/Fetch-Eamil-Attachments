@echo off
cd /e "E:\Eclipse\FetchAttachment"
java -classpath E:\Eclipse\FetchAttachment\bin;E:\Eclipse\FetchAttachment\lib\javax.mail-1.5.5.jar;E:\Eclipse\FetchAttachment\lib\log4j-1.2.17.jar com.rexnord.sfdc.mail.downloads.GetEmailAttachment Test.properties