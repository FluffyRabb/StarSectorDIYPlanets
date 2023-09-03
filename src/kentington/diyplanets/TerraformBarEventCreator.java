package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;

public class TerraformBarEventCreator extends BaseBarEventCreator
{
	public PortsideBarEvent createBarEvent() {
		return new TerraformShipBarEvent();
	}

	public float getBarEventFrequencyWeight() {
		return 10f;
	}
}
