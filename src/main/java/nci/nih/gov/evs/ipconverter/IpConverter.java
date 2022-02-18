package nci.nih.gov.evs.ipconverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import nci.nih.gov.evs.ipconverter.CustomLog.IpConsolidated;

public class IpConverter {
	
	final BlockingQueue<CustomLog.IpConsolidated> queue = new LinkedBlockingQueue<>(100);

	ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
	CustomLog log = new CustomLog();

	public static void main(String[] args) {
		new IpConverter().run(args);

	}
	
	public void run(String ... args){
		String accessPath = "/Users/bauerhs/git/evs-domain-resolve/src/main/java/nci/nih/gov/evs/ipconverter/access.sample.log";
		String filePath = "myFile.txt";
		ProcessLog processor = new ProcessLog();

		Stream<String> lines = processor.readLogLine(args.length == 0?accessPath:args[0]);

		Hashtable<String, CustomLog.IpConsolidated> consolidated = new Hashtable<String, CustomLog.IpConsolidated>();
		lines.forEach(x -> log.mapLogLineToConsolidated(consolidated, x));
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args.length == 0?filePath:args[1])))) {
			consolidated.values()
				.stream()
				.map(x -> log.createLogOutPutLineFromHashTable(x))
				.forEach(y -> {
				try {
					writer.write(y);
					writer.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			});
			
			if(writer != null)
			{
				try {
				writer.close();
				} catch (IOException e) {
				e.printStackTrace();
			}}
		} 
		catch (IOException ex) {
		   ex.printStackTrace();
		}
		
	}
	
	
	//Under Construction
	public String blockingTimedQuery(CustomLog.IpConsolidated in) {

	scheduler.scheduleAtFixedRate(() -> { queue.offer(in);
	}, 0, 2, TimeUnit.SECONDS);

	        try {
				return log.createLogOutPutLineFromHashTable(queue.take());
			} catch (InterruptedException e) {
				throw new RuntimeException(
						"Threaded concurrency error querying external resources", e);
			}

	}

}
