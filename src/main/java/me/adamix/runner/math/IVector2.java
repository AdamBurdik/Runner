package me.adamix.runner.math;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class IVector2 implements Comparable<IVector2> {
	private int x;
	private int y;

	@Override
	public int compareTo(@NotNull IVector2 o) {
		return o.x == x && o.y == y ? 0 : 1;
	}
}
