package kentington.diyplanets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;

import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

/**
 * Optional Ashes of the Domain: Vaults of Knowledge integration.
 *
 * DIY's aotd_tech_options.csv defines the research tree/rewards. This helper
 * maps each DIY industry reward to its research ID, then asks VOK directly
 * whether the player has completed that research before DIY applies its normal
 * planet/market availability rules.
 *
 * VOK remains optional at runtime. When VOK is not enabled, DIY preserves its
 * normal standalone building availability. This implementation intentionally
 * uses typed APIs only; Starsector runtime reflection is not used.
 */
public final class DIYPlanetsAotDResearch {
    private static final String DIY_MOD_ID = "diyplanets";
    private static final String VOK_MOD_ID = "aotd_vok";
    private static final String TECH_CSV = "data/campaign/aotd_tech_options.csv";

    private static final Map<String, String> INDUSTRY_RESEARCH_MAP = new HashMap<String, String>();
    private static final Map<String, String> RESEARCH_NAMES = new HashMap<String, String>();
    private static final Set<String> LOGGED_STATES = new HashSet<String>();

    private static boolean loadAttempted = false;
    private static boolean loadSucceeded = false;
    private static boolean dependencyStateLogged = false;
    private static boolean queryFailureLogged = false;

    private DIYPlanetsAotDResearch() {
    }

    public static boolean isBuildingResearchComplete(String industryId) {
        if (industryId == null || industryId.trim().length() == 0) {
            logInfo("DIY Planets AOTD research: availability check received a blank industry ID; failing open.");
            return true;
        }

        if (!isVokEnabled()) {
            return true;
        }

        ensureResearchMapLoaded();
        if (!loadSucceeded) {
            return true;
        }

        String normalizedIndustryId = industryId.trim().toLowerCase(Locale.ROOT);
        String requiredResearch = INDUSTRY_RESEARCH_MAP.get(normalizedIndustryId);
        if (requiredResearch == null) {
            return true;
        }

        Boolean researched = queryVokResearchState(requiredResearch);
        if (researched == null) {
            return true;
        }

        String stateKey = normalizedIndustryId + "=" + researched.toString();
        if (!LOGGED_STATES.contains(stateKey)) {
            LOGGED_STATES.add(stateKey);
            logInfo("DIY Planets AOTD research: industry " + normalizedIndustryId
                    + " requires " + requiredResearch + " ("
                    + getResearchDisplayName(requiredResearch) + "); researched="
                    + researched + ".");
        }

        return researched.booleanValue();
    }

    public static String getRequiredResearchForIndustry(String industryId) {
        if (industryId == null || industryId.trim().length() == 0) {
            return null;
        }
        if (isVokEnabled()) {
            ensureResearchMapLoaded();
        }
        return INDUSTRY_RESEARCH_MAP.get(industryId.trim().toLowerCase(Locale.ROOT));
    }

    private static boolean isVokEnabled() {
        boolean enabled;
        try {
            enabled = Global.getSettings().getModManager().isModEnabled(VOK_MOD_ID);
        } catch (Exception ex) {
            if (!dependencyStateLogged) {
                dependencyStateLogged = true;
                logInfo("DIY Planets AOTD research: dependency check failed; failing open. Cause: " + ex);
            }
            return false;
        }

        if (!dependencyStateLogged) {
            dependencyStateLogged = true;
            logInfo("DIY Planets AOTD research: dependency check; aotd_vok=" + enabled + ".");
        }
        return enabled;
    }

    private static synchronized void ensureResearchMapLoaded() {
        if (loadAttempted) {
            return;
        }
        loadAttempted = true;

        try {
            SettingsAPI settings = Global.getSettings();
            JSONArray researchData = settings.getMergedSpreadsheetDataForMod("id", TECH_CSV, DIY_MOD_ID);
            int diyResearchRows = 0;
            int mappingCount = 0;

            for (int i = 0; i < researchData.length(); i++) {
                JSONObject row = researchData.getJSONObject(i);
                String researchId = row.optString("id", "").trim();
                if (researchId.length() == 0) {
                    continue;
                }

                String provider = row.optString("modId", "").trim();
                if (!DIY_MOD_ID.equals(provider)) {
                    continue;
                }

                diyResearchRows++;
                String researchName = row.optString("name", "").trim();
                String rewardsRaw = row.optString("rewards", "");
                if (researchName.length() > 0) {
                    RESEARCH_NAMES.put(researchId, researchName);
                }

                String[] rewardEntries = rewardsRaw.split("[\\r\\n]+");
                for (int j = 0; j < rewardEntries.length; j++) {
                    String entry = rewardEntries[j].trim();
                    if (entry.length() == 0) {
                        continue;
                    }

                    int colonIndex = entry.lastIndexOf(':');
                    if (colonIndex <= 0) {
                        continue;
                    }

                    String rewardType = entry.substring(colonIndex + 1).trim();
                    if (!"industry".equals(rewardType)) {
                        continue;
                    }

                    String industryId = entry.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
                    if (industryId.length() == 0) {
                        continue;
                    }

                    INDUSTRY_RESEARCH_MAP.put(industryId, researchId);
                    mappingCount++;
                }
            }

            loadSucceeded = true;
            logInfo("DIY Planets AOTD research: loaded " + mappingCount
                    + " industry research mappings from " + diyResearchRows
                    + " DIY research rows.");
            if (mappingCount != 31) {
                logInfo("DIY Planets AOTD research: WARNING expected 31 industry mappings; found "
                        + mappingCount + ".");
            }
        } catch (IOException ex) {
            loadSucceeded = false;
            logInfo("DIY Planets AOTD research: failed to load research mappings; failing open. Cause: " + ex);
        } catch (JSONException ex) {
            loadSucceeded = false;
            logInfo("DIY Planets AOTD research: failed to parse research mappings; failing open. Cause: " + ex);
        } catch (RuntimeException ex) {
            loadSucceeded = false;
            logInfo("DIY Planets AOTD research: runtime failure while loading research mappings; failing open. Cause: " + ex);
        }
    }

    private static Boolean queryVokResearchState(String researchId) {
        try {
            AoTDMainResearchManager manager = AoTDMainResearchManager.getInstance();
            if (manager == null) {
                if (!queryFailureLogged) {
                    queryFailureLogged = true;
                    logInfo("DIY Planets AOTD research: VOK research manager returned null; failing open.");
                }
                return null;
            }
            return Boolean.valueOf(manager.isResearchedForPlayer(researchId));
        } catch (LinkageError ex) {
            if (!queryFailureLogged) {
                queryFailureLogged = true;
                logInfo("DIY Planets AOTD research: could not link VOK research API; failing open. Cause: " + ex);
            }
            return null;
        } catch (RuntimeException ex) {
            if (!queryFailureLogged) {
                queryFailureLogged = true;
                logInfo("DIY Planets AOTD research: failed to query VOK research state; failing open. Cause: " + ex);
            }
            return null;
        }
    }

    private static String getResearchDisplayName(String researchId) {
        String name = RESEARCH_NAMES.get(researchId);
        return name == null || name.length() == 0 ? researchId : name;
    }

    private static void logInfo(String message) {
        Global.getLogger(DIYPlanetsAotDResearch.class).info(message);
    }
}
