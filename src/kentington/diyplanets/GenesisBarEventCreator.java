package kentington.diyplanets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;

public class GenesisBarEventCreator extends BaseBarEventCreator
{
	public PortsideBarEvent createBarEvent() {
		return new GenesisBarEvent();
	}

	@Override
	public float getBarEventAcceptedTimeoutDuration() {
		return 10000000000f; // one-time-only
	}

	@Override
	public float getBarEventFrequencyWeight() {
		if (Global.getSector().getPlayerPerson().getStats().getLevel()<12 || Global.getSector().getMemoryWithoutUpdate().contains("$GenesisDeviceBarEvent")) {
			return 0f;
		}
		return 10f;
	}
}
