package com.revature.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.fileupload.FileItem;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.revature.web.MainController;

public class S3PutObject {
	String ACCESS_KEY_ID = null;
	String ACCESS_SEC_KEY = null;
	String BUCKET_NAME = null;
	
	public String putObject(FileItem image) throws IOException {
		String pattern = "MM/dd/yyyy HH:mm:ss";
		// Create an instance of SimpleDateFormat used for formatting 
		// the string representation of date according to the chosen pattern
		DateFormat df = new SimpleDateFormat(pattern);

		// Get the today date using Calendar object.
		Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		String todayAsString = df.format(today);

		InputStream file = MainController.class.getResourceAsStream("/credentials.properties");
		Properties props = new Properties(); // Object representation of the properties from a properties file
		props.load(file);
		ACCESS_KEY_ID = props.getProperty("ACCESS_KEY_ID");
		ACCESS_SEC_KEY = props.getProperty("ACCESS_SEC_KEY");
		BUCKET_NAME = props.getProperty("BUCKET_NAME");
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID,ACCESS_SEC_KEY);
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_WEST_1)
				  .build();		
		S3Object s3Object = new S3Object();
	    ObjectMetadata omd = new ObjectMetadata();
	    omd.setContentType(image.getContentType());
	    omd.setContentLength(image.getSize());
	    omd.setHeader("filename", image.getName());
	    ByteArrayInputStream bis = new ByteArrayInputStream(image.get());
	    s3Object.setObjectContent(bis);
	    s3client.putObject(new PutObjectRequest(BUCKET_NAME, todayAsString+image.getName(), bis, omd));
	    s3Object.close();
		String images3 = ((AmazonS3Client) s3client).getResourceUrl(BUCKET_NAME, todayAsString+image.getName());
		
		return images3;
	};

}
