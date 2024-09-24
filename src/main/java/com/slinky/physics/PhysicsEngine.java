package com.slinky.physics;


/**
 * Just a sandbox at the moment.
 *
 * @author Kheagen Haskins
 */
public class PhysicsEngine {

    public static void main(String[] args) {
        System.out.println(makeHeading("Create Archetypes Method Tests"));
    }
    
    static String makeHeading(String str) {
        final int size = 80 - (str.length() + 10);
        StringBuilder outp = new StringBuilder();
        
        outp.append("// ").append("=".repeat(size / 2));
        outp.append(String.format("[ %s ]", str));
        outp.append("=".repeat((size / 2) + (size % 2))).append(" \\\\");
        
        return outp.toString();
    }
    
}