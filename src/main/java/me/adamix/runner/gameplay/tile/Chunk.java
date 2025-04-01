package me.adamix.runner.gameplay.tile;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Chunk {
	private final @NotNull List<Tile[]> rowList = new LinkedList<>();

	public @NotNull Tile[] popRow() {
		return rowList.removeFirst();
	}

	public boolean isEmpty() {
		return rowList.isEmpty();
	}

	private boolean generateSquare(int x, int y) {
		if (y + 1 >= rowList.size() || x + 1 >= rowList.getFirst().length) {
			return false;
		}

		Tile tile = rowList.get(y)[x];
		if (tile.getTextureId() != 0) {
			return false;
		}

		Tile rightTile = rowList.get(y)[x + 1];
		if (rightTile == null || rightTile.getTextureId() != 0) {
			return false;
		}

		Tile topTile = rowList.get(y + 1)[x];
		if (topTile == null || topTile.getTextureId() != 0) {
			return false;
		}

		Tile topRightTile = rowList.get(y + 1)[x + 1];
		if (topRightTile == null || topRightTile.getTextureId() != 0) {
			return false;
		}

		tile.setTextureId(3);
		rightTile.setTextureId(6);
		topTile.setTextureId(9);
		topRightTile.setTextureId(12);
		return true;
	}

	private boolean generateLine(int x, int y) {
		if (y + 2 >= rowList.size()) {
			return false;
		}

		Tile tile = rowList.get(y)[x];
		if (tile == null || tile.getTextureId() != 0) {
			return false;
		}

		Tile centerTile = rowList.get(y + 1)[x];
		if (centerTile == null || centerTile.getTextureId() != 0) {
			return false;
		}

		Tile topTile = rowList.get(y + 2)[x];
		if (topTile == null || topTile.getTextureId() != 0) {
			return false;
		}

		tile.setTextureId(21);
		centerTile.setTextureId(18);
		topTile.setTextureId(15);
		return true;
	}

	public boolean generateSpecial(int x, int y, int id) {
		Tile tile = rowList.get(y)[x];
		if (tile == null || tile.getTextureId() != 0) {
			return false;
		}

		tile.setTextureId(24 + id * 3);
		return true;
	}

	public void generate(final int width, final int height, Random random) {
		for (int y = 0; y < height; y++) {
			Tile[] row = new Tile[width];

			for (int x = 0; x < width; x++) {
				row[x] = new Tile(0);
			}

			rowList.add(row);
		}

		int maxSquareCount = ((width * height) / 10);

		int i = 0;
		int currentSquareCount = 0;
		while (i < 100 && currentSquareCount <= maxSquareCount) {
			boolean success = generateSquare(random.nextInt(0, width), random.nextInt(0, height));
			if (success) {
				currentSquareCount += 1;
			}
			i++;
		}

		int maxLineCount = ((width * height) / 20);

		i = 0;
		int currentLineCount = 0;
		while (i < 100 && currentLineCount <= maxLineCount) {
			boolean success = generateLine(random.nextInt(0, width), random.nextInt(0, height));
			if (success) {
				currentLineCount += 1;
			}
			i++;
		}

		int maxSpecialCount = ((width * height) / 45);

		i = 0;
		int currentSpecialCount = 0;
		while (i < 100 && currentSpecialCount <= maxSpecialCount) {
			boolean success = generateSpecial(random.nextInt(0, width), random.nextInt(0, height), random.nextInt(0, 4));
			if (success) {
				currentSpecialCount += 1;
			}
			i++;
		}
	}
}
