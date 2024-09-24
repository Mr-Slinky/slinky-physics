package com.slinky.physics.entities;

import com.slinky.physics.components.Component;
import static com.slinky.physics.components.Component.*;

/**
 *
 * @author Kheagen Haskins
 */
public enum Archetype {

    POINT_MASS(POSITION, VELOCITY, FORCE, MASS, RESTITUTION);

    private Archetype(Component... components) {
        this.components = components;
        this.bitmask = 0;
        for (Component component : components) {
            bitmask |= component.bit();
        }
    }

    private int bitmask;
    private Component[] components;

    public int bitmask() {
        return bitmask;
    }

    public Component[] components() {
        return components;
    }

    public boolean contains(int bitset) {
        return (bitset & bitmask) == bitmask;
    }

}
