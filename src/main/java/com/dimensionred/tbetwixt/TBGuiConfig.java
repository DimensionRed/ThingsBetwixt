package com.dimensionred.tbetwixt;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;


import java.util.Iterator;
import java.util.List;

public class TBGuiConfig extends GuiConfig {
    public TBGuiConfig(GuiScreen parentScreen) {
        super(parentScreen,
                new ConfigElement(ThingsBetwixt.config.getCategory(ThingsBetwixt.config.CATEGORY_GENERAL)).getChildElements(),
                ThingsBetwixt.MODID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ThingsBetwixt.config.toString()));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        // Save the configuration if there are changes
        if (ThingsBetwixt.config.hasChanged()) {

            ThingsBetwixt.config.save();

        }
    }

     private void cleanUpConfigSlots(int previousSlotsForTeleportation, int currentSlotsForTeleportation) {
        if (currentSlotsForTeleportation < previousSlotsForTeleportation) {
            for (int i = currentSlotsForTeleportation + 1; i <= previousSlotsForTeleportation; i++) {
                ThingsBetwixt.config.getCategory(Configuration.CATEGORY_GENERAL).remove(i + "_From");
                ThingsBetwixt.config.getCategory(Configuration.CATEGORY_GENERAL).remove(i + "_To");
                ThingsBetwixt.config.getCategory(Configuration.CATEGORY_GENERAL).remove(i + "_Y");


                System.out.println("[ThingsBetwixt] Removed config parameters: " + i + "_From, " + i + "_To, " + i + "_Y");
            }
        }
    }

    private void cleanEmptyCategories() {

        Iterator<String> categoryIterator = ThingsBetwixt.config.getCategoryNames().iterator();
        while (categoryIterator.hasNext()) {
            String categoryName = categoryIterator.next();
            if (ThingsBetwixt.config.getCategory(categoryName).isEmpty()) {
                categoryIterator.remove();
            }
        }
    }
}
