package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class RadRemover4 extends TerraformIndustryNeedsArtifact {
	
	public RadRemover4()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="rare_ore_abundant";
		tag="RadRemover";
		artifactRequired="rad_remover";
		requiresTerran = true;
		reEvaluateMining=true;
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}
