package com.lucaslng.engine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.sound.sampled.*;

public class SoundHandler {

	static final private HashMap<String, Clip> clipMap;

	static {
		clipMap = new HashMap<>();
		try {
			Files.walk(Paths.get("assets/sounds")).filter(Files::isRegularFile).forEach((p) -> {
				try {
					AudioInputStream sound = AudioSystem.getAudioInputStream(p.toFile());
					Clip clip = AudioSystem.getClip();
					clip.open(sound);
					String name = p.getFileName().toString();
					clipMap.put(name.substring(0, name.length() - 4), clip); // remove .wav file extension
				} catch (Exception e) {
				}
			});

		} catch (Exception e) {
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
