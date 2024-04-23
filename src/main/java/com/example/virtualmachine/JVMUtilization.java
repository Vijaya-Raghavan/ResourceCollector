package com.example.virtualmachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class JVMUtilization extends TimerTask {

	private static Logger LOG = LoggerFactory.getLogger(JVMUtilization.class);

	static {
		Timer t=new Timer();
		t.scheduleAtFixedRate(new JVMUtilization(), 0,5*1000);
	}

	public void printMemoryUtilization () {
		LOG.info(" \t Free Memory \t Total Memory \t Max Memory");
		LOG.info(" \t " + Runtime.getRuntime().freeMemory() +
					" \t \t " + Runtime.getRuntime().totalMemory() +
					" \t \t " + Runtime.getRuntime().maxMemory());
	}

	@Override
	public void run() {
		printMemoryUtilization();
	}

	public static void main(String[] args) {
		new JVMUtilization();
	}
}
