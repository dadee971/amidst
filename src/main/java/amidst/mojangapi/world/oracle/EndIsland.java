package amidst.mojangapi.world.oracle;

import amidst.documentation.Immutable;

// TODO: check sign of differences ... (this.chunkX - chunkX) vs (chunkX - this.chunkX)
@Immutable
public class EndIsland {
	private static final int X_ADJUSTMENT = 1;
	private static final int Y_ADJUSTMENT = 1;

	private final int chunkX;
	private final int chunkY;
	private final float erosionFactor;

	protected EndIsland(int chunkX, int chunkY, float erosionFactor) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.erosionFactor = erosionFactor;
	}

	/**
	 * Retuns a value between 80 and -100 which indicates this island's
	 * influence at the block coordindates given. A non-negative value indicates
	 * there will be solid ground, while a negative value indicates the
	 * rocky-island-shore, which might be solid ground (but that becomes less
	 * likely the lower the value).
	 */
	public float influenceAtBlock(int x, int y) {
		float chunkX = x / 16.0f;
		float chunkY = y / 16.0f;
		float adjustedX = (this.chunkX - chunkX) * 2 + X_ADJUSTMENT;
		float adjustedY = (this.chunkY - chunkY) * 2 + Y_ADJUSTMENT;
		return getResult(adjustedX * adjustedX + adjustedY * adjustedY);
	}

	/**
	 * A version of influenceAt() that more exactly adheres to Minecraft's
	 * algorithm, for use in testing for End Cities.
	 */
	public float influenceAtChunk(int chunkX, int chunkY) {
		int adjustedX = (chunkX - this.chunkX) * 2 + X_ADJUSTMENT;
		int adjustedY = (chunkY - this.chunkY) * 2 + Y_ADJUSTMENT;
		return getResult(adjustedX * adjustedX + adjustedY * adjustedY);
	}

	private float getResult(double squared) {
		float result = 100.0f - (float) Math.sqrt(squared) * erosionFactor;
		if (result > 80.0f) {
			return 80.0f;
		} else if (result < -100.0f) {
			return -100.0f;
		} else {
			return result;
		}
	}
}