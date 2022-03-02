package nci.nih.gov.evs.ipconverter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ProcessLog {
	
	//Simple file reading mechanism to stream
	
	public Stream<String> readLogLine(String fileName) {

	    Path path;
	    Stream<String> lines = null;
		try {
			path = Paths.get(fileName);
		    lines = Files.lines(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return lines;
	}
	
	//Test script for a log line read
	
	public List<String> readLogLineToTestOutput(String fileName) {
		
	    Path path;
	    Stream<String> lines = null;
		try {
			path = Paths.get(fileName);
		    lines = Files.lines(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
 

	    lines.map(x->getIpFromLine(x))
	    		.distinct()
	    		.filter(x -> x != null)
	    		.map(y -> readLogLineWhois(whois(y)))
	    		.forEach(z -> System.out.println(z));

		return null;
	}

	// The whois service call has quite a lot of noise. We use a regex pattern to filter out
	// the specific domain containing value further filtered to contain only the top level domain
	
	public String readLogLineWhois(String line) {
		
		String regexEmail = "OrgTechEmail:  \\S+@\\S+\\.(\\S+)";
		Pattern pEmail = Pattern.compile(regexEmail, Pattern.DOTALL);

		
		String domainTemp = "UNKNOWN";
			Matcher m = pEmail.matcher(line);
			if (m.find()) {
				// extract the domain and set it
				try {
					domainTemp = m.group(1);
				} catch (Exception e) {
					throw new RuntimeException("Parsing a whois pattern has failed", e);
				}				
			}
			
			return domainTemp;

	}
    
    
    // Getting the timestamp representation of a particular date format from Apache formatted access log

    public String getTimeStampFromLine( String line) {
    	String ip = null;
    	//Finding the pattern of the time stamp by searching for the square brackets and populating the interior
        final String regex =  "(\\d{2}\\/[a-z,A-Z]+\\/\\d{4}:\\d{2}:\\d{2}:\\d{2} [\\+,-]\\d{4})";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			ip = matcher.group(1);
		}
    	return ip;
    }
    
    // Getting the millisecond representation of a particular date format
    
    public long getDurationMilliSeconds(String rawDate) {
 	   DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
 	   builder.appendPattern("dd/MMM/yyyy:HH:mm:ss Z");
 	   DateTimeFormatter f = builder.toFormatter();
 	   TemporalAccessor dateTime = f.parse(rawDate);
 	   return Instant.from(dateTime).toEpochMilli();
    }
    
    //Regex query returning the size of download to browser from Apache formatted access log
    
    public int getLengthFromLine( String line) {
    	String ip = null;
        final String regex =  "[\\d\\.]+ - - \\[\\d+\\/\\w+\\S+ \\S+\\] \"[\\S ]+ \\d{3} (\\d+)";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			ip = matcher.group(1);
		}
		if(ip == null || ip.equals("")) {return 0;}
		if(Integer.valueOf(ip) < 0) 
		 { return 0;}
		else{return Integer.valueOf(ip);}
    }

    // Regex query returning IP from an Apache formatted access log
    
    public String getIpFromLine(String line) {
    	if(line == null 
    			|| line.equals("") 
    			|| isBot(line) 
    			|| isHealthPing(line))
    				{ return "NOIP";}
    	String ip = null;
        final String regex = 
        		"^([\\d\\/.]+)";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			ip = matcher.group(1);
		}
    	return ip;
    }
    


    // Domain resolved using a linux based system call to dig. Returns a domain name
   
	public String getTLDString(String IP) {
    		if(IP == null) {return null;}
    		BufferedReader br = null;
            java.lang.ProcessBuilder processBuilder = new java.lang.ProcessBuilder("dig","-x", IP, "+short");
            
            Process process;
            String output = "UNKNOWN";
			try {
				process = processBuilder.start();
	            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            output = br.readLine();
	            br.close();
			} catch (IOException e) {
				throw new RuntimeException("Dig command line call has failed", e);
			}finally { if(br != null) {try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}}
			//Filter raw ips
			if(output != null && (output.equals(IP + ".")  || output.equals(IP))) {return null;}
            return output;
    	}
	
    // Filter for health checker ping
    private boolean isHealthPing(String domain) {
    	if(domain.contains("ELB-HealthChecker") || domain.contains("nagios") || domain.contains("bitdiscovery")) { return true;}
    	return false;
	}
    
	// Filter for google bot entries in log.
    public boolean isBot(String domain) {
    	if(domain.contains("Googlebot") || domain.contains("bingbot") 
    			|| domain.contains("qwant") || domain.contains("Nimbostratus-Bot") 
    			|| domain.contains("CensysInspect") || domain.contains("l9explore") || domain.contains("bitdiscovery")
    			|| domain.contains("l9tcpid") || domain.contains("302"))
    	{System.out.println("Removing bot reference"); return true;}
    	return false;
    }

    // Domain name resolved by representation in an email address
    
	public String whois(String IP) {
		String serverName = System
				.getProperty("WHOIS_SERVER", "whois.arin.net");
		InetAddress server = null;
		InputStream in = null;
		Writer out = null;
		Socket theSocket = null;
		try {
			server = InetAddress.getByName(serverName);
			theSocket = new Socket(server, 43);
			out = new OutputStreamWriter(theSocket.getOutputStream(),
					"8859_1");
			
			//pass in the current IP to the whois
			out.write(IP + "\r\n");
			out.flush();

			in = new BufferedInputStream(theSocket.getInputStream());
			int c;
			if(in == null) {return null;}
			StringBuffer response = new StringBuffer();
			while ((c = in.read()) != -1) {
				response.append((char) c);
			}
			return response.toString();

		} catch (UnknownHostException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if(in != null) {try {

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
			
			if(out != null)
			{try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
			
			if(theSocket != null)
			{try {
				theSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		}

	}
	
	public static void main(String ...args) {
	 	   DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
	 	   builder.appendPattern("dd/MMM/yyyy:HH:mm:ss Z");
	 	   DateTimeFormatter f = builder.toFormatter();
	 	   TemporalAccessor dateTime = f.parse("23/Dec/2020:17:27:54 +0100");
	 	   System.out.println(Instant.from(dateTime).toEpochMilli());

	 	   SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
	 	    try {
				Date firstDate = sdf.parse("23/Dec/2020");
		 	    Date secondDate = sdf.parse("24/Dec/2020");
		 	    
		 	    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		 	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		 	    System.out.println("" + diff);
		 	    
		 	    
				Date firstSameDate = sdf.parse("24/Dec/2020");
		 	    Date secondSameDate = sdf.parse("24/Dec/2020");
		 	    
		 	    long diffInSameMillies = Math.abs(secondSameDate.getTime() - firstSameDate.getTime());
		 	    long sameDiff = TimeUnit.DAYS.convert(diffInSameMillies, TimeUnit.MILLISECONDS);
		 	    System.out.println("" + sameDiff);
			} catch (ParseException e) {
				throw new RuntimeException("Date from Log is Incompatible with Expected Format", e);
			}

	}

	// Duration concept is based on a daily review of IP contacts where the final IP contact
	// represents the temporal boundary for an accumulated duration when compared with the initial
	// contact
	
	public long updateDurationByDayDate(Date day, Date newDay, String tmstp, String tmstp2) {
		if(!isNewDayDate(day, newDay)) {
		return Math.abs(getDurationMilliSeconds(tmstp) - getDurationMilliSeconds(tmstp2));
		}else{return 0;}
	}
	
	// We want the day portion of the date to frame each IP related data row

	public Date getDayDateFromLogLine(String logLine) {
		
    	String date = null;
    	//Finding the pattern of the time stamp by searching for the square brackets and populating the interior
        final String regex =  "(\\d{2}\\/[a-z,A-Z]+\\/\\d{4})";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(logLine);
		if(matcher.find()) {
			date = matcher.group(1);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
		Date dayDate = null;
    	try {
			dayDate = sdf.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException("Date from Log is Incompatible with Expected Format", e);
		}
		return dayDate;		
	}
	
	public boolean isNewDayDate(Date oldDate, Date newDate) {
		 	    long diffInMillies = Math.abs(oldDate.getTime() - newDate.getTime());
		 	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		 	    return diff >= 1;
	}
	

}
