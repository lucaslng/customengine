package com.lucaslng.engine;

import java.io.BufferedInputStream;
import java.util.HashMap;

import javax.sound.sampled.*;

import com.lucaslng.engine.utils.FileReader;

public class SoundHandler {

	static final private HashMap<String, Clip> clipMap;

	static {
		String[] fileNames = { "click", "coin", "music", "old" };
		clipMap = new HashMap<>();
		try {
			for (String name : fileNames) {
				try {
					AudioInputStream sound = AudioSystem.getAudioInputStream(new BufferedInputStream(FileReader.getStream("sounds/" + name + ".wav")));
					Clip clip = AudioSystem.getClip();
					clip.open(sound);
					clipMap.put(name, clip);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start(String audio) {
		clipMap.get(audio).start();
	}

	public static void setFramePosition(String audio, int frames) {
		clipMap.get(audio).setFramePosition(frames);
	}

	public static void reset(String audio) {
		setFramePosition(audio, 0);
	}

	public static void play(String audio) {
		reset(audio);
		start(audio);
	}

	public static void loop(String audio) {
		reset(audio);
		clipMap.get(audio).loop(Clip.LOOP_CONTINUOUSLY);
	}

	public static void stop(String audio) {
		clipMap.get(audio).stop();
	}

}
