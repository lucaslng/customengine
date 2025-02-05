package com.lucaslng;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import static com.lucaslng.ThreadHandler.signalTerminate;
import static com.lucaslng.ThreadHandler.signalTerminated;

public class GameFrame extends JFrame {

	// final GLData glData;
	// final AWTGLCanvas canvas;

	public GameFrame() {
		super(Settings.title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(600, 600));
	}

	@Override
	public void dispose() {
		// request the cleanup
		signalTerminate.release();
		try {
			// wait until the thread is done with the cleanup
			signalTerminated.acquire();
		} catch (InterruptedException ignored) {
		}
		super.dispose();
	}
}