package nci.nih.gov.evs.ipconverter;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IpConverterTest {
	
	Date sept8;
	Date sept9;
	Date sept10;
	Date sept11;
	Date sept30;
	Date oct1;
	Date oct2;
	Date oct5;
	Date oct28;
	Date oct29;
	Date nov3;
	Date nov5;
	Date nov6;
	Date nov10;
	Date nov12;
	
	List<Date> dates;
	
	@BeforeEach
	public void setUp() throws ParseException {
		
		   SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		   
		dates = new ArrayList<Date>();

		sept8 = sdf.parse("Tue Sep 08 00:00:00 CDT 2020");
		dates.add(sept8);
		sept9 = sdf.parse("Wed Sep 09 00:00:00 CDT 2020");
		dates.add(sept9);
		sept10 = sdf.parse("Thu Sep 10 00:00:00 CDT 2020");
		dates.add(sept10);
		sept11 = sdf.parse("Fri Sep 11 00:00:00 CDT 2020");
		dates.add(sept11);
		sept30 = sdf.parse("Wed Sep 30 00:00:00 CDT 2020");
		dates.add(sept30);
		oct1 = sdf.parse("Thu Oct 01 00:00:00 CDT 2020");
		dates.add(oct1);
		oct2 = sdf.parse("Fri Oct 02 00:00:00 CDT 2020");
		dates.add(oct2);
		oct5 = sdf.parse("Mon Oct 05 00:00:00 CDT 2020");
		dates.add(oct5);
		oct28 = sdf.parse("Wed Oct 28 00:00:00 CDT 2020");
		dates.add(oct28);
		oct29 = sdf.parse("Thu Oct 29 00:00:00 CDT 2020");
		dates.add(oct29);
		nov3 = sdf.parse("Tue Nov 03 00:00:00 CST 2020");
		dates.add(nov3);
		nov5 = sdf.parse("Thu Nov 05 00:00:00 CST 2020");
		dates.add(nov5);
		nov6 = sdf.parse("Fri Nov 06 00:00:00 CST 2020");
		dates.add(nov6);
		nov10 = sdf.parse("Tue Nov 10 00:00:00 CST 2020");
		dates.add(nov10);
		nov12 = sdf.parse("Thu Nov 12 00:00:00 CST 2020");
		dates.add(nov12);
		
		System.out.println("Date: " + sept8.toString());
	}

	@Test
	void test() throws ParseException {
		
//		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
//
//		sept8 = sdf.parse("Thu Oct 29 00:00:00 CDT 2020");
		
//		IpConverter ipC = new IpConverter();
//		String[] args = {"/Users/bauerhs/git/evs-domain-resolve/src/test/resources/accesstest.log","mytestoutput.txt"};
//		ipC.run(args );
		
		ProcessLog processor = new ProcessLog();
		Stream<String> logLine = processor.readLogLine("mytestoutput.txt");
		List<String> logLines =  logLine.collect(Collectors.toList());
		
		String firstLine = logLines.get(0);
		assertTrue(dates.contains(getDateForString(parseDate(firstLine))));
		
		List<String> sept8Log = logLines.stream().filter(x -> sept8.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept8Log.get(0)));
		System.out.println(parseDuration(sept8Log.get(0)));
		assertTrue(parseDuration(sept8Log.get(0)).equals("21885000") || parseDuration(sept8Log.get(1)).equals("21885000"));
		assertTrue(parseDuration(sept8Log.get(0)).equals("850000") || parseDuration(sept8Log.get(1)).equals("850000"));
		 
	}
	
	public boolean isSameDate(Date logDate, Date testDate) {
		return logDate.equals(testDate);
	}
	
	public String parseDate(String logLine) {
		 final String regex =  "day: (\\w{3} \\w{3} \\d{2} \\d{2}:\\d{2}:\\d{2} \\w{3} \\d{4})";
		   String date = null;
	        final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(logLine);
			if(matcher.find()) {
				date = matcher.group(1);
			}
			
			return date;
	}
	
	public Date getDateForString(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		Date dayDate = null;
    	try {
			dayDate = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return dayDate;
	}
	
	public String parseDuration(String logLine) {
		 final String regex =  "duration: (\\d+)";
		   String duration = null;
	        final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(logLine);
			if(matcher.find()) {
				duration = matcher.group(1);
			}
			
			return duration;
	}
	

}
