package com.lucaslng.engine.renderer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.lucaslng.engine.EngineSettings;
public class GameFrame extends JFrame {

	public GameFrame(EngineSettings engineSettings) {
		super(engineSettings.title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(engineSettings.windowSize);
	}

}