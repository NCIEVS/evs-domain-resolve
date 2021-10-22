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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ProcessLog {
	
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

	
	public String readLogLineWhois(String line) {
		
		String regexEmail = "OrgTechEmail:  \\S+@\\S+\\.(\\S+)";
		Pattern pEmail = Pattern.compile(regexEmail, Pattern.DOTALL);

		
		String domainTemp = "UNKNOWN";
			Matcher m = pEmail.matcher(line);
			if (m.find()) {
				// extract the domain and set it
				try {
					domainTemp = m.group(1);
					// possibly run against a lookup table, like the process
					// scripts, to get a better hostName
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			
			return domainTemp;

	}
    

    public String getTimeStampFromLine( String line) {
    	String ip = null;
    	//Finding the pattern of the time stamp by searching for the square brackets and populating the interior
        final String regex =  "(\\[\\d{2}\\/[a-z,A-Z]+\\/\\d{4}:\\d{2}:\\d{2}:\\d{2} [\\+,-]\\d{4}\\])";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			ip = matcher.group(1);
		}
    	return ip;
    }
    
    
    public int getLengthFromLine( String line) {
    	String ip = null;
        final String regex =  "[\\d\\.]+ - - \\[\\d+\\/\\w+\\S+ \\S+\\] \"[\\S ]+ \\d{3} (\\d+)";
   
        final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			ip = matcher.group(1);
		}
		if(ip == null || ip.equals("")) {return 0;}
		else{return Integer.valueOf(ip);}
    }

    
    public String getIpFromLine(String line) {
    	if(line == null 
    			|| line.equals("") 
    			|| isBot(line)) { return "NOIP";}
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
				e.printStackTrace();
			}finally { if(br != null) {try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}}
			//Filter raw ips
			if(output != null && (output.equals(IP + ".")  || output.equals(IP))) {return null;}
            return output;
    	}
    
    public boolean isBot(String domain) {
    	if(domain.contains("bot") || domain.contains("crawl")) {System.out.println("Is bot"); return true;}
    	return false;
    }

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
			
			//pass in the current host to the whois
			out.write(IP + "\r\n");
			out.flush();

			in = new BufferedInputStream(theSocket.getInputStream());
			int c;
			if(in == null) {return null;}
			StringBuffer response = new StringBuffer();
			while ((c = in.read()) != -1) {
				// System.out.write(c);
				response.append((char) c);
				// response.append("\r\n");
			}
			return response.toString();
			// whois_parser(response.toString());
		} catch (UnknownHostException e) {

			// TODO Auto-generated catch block
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
    
}
