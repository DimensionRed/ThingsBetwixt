package com.dimensionred.tbetwixt;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.event.entity.living.LivingEvent;

@Mod(modid = ThingsBetwixt.MODID, version = ThingsBetwixt.VERSION, guiFactory = "com.dimensionred.tbetwixt.TBGuiFactory")
public class ThingsBetwixt {
    public static final String MODID = "tbetwixt";
    public static final String VERSION = "1.0";

    public static Configuration config;
    // Default teleportation IDs for main dimensions
    public static int teleportFromOverworldTo;
    public static int teleportFromNetherTo;
    public static int teleportFromEndTo;
    public static int basicY;
    public static boolean modState;
    public static boolean basicState;

    // Custom teleportation slots
    public static int[] customFromIds;
    public static int[] customToIds;
    public static int[] customYCoordinates;

    // Number of custom slots
    public static int slotsForTeleportation = 5;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();
        MinecraftForge.EVENT_BUS.register(this); // Register event handler
    }

    public static void loadConfig() {
        modState = config.getBoolean("ModActivation", Configuration.CATEGORY_GENERAL, true, StatCollector.translateToLocal("config.tbetwixt.activationState") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.activation" );
        basicState = config.getBoolean("BasicState", Configuration.CATEGORY_GENERAL, true, StatCollector.translateToLocal("config.tbetwixt.basicState") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.basic" );
        basicY = config.getInt("BasicY", Configuration.CATEGORY_GENERAL, 256, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.basicYCoordinate") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.basicY");
        teleportFromOverworldTo = config.getInt("TeleportFromOverworldTo", Configuration.CATEGORY_GENERAL, -1, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.teleportFromOverworldTo") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.fromOverworld");
        teleportFromNetherTo = config.getInt("TeleportFromNetherTo", Configuration.CATEGORY_GENERAL, 1, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.teleportFromNetherTo") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.fromNether");
        teleportFromEndTo = config.getInt("TeleportFromEndTo", Configuration.CATEGORY_GENERAL, 0, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.teleportFromEndTo") + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.fromEnd");

        // Load the number of slots for teleportation

        customFromIds = new int[slotsForTeleportation];
        customToIds = new int[slotsForTeleportation];
        customYCoordinates = new int[slotsForTeleportation];


        for (int i = 0; i < slotsForTeleportation; i++) {
            customFromIds[i] = config.getInt((i + 1) + "_From", Configuration.CATEGORY_GENERAL, 100, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.customFromId") + (i + 1) + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.fromID_" + (i + 1));
            customToIds[i] = config.getInt((i + 1) + "_To", Configuration.CATEGORY_GENERAL, 100, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.customToId") + (i + 1) + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.toID_" + (i + 1));
            customYCoordinates[i] = config.getInt((i + 1) + "_Y", Configuration.CATEGORY_GENERAL, 256, -9999, 9999, StatCollector.translateToLocal("config.tbetwixt.customYCoordinate") + (i + 1) + StatCollector.translateToLocal("config.tbetwixt.restart"), "config.tbetwixt.YCoordinate_" + (i + 1));
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer && modState) {
            EntityPlayer player = (EntityPlayer) event.entity;
            World world = player.worldObj;

            if (!world.isRemote && player.posY < -20) {
                int targetDimensionId = 100; //unreachable ID
                int targetYCoordinate = basicY; // Default Y-coordinate

                // Check if the player is in one of the main dimensions and set the corresponding target dimension ID
                if (basicState) {

                    if (world.provider.dimensionId == 0) {
                        targetDimensionId = teleportFromOverworldTo;
                    } else if (world.provider.dimensionId == -1) {
                        targetDimensionId = teleportFromNetherTo;
                    } else if (world.provider.dimensionId == 1) {
                        targetDimensionId = teleportFromEndTo;
                    }

                }

                // Check custom slots
                for (int i = 0; i < slotsForTeleportation; i++) {
                    if (world.provider.dimensionId == customFromIds[i]) {
                        targetDimensionId = customToIds[i];
                        targetYCoordinate = customYCoordinates[i];
                        break;
                    }
                }

                // If a valid target dimension is found, teleport the player
                if (targetDimensionId != 100 && DimensionManager.isDimensionRegistered(targetDimensionId)) {
                    if (player instanceof EntityPlayerMP) {
                        teleportPlayerToDimension((EntityPlayerMP) player, targetDimensionId, targetYCoordinate);
                    }
                }
            }
        }
    }

    private void teleportPlayerToDimension(EntityPlayerMP player, int dimensionId, int yCoordinate) {
        if (!player.worldObj.isRemote && !player.isDead) {
            MinecraftServer server = MinecraftServer.getServer();
            WorldServer targetWorld = server.worldServerForDimension(dimensionId);

            if (targetWorld != null) {
                // Transfer player to the target dimension

                if (player.dimension != dimensionId) {
                    server.getConfigurationManager().transferPlayerToDimension(player, dimensionId, new SimpleTeleporter(targetWorld));
                }

                // Set the player's Y position to the configured Y-coordinate in the target dimension
                int newPosX = (int) player.posX;
                int newPosZ = (int) player.posZ;
                player.setPositionAndUpdate(newPosX, yCoordinate, newPosZ);
            }
        }
    }

}
