package mod.reverseengineering;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;

import mod.ReverseEngineeringPlugin;

public class ReverseEngineeringScript implements EveryFrameScript{
    public boolean firstTick = true;
    public int lastDayChecked = 0;
    private static Logger logger = Global.getLogger(ReverseEngineeringPlugin.class);

    public boolean isDone() {return false;}

    public boolean runWhilePaused() {return false;}

    // Check a day passed or not
    private boolean newDay() {
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstTick) {
            lastDayChecked = clock.getDay();
            firstTick = false;
            return false;
        } else if (clock.getDay() != lastDayChecked) {
            lastDayChecked = clock.getDay();
            return true;
        }
        return false;
    }

    public void advance(float var1){
        if (newDay()) {
            logger.log(Level.INFO, "NEW DAY");
            // Handle reverse engineering logic
            //onNewDay();
            //updateMarketTagTimePassed();
        }
    }
}
