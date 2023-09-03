package kentington.diyplanets;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;

public class TerraformingModPlugin extends BaseModPlugin {
	
	public static boolean ReEvaluateMode = false;
	
	@Override
	public void onGameLoad(boolean newGame) {
		if (!BarEventManager.getInstance().hasEventCreator(TerraformBarEventCreator.class))
			BarEventManager.getInstance().addEventCreator(new TerraformBarEventCreator());
		if (!BarEventManager.getInstance().hasEventCreator(GenesisBarEventCreator.class))
			BarEventManager.getInstance().addEventCreator(new GenesisBarEventCreator());
        Global.getSector().registerPlugin(new TerraformingCampaignPlugin());
        if(Global.getSettings().getHullIdToVariantListMap().get("planetary_accretor").size()==0)
        	Global.getSettings().getHullIdToVariantListMap().get("planetary_accretor").add("planetary_accretor_Standard");
        if(Global.getSettings().getHullIdToVariantListMap().get("terraform_hypershunt").size()==0)
        	Global.getSettings().getHullIdToVariantListMap().get("terraform_hypershunt").add("terraform_hypershunt_Standard");

		try {
			JSONObject settings = Global.getSettings().getMergedJSONForMod("terraform_config.json", "diyplanets");
			ReEvaluateMode = settings.optBoolean("reEvaluateMode",false);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
