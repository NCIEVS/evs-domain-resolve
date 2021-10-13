package nci.nih.gov.evs.ipconverter;

import java.util.Hashtable;
import java.util.stream.Stream;

public class CustomLog {
	
   	class IpConsolidated{
   		public IpConsolidated(String ip, String tmstp, int size) {
   			this.ip = ip;
   			this.tmstp = tmstp;
   			this.size = size;
   		}
  		
   		public IpConsolidated() {
		}

		String ip;
   		String tmstp;
   		Integer size;
   	}
   	
	
	public String createLogOutPutLine(ProcessLog processor, String line) {
		StringBuilder logLine = new StringBuilder();
		String domain = processDomainfromLine(processor,line);
		if(domain.equals("UNKNOWN")) 
		{ domain = processor.readLogLineWhois(processor.whois(processor.getIpFromLine(line)));}
		logLine.append("timestamp: " + processor.getTimeStampFromLine(line) + " ");
		logLine.append("domain: " + domain + " ");
		logLine.append("length: " + processor.getLengthFromLine(line));
		return logLine.toString();	
	}
	
	public String createLogOutPutLineFromHashTable(IpConsolidated conso) {
		StringBuilder logLine = new StringBuilder();
		ProcessLog processor = new ProcessLog();
		String domain = processDomainFromResolvedIP(processor.getTLDString(conso.ip));
		if(domain.equals("UNKNOWN")) 
		{ domain = processor.readLogLineWhois(processor.whois(conso.ip));}
		logLine.append("timestamp: " + conso.tmstp + " ");
		logLine.append("domain: " + domain + " ");
		logLine.append("length: " + conso.size);
		return logLine.toString();	
	}
	
   public String processDomainFromResolvedIP(String dugDNS) {
	   if(dugDNS == null || dugDNS.equals("")) return "UNKNOWN";
	   String tld = dugDNS.substring(0,dugDNS.lastIndexOf("."));
	   tld = tld.substring(tld.lastIndexOf(".") + 1,tld.length());
	   return tld;
   }
   
   public String processDomainfromLine(ProcessLog processor, String line) {
	   return processDomainFromResolvedIP(processor.getTLDString(processor.getIpFromLine(line)));
   }
   
   
   public void  mapLogLineToConsolidated(Hashtable<String, IpConsolidated> consolidatedIPs, String logLine) {
	   ProcessLog processor = new ProcessLog();
	   IpConsolidated consolidated = new IpConsolidated();
	   consolidated.ip = processor.getIpFromLine(logLine);
	   if(consolidated.ip.equals("NOIP")) {return;}
	   consolidated.tmstp = processor.getTimeStampFromLine(logLine);
	   consolidated.size = Integer.valueOf(processor.getLengthFromLine(logLine));
	  if(consolidatedIPs.containsKey(consolidated.ip)) 
	  	{ consolidatedIPs.get(consolidated.ip).size += consolidated.size;}
	  else 
	   { consolidatedIPs.put(consolidated.ip, consolidated);}
   }
   
   public static void main(String ... args) {
	   ProcessLog processor = new ProcessLog();
	   System.out.println(new CustomLog().processDomainFromResolvedIP("crawl-66-249-75-92.googlebot.com."));	   
   }
}
