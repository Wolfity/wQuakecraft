package me.wolf.wquakecraft.railgun;

import org.bukkit.Material;

public class RailGun implements Comparable<RailGun> {

    private final String name, identifier;
    private final Material material;
    private double fireRate;

    public RailGun(final String identifier, final String name, final Material material, final double fireRate) {
        this.identifier = identifier;
        this.name = name;
        this.fireRate = fireRate;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public double getFireRate() {
        return fireRate;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void decrementCooldown() {
        this.fireRate--;
    }

    @Override
    public int compareTo(RailGun o) {
        return o.getIdentifier().compareTo(identifier);
    }
}
