package de.keksuccino.fancymenu.menu.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import de.keksuccino.core.gui.screens.SimpleLoadingScreen;
import de.keksuccino.core.rendering.animation.IAnimationRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class AnimationLoadingScreen extends SimpleLoadingScreen {

	private Screen fallback;
	private List<IAnimationRenderer> renderers = new ArrayList<IAnimationRenderer>();
	private boolean ready = false;
	private int cachedFPS;
	private boolean cachedLoop;
	private boolean done = false;
	private volatile boolean preparing = false;

	public AnimationLoadingScreen(@Nullable Screen fallbackGui, IAnimationRenderer... renderer) {
		super(Minecraft.getInstance());
		this.renderers.addAll(Arrays.asList(renderer));
		this.fallback = fallbackGui;
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		IAnimationRenderer current = this.getCurrentRenderer();
		
		if (current == null) {
			this.done = true;
			this.onFinished();
			if (this.fallback != null) {
				Minecraft.getInstance().displayGuiScreen(this.fallback);
			}
		} else {
			if (!this.ready) {
				this.cachedFPS = current.getFPS();
				this.cachedLoop = current.isGettingLooped();
				current.setFPS(-1);
				current.setLooped(false);
				this.ready = true;
			}
			
			if (!current.isReady()) {
				if (!this.preparing) {
					AnimationLoadingScreen loading = this;
					this.preparing = true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							loading.setStatusText("Loading animation frames for " + current.getPath());
							current.prepareAnimation();
							System.gc();
							loading.preparing = false;
						}
					}).start();
				}
			} else {
				if (!current.isFinished()) {
					this.setStatusText("Pre-rendering animation " + current.getPath());
					if (current instanceof AdvancedAnimation) {
						((AdvancedAnimation)current).setMuteAudio(true);
					}
					current.render();
					if (current instanceof AdvancedAnimation) {
						((AdvancedAnimation)current).setMuteAudio(false);
					}
				} else {
					current.setFPS(this.cachedFPS);
					current.setLooped(this.cachedLoop);
					current.resetAnimation();
					this.renderers.remove(0);
					this.ready = false;
				}
			}
		}
		
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}
	
	private IAnimationRenderer getCurrentRenderer() {
		if (!this.renderers.isEmpty()) {
			return this.renderers.get(0);
		}
		return null;
	}
	
	public void onFinished() {
		this.setStatusText("Done!");
	}

	public boolean loadingFinished() {
		return this.done;
	}
}
