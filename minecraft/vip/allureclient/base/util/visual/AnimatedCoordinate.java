package vip.allureclient.base.util.visual;

public class AnimatedCoordinate {

    private double x, y;

    public AnimatedCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void animateCoordinates(double newX, double newY, AnimationType animationType) {
        switch (animationType) {
            case LINEAR:
                this.x = AnimationUtil.linearAnimation(newX, x, 1.5F);
                this.y = AnimationUtil.linearAnimation(newY, y, 1.5F);
                break;
            case EASE_OUT:
                this.x = AnimationUtil.easeOutAnimation(newX, x, 0.05F);
                this.y = AnimationUtil.easeOutAnimation(newY, y, 0.05F);
                break;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public enum AnimationType {
        LINEAR,
        EASE_OUT
    }
}
