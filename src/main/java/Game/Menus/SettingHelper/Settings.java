package Game.Menus.SettingHelper;

import java.io.Serializable;

public class Settings implements Serializable{
    private boolean fullscreen = true;
    private int resX = 1366, resY = 768, renderResX = 683, renderResY = 384;

    public Settings() {
        this.resX = 1920;
        this.resY = 1080;
        this.fullscreen = false;
        this.renderResX = 1920;
        this.renderResY = 1080;
    }


    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public int getResX() {
        return resX;
    }

    public void setResX(int resX) {
        this.resX = resX;
    }

    public int getResY() {
        return resY;
    }

    public void setResY(int resY) {
        this.resY = resY;
    }

    public int getRenderResX() {
        return renderResX;
    }

    public void setRenderResX(int renderResX) {
        this.renderResX = renderResX;
    }

    public int getRenderResY() {
        return renderResY;
    }

    public void setRenderResY(int renderResY) {
        this.renderResY = renderResY;
    }
}
