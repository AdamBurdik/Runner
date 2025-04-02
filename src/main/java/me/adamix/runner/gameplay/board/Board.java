package me.adamix.runner.gameplay.board;

import lombok.Getter;
import me.adamix.jframework.JFramework;
import me.adamix.jframework.context.RenderContext;
import me.adamix.jframework.context.UpdateContext;
import me.adamix.runner.gameplay.tile.Chunk;
import me.adamix.runner.gameplay.tile.Tile;
import me.adamix.runner.gameplay.traps.EnergyBall;
import me.adamix.runner.gameplay.traps.EnergyCircle;
import me.adamix.runner.math.IVector2;
import me.adamix.runner.texture.TextureManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Board {
	public static final int INITIAL_WIDTH = 3;
	public static final int INITIAL_HEIGHT = 5;
	public static final int MAX_WIDTH = 10;

	private final @NotNull Random random;
	@Getter
	private final @NotNull Map<Integer, Map<Integer, Tile>> rowMap= new ConcurrentHashMap<>(
			INITIAL_WIDTH * INITIAL_HEIGHT
	);

	@Getter
	private float verticalOffset = 0;

	private int currentWidth = INITIAL_WIDTH;
	private int activeWidth = currentWidth;
	@Getter
	private int currentRowIndex = 0;

	private final Queue<IVector2> tilesToRemove = new PriorityQueue<>();
	private Chunk processingChunk;
	private List<Chunk> chunkQueue = new LinkedList<>();

	private final Set<EnergyBall> energyBalls = new HashSet<>(MAX_WIDTH);
	private final Set<EnergyBall> energyBallsToRemove = new HashSet<>();

	private final @NotNull Map<Integer, Map<Integer, EnergyCircle>> energyCircles= new ConcurrentHashMap<>(
			INITIAL_WIDTH * INITIAL_HEIGHT
	);
	private final Queue<IVector2> energyCirclesToRemove = new PriorityQueue<>();

	@Getter
	private int difficulty = 1;

	private int generatedChunkCount = 0;

	public Board(@NotNull Random random) {

		this.random = random;
		this.processingChunk = new Chunk();
		processingChunk.generate(INITIAL_WIDTH, INITIAL_HEIGHT, random);

		Chunk initialChunk = new Chunk();
		initialChunk.generate(INITIAL_WIDTH, INITIAL_HEIGHT, random);

		// Generate initial board
		for (int y = 0; y < INITIAL_HEIGHT; y++) {
			Tile[] generatedRow = initialChunk.popRow();
			Map<Integer, Tile> row = new HashMap<>(INITIAL_WIDTH);

			int i = 0;
			for (int x = -(INITIAL_WIDTH - 1) / 2; x <= (INITIAL_WIDTH - 1) / 2; x++) {
				Tile tile = generatedRow[i];
				tile.setWalkableOn(true);
				row.put(x, tile);

				i++;
			}

			rowMap.put(INITIAL_HEIGHT - 1 - y, row);
		}
		generateQueue();
	}

	public @Nullable Tile getTile(int x, int y) {
		Map<Integer, Tile> row = rowMap.get(y);
		if (row == null) {
			return null;
		}
		return row.get(x);
	}

	private void generateQueue() {
		System.out.println("Generating");
		CompletableFuture.runAsync(() -> {
			for (int i = 0; i < 4; i++) {
				Chunk chunk = new Chunk();
				chunk.generate(currentWidth, INITIAL_HEIGHT, random);
				chunkQueue.add(chunk);
				if (currentWidth + 2 <= MAX_WIDTH) {
					currentWidth += 2;
				}
			}
		});
	}

	// Pops row from processing chunk and adds it to current tile map
	public void generateRow() {
		currentRowIndex--;

		if (processingChunk.isEmpty()) {
			difficulty = 1 + generatedChunkCount / 5;
			generatedChunkCount++;
			for (int i = 0; i < random.nextInt(0, currentWidth - 1); i++) {
				if (random.nextInt(0, 2) == 0) {
					generateEnergyBall();
				}
			}
//			for (int i = 0; i < random.nextInt(1, 2 + difficulty * 2); i++) {
//				generateEnergyCircle();
//			}
			processingChunk = chunkQueue.removeFirst();
			if (chunkQueue.isEmpty()) {
				generateQueue();
			}
		}
		Tile[] generatedRow = processingChunk.popRow();
		Map<Integer, Tile> row = new HashMap<>();
		activeWidth = generatedRow.length;

		int i = 0;
		for (int x = -(generatedRow.length - 1) / 2; x <= (generatedRow.length - 1) / 2; x++) {
			Tile tile = generatedRow[i];
			tile.startAppearAnimation(this.random.nextFloat(0f, 1.2f));
			row.put(x, tile);
			i++;
		}

		rowMap.put(currentRowIndex, row);
//		removeRandomRowTiles(row);
	}

	public void update(@NotNull UpdateContext ctx) {
		verticalOffset += ctx.getDeltaTime() * Tile.HEIGHT * TextureManager.TILE_SCALE;

		rowMap.forEach((y, row) -> {
			row.forEach((x, tile) -> {
				tile.update(ctx);
				if (tile.isRemove()) {
					tilesToRemove.add(new IVector2(x, y));
				}
			});
		});

		if (!tilesToRemove.isEmpty()) {
			for (IVector2 tileGrid : tilesToRemove) {
				rowMap.get(tileGrid.getY()).remove(tileGrid.getX());
			}
			tilesToRemove.clear();
		}

		if (!energyBallsToRemove.isEmpty()) {
			for (EnergyBall energyBall : energyBallsToRemove) {
				energyBalls.remove(energyBall);
			}
			energyBallsToRemove.clear();
		}
	}

	public void render(@NotNull RenderContext ctx) {
		for (int y = currentRowIndex; y < currentRowIndex + 10; y++) {
			var energyCircleRow = energyCircles.get(y);
			if (energyCircleRow != null) {
				for (int x = -(currentWidth - 1) / 2; x <= (currentWidth - 1) / 2; x++) {
					EnergyCircle circle = energyCircleRow.get(x);
					if (circle != null) {
						circle.render(ctx, x, y, verticalOffset);
					}
				}
			}

			var tileRow = rowMap.get(y);
			if (tileRow != null) {
				for (int x = -(currentWidth - 1) / 2; x <= (currentWidth - 1) / 2; x++) {
					var tile = tileRow.get(x);
					if (tile != null && !tile.isRemove()) {
						tile.render(ctx, x, y, verticalOffset);

						Map<Integer, Tile> rowUnderneath = rowMap.get(y + 1);
						if (rowUnderneath != null) {
							if (tile.getTextureId() == 360) {
								continue;
							}
							Tile tileUnderneath = rowUnderneath.get(x);
							if (tileUnderneath == null || tileUnderneath.isRemove() || tileUnderneath.isTransparent()) {
								tile.renderBottom(ctx, x, y, verticalOffset);
							}
						} else {
							tile.renderBottom(ctx, x, y, verticalOffset);
						}

					}
				}
			}
		}

		for (EnergyBall energyBall : energyBalls) {
			if (energyBall.isRemove()) {
				energyBallsToRemove.add(energyBall);
			} else {
				energyBall.render(ctx, verticalOffset);
			}
		}
	}

	public void generateEnergyBall() {
//		int activeWidth = rowMap.get(currentRowIndex).size();
		EnergyBall ball = new EnergyBall(
				random.nextInt(
						-(activeWidth - 1) / 2,
						(activeWidth - 1) / 2
				),
				random.nextInt(1, 4)
		);
		ball.startAppearAnimation();
		energyBalls.add(ball);
	}

	public void generateEnergyCircle() {
		int randomY = random.nextInt(currentRowIndex, currentRowIndex + rowMap.size());
		var row = rowMap.get(randomY);
		if (row != null) {
			int randomX = random.nextInt(
					-(row.size() - 1) / 2,
					(row.size() - 1) / 2
			);
			Tile tile = row.get(randomX);
			System.out.println("X: " + randomX + " Y: " + randomY);
			if (tile == null || tile.isRemove() || !tile.isWalkableOn()) {
				return;
			}

			var circle = new EnergyCircle();
			circle.startAppearAnimation();
			var circleRow = energyCircles.computeIfAbsent(randomY, k -> new HashMap<>());
			circleRow.put(randomX, circle);
		}
	}

	public void removeRandomRowTiles(Map<Integer, Tile> row) {
		// ToDo Fix this method
		List<Integer> already = new ArrayList<>();
		for (int i = 0; i < random.nextInt(0, Math.max((int) (2 + difficulty * 1.2f), row.size() - 2)); i++) {
			int randomX = random.nextInt(-(row.size() - 1) / 2, row.size() / 2);
			if (already.contains(randomX)) {
				continue;
			}

			already.add(randomX);

			Tile tile = row.get(randomX);
			if (tile == null) {
				continue;
			}

			JFramework.scheduleDelayed(random.nextFloat(1.3f, 3f), task -> {
				tile.startDisappearAnimation(0f);
			});
		}
	}

//	public void removeRandomTiles() {
//		for (int i = 0; i < random.nextInt(0, difficulty * 10); i++) {
//			int randomY = random.nextInt(currentRowIndex, currentRowIndex + rowMap.size() - 5);
//			var row = rowMap.get(randomY);
//			if (row == null) {
//				continue;
//			}
//			int randomX = random.nextInt(-(row.size() - 1) / 2, row.size() / 2);
//			var tile = row.get(randomX);
//			if (tile == null || !tile.isWalkableOn()) {
//				continue;
//			}
//
//			System.out.println("X: " + randomX + " Y: " + randomY);
//			tile.startDisappearAnimation(random.nextFloat(0, 1f));
//		}
//	}

	public boolean shouldDie(int tileX, int tileY) {
		var row = rowMap.get(tileY);
		if (row != null) {
			var tile = row.get(tileX);
			if (tile != null) {
				if (tile.isDeadly()) {
					return true;
				}
			}
		}

		// Check for energy ball
		for (EnergyBall energyBall : energyBalls) {
			if (energyBall.isRenderLaser() && energyBall.getTileX() == tileX) {
				return true;
			}
		}

		return false;
	}
}
