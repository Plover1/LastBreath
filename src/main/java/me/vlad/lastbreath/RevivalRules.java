package me.vlad.lastbreath;

/** Pure rules, shared by the server transaction and executable regression tests. */
public final class RevivalRules {
    private RevivalRules() {}
    public static double cost(double base, double extra, int revivals) {
        return base + extra * Math.max(0, revivals);
    }
    public static boolean canPay(float health, double cost) {
        return Double.isFinite(cost) && cost >= 0 && Float.isFinite(health) && health - cost >= 1.0;
    }
    public static float returnedHealth(double requested, float maximum) {
        return (float) Math.min(maximum, Math.max(1.0, requested));
    }
}
