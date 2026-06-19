package com.taiter.ce.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LocationHelper {

    private static String getPlayerDirection(Location loc) {
        double rotation = (loc.getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "W";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "W";
        } else {
            return null;
        }
    }

    public static List<Location> getLinePlayer(Player player, int length) {
        List<Location> list = new ArrayList<>();
        for (int amount = length; amount > 0; amount--) {
            list.add(player.getTargetBlock((Set<Material>) null, amount).getLocation());
        }
        return list;
    }

    public static List<Location> getCone(Location loc) {
        List<Location> locs = new ArrayList<>();
        String direction = getPlayerDirection(loc);
        if (direction == null)
            return locs;

        Location loc1 = loc.clone();
        Location loc2 = loc.clone();
        Location loc3 = loc.clone();
        if (direction.equals("N")) {
            loc1.setZ(loc.getZ() - 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() - 2);
            locs.add(loc2);
            loc3.setZ(loc.getZ() - 3);
            locs.add(loc3);
            Location loc4 = loc2.clone();
            Location loc5 = loc2.clone();
            Location loc6 = loc3.clone();
            Location loc7 = loc3.clone();
            Location loc8 = loc3.clone();
            Location loc9 = loc3.clone();
            loc4.setX(loc2.getX() - 1);
            locs.add(loc4);
            loc5.setX(loc2.getX() + 1);
            locs.add(loc5);
            loc6.setX(loc3.getX() + 2);
            locs.add(loc6);
            loc7.setX(loc3.getX() + 1);
            locs.add(loc7);
            loc8.setX(loc3.getX() - 1);
            locs.add(loc8);
            loc9.setX(loc3.getX() - 2);
            locs.add(loc9);
        } else if (direction.equals("S")) {
            loc1.setZ(loc.getZ() + 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() + 2);
            locs.add(loc2);
            loc3.setZ(loc.getZ() + 3);
            locs.add(loc3);
            Location loc4 = loc2.clone();
            Location loc5 = loc2.clone();
            Location loc6 = loc3.clone();
            Location loc7 = loc3.clone();
            Location loc8 = loc3.clone();
            Location loc9 = loc3.clone();
            loc4.setX(loc2.getX() - 1);
            locs.add(loc4);
            loc5.setX(loc2.getX() + 1);
            locs.add(loc5);
            loc6.setX(loc3.getX() + 2);
            locs.add(loc6);
            loc7.setX(loc3.getX() + 1);
            locs.add(loc7);
            loc8.setX(loc3.getX() - 1);
            locs.add(loc8);
            loc9.setX(loc3.getX() - 2);
            locs.add(loc9);
        } else if (direction.equals("E")) {
            loc1.setX(loc.getX() + 1);
            locs.add(loc1);
            loc2.setX(loc1.getX() + 1);
            locs.add(loc2);
            loc3.setX(loc2.getX() + 1);
            locs.add(loc3);
            Location loc4 = loc2.clone();
            Location loc5 = loc2.clone();
            Location loc6 = loc3.clone();
            Location loc7 = loc3.clone();
            Location loc8 = loc3.clone();
            Location loc9 = loc3.clone();
            loc4.setZ(loc2.getZ() - 1);
            locs.add(loc4);
            loc5.setZ(loc2.getZ() + 1);
            locs.add(loc5);
            loc6.setZ(loc3.getZ() + 2);
            locs.add(loc6);
            loc7.setZ(loc3.getZ() + 1);
            locs.add(loc7);
            loc8.setZ(loc3.getZ() - 1);
            locs.add(loc8);
            loc9.setZ(loc3.getZ() - 2);
            locs.add(loc9);
        } else if (direction.equals("W")) {
            loc1.setX(loc.getX() - 1);
            locs.add(loc1);
            loc2.setX(loc1.getX() - 1);
            locs.add(loc2);
            loc3.setX(loc2.getX() - 1);
            locs.add(loc3);
            Location loc4 = loc2.clone();
            Location loc5 = loc2.clone();
            Location loc6 = loc3.clone();
            Location loc7 = loc3.clone();
            Location loc8 = loc3.clone();
            Location loc9 = loc3.clone();
            loc4.setZ(loc2.getZ() - 1);
            locs.add(loc4);
            loc5.setZ(loc2.getZ() + 1);
            locs.add(loc5);
            loc6.setZ(loc3.getZ() + 2);
            locs.add(loc6);
            loc7.setZ(loc3.getZ() + 1);
            locs.add(loc7);
            loc8.setZ(loc3.getZ() - 1);
            locs.add(loc8);
            loc9.setZ(loc3.getZ() - 2);
            locs.add(loc9);
        } else if (direction.equals("NW")) {
            loc1.setZ(loc.getZ() - 1);
            loc1.setX(loc.getX() - 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() - 2);
            loc2.setX(loc.getX() - 2);
            locs.add(loc2);
            loc3 = loc1.clone();
            loc3.setZ(loc1.getZ() - 1);
            locs.add(loc3);
            Location loc4 = loc1.clone();
            Location loc5 = loc1.clone();
            Location loc6 = loc1.clone();
            loc4.setZ(loc1.getZ() - 2);
            locs.add(loc4);
            loc5.setX(loc1.getX() - 1);
            locs.add(loc5);
            loc6.setX(loc1.getX() - 2);
            locs.add(loc6);
        } else if (direction.equals("NE")) {
            loc1.setZ(loc.getZ() - 1);
            loc1.setX(loc.getX() + 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() - 2);
            loc2.setX(loc.getX() + 2);
            locs.add(loc2);
            loc3 = loc1.clone();
            loc3.setZ(loc1.getZ() - 1);
            locs.add(loc3);
            Location loc4 = loc1.clone();
            Location loc5 = loc1.clone();
            Location loc6 = loc1.clone();
            loc4.setZ(loc1.getZ() - 2);
            locs.add(loc4);
            loc5.setX(loc1.getX() + 1);
            locs.add(loc5);
            loc6.setX(loc1.getX() + 2);
            locs.add(loc6);
        } else if (direction.equals("SW")) {
            loc1.setZ(loc.getZ() + 1);
            loc1.setX(loc.getX() - 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() + 2);
            loc2.setX(loc.getX() - 2);
            locs.add(loc2);
            loc3 = loc1.clone();
            Location loc4 = loc1.clone();
            Location loc5 = loc1.clone();
            Location loc6 = loc1.clone();
            loc3.setZ(loc1.getZ() + 1);
            locs.add(loc3);
            loc4.setZ(loc1.getZ() + 2);
            locs.add(loc4);
            loc5.setX(loc1.getX() - 1);
            locs.add(loc5);
            loc6.setX(loc1.getX() - 2);
            locs.add(loc6);
        } else if (direction.equals("SE")) {
            loc1.setZ(loc.getZ() + 1);
            loc1.setX(loc.getX() + 1);
            locs.add(loc1);
            loc2.setZ(loc.getZ() + 2);
            loc2.setX(loc.getX() + 2);
            locs.add(loc2);
            loc3 = loc1.clone();
            loc3.setZ(loc1.getZ() + 1);
            locs.add(loc3);
            Location loc4 = loc1.clone();
            Location loc5 = loc1.clone();
            Location loc6 = loc1.clone();
            loc4.setZ(loc1.getZ() + 2);
            locs.add(loc4);
            loc5.setX(loc1.getX() + 1);
            locs.add(loc5);
            loc6.setX(loc1.getX() + 2);
            locs.add(loc6);
        }
        return locs;
    }
}
