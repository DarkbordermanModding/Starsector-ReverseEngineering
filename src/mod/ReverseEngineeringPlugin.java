package mod;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.Description.Type;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReverseEngineeringPlugin extends BaseModPlugin
{
    private static Logger logger = Global.getLogger(ReverseEngineeringPlugin.class);

    public void onGameLoad(boolean newGame) {
        // Terraforming Platform, best station
        SectorEntityToken neturalPlatform =  Global.getSector().getEntityById("corvus_abandoned_station");
        logger.log(Level.INFO, neturalPlatform);
//       .getMarket().addSubmarket(null);
    }
}
