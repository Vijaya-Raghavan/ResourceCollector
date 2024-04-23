package com.example.linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandExecution extends TimerTask {

	private static Logger LOG = LoggerFactory.getLogger(CommandExecution.class);

	private static ExecutorService pool = Executors.newSingleThreadExecutor();

	static {
		Timer t=new Timer();
		t.scheduleAtFixedRate(new CommandExecution(), 0,1000);
	}

	public void htop() throws Exception {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("/bin/sh", "-c", "sleep 3 | htop  > /tmp/htop.out");
		builder.command("/bin/sh", "-c", "head /tmp/htop.out  | tail -c +10");
		//builder.directory(new File(System.getProperty("user.home")));
		Process process = builder.start();
		ProcessReadTask task = new ProcessReadTask(process.getInputStream());
		Future<List<String>> future = pool.submit(task);
		List<String> result = future.get(10, TimeUnit.SECONDS);
		for (String s : result) {
			LOG.info(s);
		}
		int exitCode = process.waitFor();
		LOG.info("\nExited with error code : " + exitCode);
	}


	@Override
	public void run() {
		try {
			htop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		new CommandExecution();
		/*ProcessBuilder builder = new ProcessBuilder();
		builder.command("/bin/sh", "-c", "echo | htop  > /tmp/htop.out");
		builder.command("/bin/sh", "-c", "cat /tmp/htop.out");
		//builder.directory(new File(System.getProperty("user.home")));
		Process process = builder.start();
		ProcessReadTask task = new ProcessReadTask(process.getInputStream());
		Future<List<String>> future = pool.submit(task);
		List<String> result = future.get(5, TimeUnit.SECONDS);
		for (String s : result) {
			LOG.info(s);
		}
		int exitCode = process.waitFor();
		LOG.info("\nExited with error code : " + exitCode);*/
	}

	private static class ProcessReadTask implements Callable<List<String>> {

		private InputStream inputStream;

		public ProcessReadTask(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public List<String> call() {
			return new BufferedReader(new InputStreamReader(inputStream))
					.lines()
					.collect(Collectors.toList());
		}
	}
}


