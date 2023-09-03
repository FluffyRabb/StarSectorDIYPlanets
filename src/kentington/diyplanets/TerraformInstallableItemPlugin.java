package kentington.diyplanets;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableIndustryItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class TerraformInstallableItemPlugin extends BaseInstallableIndustryItemPlugin {
	
	/**
	 * Very important for implementations of this to not store *any* references to campaign data in data members, since
	 * this is stored in a static map and persists between game loads etc.
	 */
	public static interface TerraformEffect {
		void apply(Industry industry);
		void unapply(Industry industry);
		void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode);
	}
	
	public static Map<String, TerraformEffect> ARTIFACT_EFFECTS = new HashMap<String, TerraformEffect>() {/**
		 * 
		 */
		private static final long serialVersionUID = -2036283358689187147L;

	{
		put("atmo_mineralizer", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows toxic or dense atmospheres to be completely terraformed.",
						pad, Misc.getHighlightColor());
			}
		});
		put("atmo_sublimator", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows creation of a breathable planetary atmosphere.",
						pad, Misc.getHighlightColor());
			}
		});
		put("solar_reflector", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent manipulation of a planet's light levels.",
						pad, Misc.getHighlightColor());
			}
		});
		put("tectonic_attenuator", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent removal of a planet's tectonic activity.",
						pad, Misc.getHighlightColor());
			}
		});
		put("weather_core", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent removal of a planet's extreme weather.",
						pad, Misc.getHighlightColor());
			}
		});
		put("climate_sculptor", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent addition of a mild climate to a Terran planet.",
						pad, Misc.getHighlightColor());
			}
		});
		put("gravity_oscillator", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent adjustment of a planet's gravitational field.",
						pad, Misc.getHighlightColor());
			}
		});
		put("rad_remover", new TerraformEffect() {
			public void apply(Industry industry) {
			}
			public void unapply(Industry industry) {
			}
			public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, InstallableItemDescriptionMode mode) {
				SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(data.getId());
				String name = Misc.ucFirst(spec.getName().toLowerCase());
				String pre = "";
				float pad = 0f;
				if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_LIST ||
						mode == InstallableItemDescriptionMode.INDUSTRY_TOOLTIP) {
					pre = name + ". ";
				} else if (mode == InstallableItemDescriptionMode.MANAGE_ITEM_DIALOG_INSTALLED || 
						mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP) {
					pre = name + " currently installed. ";
				}
				if (mode == InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP ||
						mode == InstallableItemDescriptionMode.CARGO_TOOLTIP) {
					pad = 10f;
				}
				text.addPara(pre + "Allows permanent removal of a planet's radioactivity.",
						pad, Misc.getHighlightColor());
			}
		});
	}};
	
	
	private TerraformIndustryNeedsArtifact industry;
	
	public TerraformInstallableItemPlugin(TerraformIndustryNeedsArtifact industry) {
		this.industry = industry;
	}
	
	public TerraformInstallableItemPlugin() {
		this.industry = null;
	}


	@Override
	public String getMenuItemTitle() {
		if (getCurrentlyInstalledItemData() == null) {
			return "Install terraforming core...";
		}
		return "Manage terraforming core...";
	}
	
	@Override
	public String getUninstallButtonText() {
		return "Uninstall terraforming core";
	}


	@Override
	public boolean isInstallableItem(CargoStackAPI stack) {
		if (!stack.isSpecialStack()) return false;
		
		return ARTIFACT_EFFECTS.containsKey(stack.getSpecialDataIfSpecial().getId()) && this.industry!=null && this.industry.artifactRequired.equals(stack.getSpecialItemSpecIfSpecial().getId());
	}
	
	
	@Override
	public SpecialItemData getCurrentlyInstalledItemData() {
		return industry.getArtifact();
	}
	
	@Override
	public void setCurrentlyInstalledItemData(SpecialItemData data) {
		industry.setArtifact(data);
	}
	
	@Override
	public String getNoItemCurrentlyInstalledText() {
		return "No terraforming core currently installed";
	}

	@Override
	public String getNoItemsAvailableText() {
		return "No terraforming core available";
	}
	
	@Override
	public String getNoItemsAvailableTextRemote() {
		return "No terraforming core available in storage";
	}

	@Override
	public String getSelectItemToAssignToIndustryText() {
		return "Select terraforming core to install for " + industry.getCurrentName();
	}

	@Override
	public void addItemDescription(TooltipMakerAPI text, SpecialItemData data, 
								   InstallableItemDescriptionMode mode) {
		TerraformEffect effect = ARTIFACT_EFFECTS.get(data.getId());
		if (effect != null) {
			effect.addItemDescription(text, data, mode);
		}
	}

	@Override
	public boolean isMenuItemTooltipExpandable() {
		return false;
	}

	@Override
	public float getMenuItemTooltipWidth() {
		return super.getMenuItemTooltipWidth();
	}
	
	@Override
	public boolean hasMenuItemTooltip() {
		return super.hasMenuItemTooltip();
	}

	@Override
	public void createMenuItemTooltip(TooltipMakerAPI tooltip, boolean expanded) {
		float opad = 10f;
		
		tooltip.addPara("An irreplaceable piece of Domain technology, " +
						"this terraforming core allows a planetary hazard to be entirely removed.", 0f);

		SpecialItemData data = industry.getArtifact();
		if (data == null) {
			tooltip.addPara(getNoItemCurrentlyInstalledText() + ".", opad);
		} else {
			TerraformEffect effect = ARTIFACT_EFFECTS.get(data.getId());
			effect.addItemDescription(tooltip, data, InstallableItemDescriptionMode.INDUSTRY_MENU_TOOLTIP);
		}
	}
	
	
}