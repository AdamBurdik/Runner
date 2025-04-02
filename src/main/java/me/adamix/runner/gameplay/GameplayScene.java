package me.adamix.runner.gameplay;

import lombok.Getter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.keyboard.Keyboard;
import me.adamix.jframework.scene.JScene;
import me.adamix.runner.gameplay.board.Board;
import me.adamix.runner.gameplay.player.Player;
import me.adamix.runner.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GameplayScene implements JScene {
	private final Random random = new Random(52858728);
	@Getter
	private static Board board;
	@Getter
	private static Player player;

	@Override
	public void init() {
		TextureManager.addTexture(-2, "assets/tiles/debug.png");
		TextureManager.addTexture(0, "assets/tiles/single/single_tile.png");
		TextureManager.addTexture(1, "assets/tiles/single/single_corruption_7.png");
		TextureManager.addTexture(2, "assets/tiles/single/single_corruption_8.png");

		TextureManager.addTexture(3, "assets/tiles/square/bottom_left.png");
		TextureManager.addTexture(4, "assets/tiles/square/bottom_left_corruption_7.png");
		TextureManager.addTexture(5, "assets/tiles/square/bottom_left_corruption_8.png");

		TextureManager.addTexture(6, "assets/tiles/square/bottom_right.png");
		TextureManager.addTexture(7, "assets/tiles/square/bottom_right_corruption_7.png");
		TextureManager.addTexture(8, "assets/tiles/square/bottom_right_corruption_8.png");

		TextureManager.addTexture(9, "assets/tiles/square/top_left.png");
		TextureManager.addTexture(10, "assets/tiles/square/top_left_corruption_7.png");
		TextureManager.addTexture(11, "assets/tiles/square/top_left_corruption_8.png");

		TextureManager.addTexture(12, "assets/tiles/square/top_right.png");
		TextureManager.addTexture(13, "assets/tiles/square/top_right_corruption_7.png");
		TextureManager.addTexture(14, "assets/tiles/square/top_right_corruption_8.png");

		TextureManager.addTexture(15, "assets/tiles/line/top.png");
		TextureManager.addTexture(16, "assets/tiles/line/top_corruption_7.png");
		TextureManager.addTexture(17, "assets/tiles/line/top_corruption_8.png");

		TextureManager.addTexture(18, "assets/tiles/line/center.png");
		TextureManager.addTexture(19, "assets/tiles/line/center_corruption_7.png");
		TextureManager.addTexture(20, "assets/tiles/line/center_corruption_8.png");

		TextureManager.addTexture(21, "assets/tiles/line/bottom.png");
		TextureManager.addTexture(22, "assets/tiles/line/bottom_corruption_7.png");
		TextureManager.addTexture(23, "assets/tiles/line/bottom_corruption_8.png");

		TextureManager.addTexture(24, "assets/tiles/special/grass.png");
		TextureManager.addTexture(25, "assets/tiles/special/grass_corruption_7.png");
		TextureManager.addTexture(26, "assets/tiles/special/grass_corruption_8.png");

		TextureManager.addTexture(27, "assets/tiles/special/creeper.png");
		TextureManager.addTexture(28, "assets/tiles/special/creeper_corruption_7.png");
		TextureManager.addTexture(29, "assets/tiles/special/creeper_corruption_8.png");

		TextureManager.addTexture(30, "assets/tiles/special/hi.png");
		TextureManager.addTexture(31, "assets/tiles/special/hi_corruption_7.png");
		TextureManager.addTexture(32, "assets/tiles/special/hi_corruption_8.png");

		TextureManager.addTexture(33, "assets/tiles/special/shield.png");
		TextureManager.addTexture(34, "assets/tiles/special/shield_corruption_7.png");
		TextureManager.addTexture(35, "assets/tiles/special/shield_corruption_8.png");

		// Load corruption tiles
		for (int i = 1; i <= 6; i++) {
			TextureManager.addTexture(40 + i, "assets/tiles/corruption/" + i + ".png");
		}

		TextureManager.addTexture(50, "assets/tiles/bottom/tile_bottom.png");
		for (int i = 1; i <= 8; i++) {
			TextureManager.addTexture(51 + i, "assets/tiles/bottom/bottom_corruption_" + i + ".png");
		}

		// Load player
		TextureManager.addTexture(200, "assets/player/idle.png");
		TextureManager.addTexture(201, "assets/player/right_dash_1.png");
		TextureManager.addTexture(202, "assets/player/right_dash_2.png");
		// Just setting opposite dash to its negative part because im lazy to do anything else :(
		TextureManager.addTexture(-201, "assets/player/left_dash_1.png");
		TextureManager.addTexture(-202, "assets/player/left_dash_2.png");

		// Energy Ball - Appear
		TextureManager.addTexture(300, "assets/traps/energy_ball/appear/1.png");
		TextureManager.addTexture(301, "assets/traps/energy_ball/appear/2.png");
		TextureManager.addTexture(302, "assets/traps/energy_ball/appear/3.png");
		TextureManager.addTexture(303, "assets/traps/energy_ball/appear/4.png");

		TextureManager.addTexture(310, "assets/traps/energy_ball/core/1.png");
		TextureManager.addTexture(311, "assets/traps/energy_ball/core/2.png");
		TextureManager.addTexture(312, "assets/traps/energy_ball/core/3.png");

		for (int i = 1; i <= 20; i++) {
			TextureManager.addTexture(320 + i - 1, "assets/traps/energy_ball/shell/" + i + ".png");
		}

		// Energy Ball - Laser
		TextureManager.addTexture(350, "assets/traps/energy_ball/laser/1.png");
		TextureManager.addTexture(351, "assets/traps/energy_ball/laser/2.png");

		TextureManager.addTexture(400, "assets/traps/energy_circle/1.png");
		TextureManager.addTexture(401, "assets/traps/energy_circle/2.png");
		TextureManager.addTexture(402, "assets/traps/energy_circle/3.png");
		TextureManager.addTexture(403, "assets/traps/energy_circle/4.png");
		TextureManager.addTexture(404, "assets/traps/energy_circle/5.png");
	}

	@Override
	public void create() {
		board = new Board(random);
		player = new Player(200, 0, 0);

		JFramework.scheduleRepeating(1f, 0f, task -> {
			board.generateRow();
		});

//		JFramework.createSceneTask(this, (ctx) -> {
//			board.updateCycle(ctx);
//			board.move();
//		}, 1);
	}

	@Override
	public void update(@NotNull UpdateContext ctx) {
		if (player.isDead()) {
			JFramework.setCurrentScene("none");
		}

		if (ctx.isKeyPressed(Keyboard.KEY_X)) {
			board.generateRow();
		}

		// Player Movement Handle
		int dirX = 0;
		int dirY = 0;
		if (ctx.isKeyPressed(Keyboard.KEY_W)) {
			dirY -= 1;
		}
		if (ctx.isKeyPressed(Keyboard.KEY_S)) {
			dirY += 1;
		}
		if (ctx.isKeyPressed(Keyboard.KEY_A)) {
			dirX -= 1;
		}
		if (ctx.isKeyPressed(Keyboard.KEY_D)) {
			dirX += 1;
		}
		player.handleMovement(board, dirX, dirY);

		board.update(ctx);
		player.update(ctx);
	}

	@Override
	public void render(@NotNull RenderContext ctx) {
		ctx.clear(JColor.RAYWHITE);
		board.render(ctx);
		player.render(ctx, board.getVerticalOffset());

		ctx.drawTextShadow("Difficulty: " + board.getDifficulty(), 25, 50, 30, JColor.WHITE, JColor.BLACK, 2f, 2f);
	}
}
