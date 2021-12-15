package nci.nih.gov.evs.ipconverter;

import java.util.Date;
import java.util.Hashtable;

public class CustomLog {
	
	// Model element for log output items
   	class IpConsolidated{
   		public IpConsolidated(String ip, String tmstp, long size, long duration, Date day) {
   			this.ip = ip;
   			this.tmstp = tmstp;
   			this.size = size;
   			this.duration = duration;
   			this.day = day;
   		}
  		
   		public IpConsolidated() {
		}

		String ip;
   		String tmstp;
   		long size;
   		long duration;
   		Date day;
   	}
   	
	// The output string from the model element into the custom log
	
	public String createLogOutPutLineFromHashTable(IpConsolidated conso) {
		StringBuilder logLine = new StringBuilder();
		ProcessLog processor = new ProcessLog();
		String domain = processDomainFromResolvedIP(processor.getTLDString(conso.ip));
		if(domain.equals("UNKNOWN")) 
		{ domain = processor.readLogLineWhois(processor.whois(conso.ip));}
		logLine.append("timestamp: " + conso.tmstp + " ");
		logLine.append("domain: " + domain + " ");
		logLine.append("length: " + conso.size  + " ");
		logLine.append("duration: " + conso.duration  + " ");
		logLine.append("day: " + conso.day);
		return logLine.toString();	
	}
	
  // Filtering and trimming top level domains based on artifacts found after
  // resolution calls
	
   public String processDomainFromResolvedIP(String dugDNS) {
	   if(dugDNS == null || dugDNS.equals("")) return "UNKNOWN";
	   String tld = dugDNS.substring(0,dugDNS.lastIndexOf("."));
	   tld = tld.substring(tld.lastIndexOf(".") + 1,tld.length());
	   return tld;
   }
   
   // Making calls to domain resolution services and processing them to trimmed and qualified values
   
   public String processDomainfromLine(ProcessLog processor, String line) {
	   return processDomainFromResolvedIP(processor.getTLDString(processor.getIpFromLine(line)));
   }
   
   // Mapping the line from an access log by transforming it into a model element
   
   public void  mapLogLineToConsolidated(Hashtable<String, IpConsolidated> consolidatedIPs, String logLine) {
	   ProcessLog processor = new ProcessLog();
	   IpConsolidated consolidated = new IpConsolidated();
	   consolidated.ip = processor.getIpFromLine(logLine);
	   if(consolidated.ip.equals("NOIP")) {return;}
	   consolidated.tmstp = processor.getTimeStampFromLine(logLine);
	   consolidated.size = Integer.valueOf(processor.getLengthFromLine(logLine));
	   consolidated.day = processor.getDayDateFromLogLine(logLine);
	  if(consolidatedIPs.containsKey(consolidated.ip + consolidated.day)) 
	  	{IpConsolidated ip = consolidatedIPs.get(consolidated.ip + consolidated.day);
	  	 ip.size += consolidated.size;
	  	 ip.duration = processor.updateDurationByDayDate(ip.day, consolidated.day, ip.tmstp, consolidated.tmstp);
	  	}
	  else 
	   { consolidatedIPs.put(consolidated.ip + consolidated.day.toString(), consolidated);}
   }
}
