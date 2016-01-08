package amidst.fragment;

import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.layer.LayerLoader;
import amidst.gui.main.viewer.DimensionSelection;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@NotThreadSafe
public class FragmentManager {
	private final ConcurrentLinkedQueue<Fragment> availableQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> loadingQueue = new ConcurrentLinkedQueue<Fragment>();
	private final ConcurrentLinkedQueue<Fragment> recycleQueue = new ConcurrentLinkedQueue<Fragment>();
	private final FragmentCache cache;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentManager(Iterable<FragmentConstructor> constructors,
			int numberOfLayers) {
		this.cache = new FragmentCache(availableQueue, loadingQueue,
				constructors, numberOfLayers);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment requestFragment(CoordinatesInWorld coordinates) {
		Fragment fragment;
		while ((fragment = availableQueue.poll()) == null) {
			cache.increaseSize();
		}
		fragment.setCorner(coordinates);
		fragment.setInitialized();
		loadingQueue.offer(fragment);
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void recycleFragment(Fragment fragment) {
		recycleQueue.offer(fragment);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentQueueProcessor createQueueProcessor(LayerLoader layerLoader,
			DimensionSelection dimensionSelection) {
		return new FragmentQueueProcessor(availableQueue, loadingQueue,
				recycleQueue, cache, layerLoader, dimensionSelection);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getAvailableQueueSize() {
		return availableQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getLoadingQueueSize() {
		return loadingQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getRecycleQueueSize() {
		return recycleQueue.size();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getCacheSize() {
		return cache.size();
	}
}
