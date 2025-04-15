package me.adamix.runner;

import me.adamix.jframework.JFramework;
import me.adamix.jframework.JGame;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.flags.Flags;
import me.adamix.jframework.keyboard.Keyboard;
import me.adamix.jframework.scene.JScene;
import me.adamix.jframework.screen.JScreen;
import me.adamix.runner.background.Background;
import me.adamix.runner.gameplay.GameplayScene;
import me.adamix.runner.mainscene.MainScene;
import me.adamix.runner.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

public class Runner implements JGame {
	private Background background;

	public static void main(String[] args) {
		JFramework.create(new Runner());
	}

	@Override
	public void init() {
		JFramework.setTitle("Runner");
		JFramework.setTraceLogLevel(4);
		JFramework.setConfigFlags(Flags.FLAG_WINDOW_RESIZABLE);
		JFramework.setTargetFps(120);
	}

	@Override
	public void create() {
//		JFramework.addScene("main", new MainScene());
		JFramework.addScene("gameplay", new GameplayScene());

		// Load assets
		TextureManager.addTexture(10000, "assets/bg/background_1.png");
		TextureManager.addTexture(10001, "assets/bg/background_2.png");

		background = new Background();
	}

	@Override
	public void update(@NotNull UpdateContext ctx) {
		if (ctx.isKeyPressed(Keyboard.KEY_F1)) {
			JFramework.toggleDebug();
		}
		if (ctx.isKeyPressed(Keyboard.KEY_F11)) {
			JFramework.toggleFullscreen();
		}

		background.update(ctx);

		JScene currentScene = JFramework.getCurrentScene();
		if (currentScene != null) {
			currentScene.update(ctx);
		}
	}

	@Override
	public void render(@NotNull JScreen screen) {

		screen.begin();

		RenderContext ctx = screen.getRenderContext();

		background.render(ctx);

		JScene currentScene = JFramework.getCurrentScene();
		if (currentScene != null) {
			currentScene.render(ctx);
		}

		if (JFramework.isDebugMode()) {
			JFramework.renderDebug(ctx);
		}

		screen.end();
	}

	@Override
	public void dispose() {
		TextureManager.dispose();
	}
}