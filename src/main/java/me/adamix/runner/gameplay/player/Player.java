package me.adamix.runner.gameplay.player;

import lombok.Getter;
import lombok.Setter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.color.JColor;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.jframework.task.Task;
import me.adamix.jframework.texture.JTexture;
import me.adamix.runner.gameplay.GameplayScene;
import me.adamix.runner.gameplay.board.Board;
import me.adamix.runner.gameplay.tile.Tile;
import me.adamix.runner.texture.TextureManager;

public class Player {
	private int textureId;
	private int tileX;
	private int tileY;
	@Setter
	@Getter
	private boolean dead = false;
	private int dashDirection = 0; // 0 = Not dashing, -1 = Left, 1 = right
	private int dashState = 0;
	private float dashOffset = 0f;
	private Task dashTask = null;

	public Player(int textureId, int tileX, int tileY) {
		this.textureId = textureId;
		this.tileX = tileX;
		this.tileY = tileY;
	}

	public void dash(int direction) {
		dashDirection = direction;
		if (dashTask != null) {
			dashTask.setExpired(true);
			dashTask = null;
		}
		JFramework.scheduleRepeating(0.2f, 0f, task -> {
			dashState++;
			if (dashState > 1) {
				dashState = 0;
				dashDirection = 0;
				task.setExpired(true);
			}
		});
		dashTask = JFramework.scheduleRepeating(0.01f, 0f, task -> {
			if (dashOffset != 0) {
				dashOffset += 4;
			}
		});
	}

	public void handleMovement(Board board, int dirX, int dirY) {
		Tile tile = board.getTile(tileX + dirX, tileY + dirY);
		if (tile == null || !tile.isWalkableOn()) {
			return;
		}

//		if (dirX != 0) {
//			dashOffset = tileX - dirX * Tile.WIDTH * TextureManager.TILE_SCALE;
//			dash(dirX);
//		}
		this.tileX += dirX;
		this.tileY += dirY;
		checkForDead();
	}

	public void update(UpdateContext ctx) {

	}

	public void render(RenderContext ctx, float verticalOffset) {
		int currentTextureId = textureId;
		if (dashDirection != 0) {
			currentTextureId = (currentTextureId + dashState + 1) * dashDirection;
		}

		JTexture texture = TextureManager.getTexture(currentTextureId);
		if (texture == null) {
			System.out.println(currentTextureId);
			return;
		}

		int width = Tile.WIDTH * TextureManager.TILE_SCALE;
		int height = Tile.HEIGHT * TextureManager.TILE_SCALE;

		float centerOffsetX = (JFramework.getScreenWidth() - width) / 2f;
		float centerOffsetY = (JFramework.getScreenHeight() - height) / 4f;

		float x = tileX * width + centerOffsetX + (width - texture.width() * TextureManager.TILE_SCALE) / 2f;
		float y = tileY * height + centerOffsetY + verticalOffset + (height - texture.height() * TextureManager.TILE_SCALE) / 2f;

		ctx.drawTextureEx(texture, x + (dashDirection != 0 ? dashOffset : 0), y, 0f, TextureManager.TILE_SCALE, JColor.WHITE);

		if (y > JFramework.getScreenHeight() + 70) {
			this.dead = true;
		}
	}

	public void checkForDead() {
		boolean shouldDie = GameplayScene.getBoard().shouldDie(tileX, tileY);
		this.dead = shouldDie || dead;
	}
}
