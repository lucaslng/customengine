package com.lucaslng;


import java.util.concurrent.Semaphore;

public class ThreadHandler {
	public static Semaphore signalTerminate = new Semaphore(0);
	public static Semaphore signalTerminated = new Semaphore(0);
}
