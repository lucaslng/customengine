package com.lucaslng;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.lwjgl.opengl.awt.GLData;

public class Game {

    private final JFrame frame;
    private final GLData data;
    private final GameCanvas canvas;
    private final Thread renderThread;

    public Game() {
        frame = new GameFrame();
        data = Settings.getGLData();
        frame.add(canvas = new GameCanvas(data), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        renderThread = new Thread(new RenderLoop(canvas));
    }

    public void run() {
        renderThread.start();
        
    }
}