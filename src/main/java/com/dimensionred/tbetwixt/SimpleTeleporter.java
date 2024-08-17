package com.dimensionred.tbetwixt;

import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class SimpleTeleporter extends Teleporter{

    private final WorldServer worldServerInstance;

    public SimpleTeleporter(WorldServer worldIn) {
        super(worldIn);
        this.worldServerInstance = worldIn;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        // Bypass portal creation; place the entity directly
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        return false; // Bypass the portal system
    }

    @Override
    public boolean makePortal(Entity entity) {
        return false; // Do not create a portal
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {
        // Don't use portals in this teleporter
    }

}
