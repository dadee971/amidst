package amidst.gui.main.worldsurroundings.widget;

import java.awt.Point;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.gui.main.worldsurroundings.FragmentGraphToScreenTranslator;
import amidst.mojangapi.world.Biome;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.Resolution;

@NotThreadSafe
public class CursorInformationWidget extends TextWidget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;

	@CalledOnlyBy(AmidstThread.EDT)
	public CursorInformationWidget(CornerAnchorPoint anchor,
			FragmentGraph graph, FragmentGraphToScreenTranslator translator) {
		super(anchor);
		this.graph = graph;
		this.translator = translator;
		forceVisibility(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			CoordinatesInWorld coordinates = translator
					.screenToWorld(mousePosition);
			String biomeName = getBiomeNameAt(coordinates);
			return biomeName + " " + coordinates.toString();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getBiomeNameAt(CoordinatesInWorld coordinates) {
		Fragment fragment = graph.getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {
			long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
			long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
			short biome = fragment.getBiomeDataAt((int) x, (int) y);
			return Biome.getByIndex(biome).getName();
		} else {
			return UNKNOWN_BIOME_NAME;
		}
	}
}
