package vip.allureclient.base.bind;

public interface BindableObject {
    int getBind();
    void onPressed();
    void unbind();
    void setBind(int bind);
}
