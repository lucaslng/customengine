package com.lucaslng.engine.systems;

public class LevelTransition {
	private boolean isTransitioning;
	private float transitionTime;
	private final float fadeOutDuration;
	private final float fadeInDuration;
	private final float holdDuration;
	private Runnable onTransitionComplete;
	
	public LevelTransition() {
		this.isTransitioning = false;
		this.transitionTime = 0f;
		this.fadeOutDuration = 0.8f;
		this.fadeInDuration = 0.8f;
		this.holdDuration = 0.6f;     // black screen time
	}
	
	public void startTransition(Runnable onComplete) {
		this.isTransitioning = true;
		this.transitionTime = 0f;
		this.onTransitionComplete = onComplete;
	}
	
	public void update(float dt) {
		if (!isTransitioning) return;
		
		transitionTime += dt;
		
		float totalDuration = fadeOutDuration + holdDuration + fadeInDuration;

		if (transitionTime >= fadeOutDuration + holdDuration * 0.5f && onTransitionComplete != null) {
			onTransitionComplete.run();
			onTransitionComplete = null;
		}

		if (transitionTime >= totalDuration) {
			isTransitioning = false;
			transitionTime = 0f;
		}
	}
	
	public boolean isTransitioning() {
		return isTransitioning;
	}

	public float getFadeAlpha() {
		if (!isTransitioning) return 0f;
		
		float t = transitionTime;
		
		if (t < fadeOutDuration) {
			return t / fadeOutDuration;
		}
		
		if (t < fadeOutDuration + holdDuration) {
			return 1f;
		}

		float fadeInStart = fadeOutDuration + holdDuration;
		float fadeInProgress = (t - fadeInStart) / fadeInDuration;
		return 1f - fadeInProgress;
	}
}