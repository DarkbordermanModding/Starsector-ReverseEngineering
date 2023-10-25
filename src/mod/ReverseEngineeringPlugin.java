package mod;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ReverseEngineeringPlugin extends BaseModPlugin
{
    private static Logger logger = Global.getLogger(ReverseEngineeringPlugin.class);

    public void onGameLoad(boolean newGame) {
        logger.log(Level.INFO, "---Terraforming Platform, best platform");
        SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
        logger.log(Level.INFO, neturalPlatform);
        if(newGame){
            neturalPlatform.getMarket().addSubmarket("reverse_engineering");
        }
    }
}
