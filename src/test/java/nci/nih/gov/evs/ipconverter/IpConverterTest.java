package nci.nih.gov.evs.ipconverter;



import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import org.junit.jupiter.api.Test;

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

		
		List<String> sept8LogDay = logLines.stream().filter(x -> sept8.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept8LogDay.get(0)));
		System.out.println(parseDuration(sept8LogDay.get(0)));
		
		assertTrue(parseDuration(sept8LogDay.get(0)).equals("21885000") || parseDuration(sept8LogDay.get(1)).equals("21885000"));
		assertTrue(parseDuration(sept8LogDay.get(0)).equals("850000") || parseDuration(sept8LogDay.get(1)).equals("850000"));
		
		assertTrue(parseSize(sept8LogDay.get(0)).equals("2250339") || parseSize(sept8LogDay.get(1)).equals("2250339"));
		assertTrue(parseSize(sept8LogDay.get(0)).equals("75861") || parseSize(sept8LogDay.get(1)).equals("75861"));
		
		List<String> sept9LogDay = logLines.stream().filter(x -> sept9.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept9LogDay.get(0)));
		System.out.println(parseDuration(sept9LogDay.get(0)));
		assertTrue(parseDuration(sept9LogDay.get(0)).equals("0") && parseDuration(sept9LogDay.get(1)).equals("0"));
		//assertTrue(parseDuration(sept9LogDay.get(0)).equals("850000") || parseDuration(sept9LogDay.get(1)).equals("850000"));
		
		assertTrue(parseSize(sept9LogDay.get(0)).equals("885629") || parseSize(sept9LogDay.get(1)).equals("885629"));
		assertTrue(parseSize(sept9LogDay.get(0)).equals("4736") || parseSize(sept9LogDay.get(1)).equals("4736"));
		
		List<String> sept10LogDay = logLines.stream().filter(x -> sept10.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept10LogDay.get(0)));
		System.out.println(parseDuration(sept10LogDay.get(0)));
		assertTrue(parseDuration(sept10LogDay.get(0)).equals("31086000") || parseDuration(sept10LogDay.get(1)).equals("31086000"));
		assertTrue(parseDuration(sept10LogDay.get(0)).equals("1252000") || parseDuration(sept10LogDay.get(1)).equals("1252000"));
		
		assertTrue(parseSize(sept10LogDay.get(0)).equals("955816") || parseSize(sept10LogDay.get(1)).equals("955816"));
		assertTrue(parseSize(sept10LogDay.get(0)).equals("5713") || parseSize(sept10LogDay.get(1)).equals("5713"));
		
		List<String> sept11LogDay = logLines.stream().filter(x -> sept11.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept11LogDay.get(0)));
		System.out.println(parseDuration(sept11LogDay.get(0)));
		assertTrue(parseDuration(sept11LogDay.get(0)).equals("0"));
		
		assertTrue(parseSize(sept11LogDay.get(0)).equals("196"));
		
		List<String> sept30LogDay = logLines.stream().filter(x -> sept30.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(sept30LogDay.get(0)));
		System.out.println(parseDuration(sept30LogDay.get(0)));

		assertTrue(parseDuration(sept30LogDay.get(0)).equals("1000") || parseDuration(sept30LogDay.get(1)).equals("1000"));
		assertTrue(parseDuration(sept30LogDay.get(0)).equals("0") || parseDuration(sept30LogDay.get(1)).equals("0"));
		
		assertTrue(parseSize(sept30LogDay.get(0)).equals("3090342") || parseSize(sept30LogDay.get(1)).equals("3090342"));
		assertTrue(parseSize(sept30LogDay.get(0)).equals("60201") || parseSize(sept30LogDay.get(1)).equals("60201"));
		
		List<String> oct1LogDay = logLines.stream().filter(x -> oct1.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration( oct1LogDay.get(0)));
		System.out.println(parseDuration( oct1LogDay.get(0)));

		assertTrue(parseDuration( oct1LogDay.get(0)).equals("19695000"));
		
		assertTrue(parseSize( oct1LogDay.get(0)).equals("38229"));
		
		List<String> oct2LogDay = logLines.stream().filter(x -> oct2.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(oct2LogDay.get(0)));
		System.out.println(parseDuration(oct2LogDay.get(0)));

		assertTrue(parseDuration(oct2LogDay.get(0)).equals("1412000") || parseDuration(oct2LogDay.get(1)).equals("1412000") 
				|| parseDuration(oct2LogDay.get(2)).equals("1412000"));
		assertTrue(parseDuration(oct2LogDay.get(0)).equals("44000") || parseDuration(oct2LogDay.get(1)).equals("44000")
				|| parseDuration(oct2LogDay.get(2)).equals("44000"));
		assertTrue(parseDuration(oct2LogDay.get(0)).equals("6000") || parseDuration(oct2LogDay.get(1)).equals("6000")
				|| parseDuration(oct2LogDay.get(2)).equals("6000"));
		
		assertTrue(parseSize(oct2LogDay.get(0)).equals("4093") || parseSize(oct2LogDay.get(1)).equals("4093") 
				|| parseSize(oct2LogDay.get(2)).equals("4093"));
		assertTrue(parseSize(oct2LogDay.get(0)).equals("957800") || parseSize(oct2LogDay.get(1)).equals("957800") 
				|| parseSize(oct2LogDay.get(2)).equals("957800"));
		assertTrue(parseSize(oct2LogDay.get(0)).equals("13616") || parseSize(oct2LogDay.get(1)).equals("13616") 
				|| parseSize(oct2LogDay.get(2)).equals("13616"));
		
		List<String> oct5LogDay = logLines.stream().filter(x -> oct5.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(oct5LogDay.get(0)));
		System.out.println(parseDuration(oct5LogDay.get(0)));

		assertTrue(parseDuration(oct5LogDay.get(0)).equals("21315000") || parseDuration(oct5LogDay.get(1)).equals("21315000"));
		assertTrue(parseDuration(oct5LogDay.get(0)).equals("1000") || parseDuration(oct5LogDay.get(1)).equals("1000"));
		
		assertTrue(parseSize(oct5LogDay.get(0)).equals("221605") || parseSize(oct5LogDay.get(1)).equals("221605"));
		assertTrue(parseSize(oct5LogDay.get(0)).equals("892183") || parseSize(oct5LogDay.get(1)).equals("893183"));
		
		List<String> oct28LogDay = logLines.stream().filter(x -> oct28.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(oct28LogDay.get(0)));
		System.out.println(parseDuration(oct28LogDay.get(0)));

		assertTrue(parseDuration(oct28LogDay.get(0)).equals("41000"));
		
		assertTrue(parseSize(oct28LogDay.get(0)).equals("26345"));
		
		List<String> oct29LogDay = logLines.stream().filter(x -> oct29.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(oct29LogDay.get(0)));
		System.out.println(parseDuration(oct29LogDay.get(0)));

		assertTrue(parseDuration(oct29LogDay.get(0)).equals("0"));
		
		assertTrue(parseSize(oct29LogDay.get(0)).equals("892382"));
		
		List<String> nov3LogDay = logLines.stream().filter(x -> nov3.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(nov3LogDay.get(0)));
		System.out.println(parseDuration(nov3LogDay.get(0)));

		assertTrue(parseDuration(nov3LogDay.get(0)).equals("218000") || parseDuration(nov3LogDay.get(1)).equals("218000"));
		assertTrue(parseDuration(nov3LogDay.get(0)).equals("2000") || parseDuration(nov3LogDay.get(1)).equals("2000"));
		
		assertTrue(parseSize(nov3LogDay.get(0)).equals("959539") || parseSize(nov3LogDay.get(1)).equals("959539"));
		assertTrue(parseSize(nov3LogDay.get(0)).equals("892141") || parseSize(nov3LogDay.get(1)).equals("893141"));
		
		List<String> nov5LogDay = logLines.stream().filter(x -> nov5.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(nov5LogDay.get(0)));
		System.out.println(parseDuration(nov5LogDay.get(0)));

		assertTrue(parseDuration(nov5LogDay.get(0)).equals("0"));
		
		assertTrue(parseSize(nov5LogDay.get(0)).equals("892382"));
		
		List<String> nov6LogDay = logLines.stream().filter(x -> nov6.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(nov6LogDay.get(0)));
		System.out.println(parseDuration(nov6LogDay.get(0)));

		assertTrue(parseDuration(nov6LogDay.get(0)).equals("0"));
		
		assertTrue(parseSize(nov6LogDay.get(0)).equals("0"));
		
		List<String>nov10LogDay = logLines.stream().filter(x -> nov10.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(nov10LogDay.get(0)));
		System.out.println(parseDuration(nov10LogDay.get(0)));

		assertTrue(parseDuration(nov10LogDay.get(0)).equals("29000"));
		
		assertTrue(parseSize(nov10LogDay.get(0)).equals("9547"));
		
		List<String> nov12LogDay = logLines.stream().filter(x -> nov12.equals(getDateForString(parseDate(x)))).collect(Collectors.toList());
		assertNotNull(parseDuration(nov12LogDay.get(0)));
		System.out.println(parseDuration(nov12LogDay.get(0)));

		assertTrue(parseDuration(nov12LogDay.get(0)).equals("9111000"));
		
		assertTrue(parseSize(nov12LogDay.get(0)).equals("898407"));
		
		
		 
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
	
	public String parseSize(String logLine) {
		 final String regex =  "length: (\\d+)";
		   String duration = null;
	        final Pattern pattern = Pattern.compile(regex);
			final Matcher matcher = pattern.matcher(logLine);
			if(matcher.find()) {
				duration = matcher.group(1);
			}
			
			return duration;
	}
	

}
