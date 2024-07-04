package com.paint.hwpaint;

public enum LineWidth {
    OnePixel, ThreePixel, FivePixel, EightPixel;
    public int getSize(){
        switch (this){
            case OnePixel : return 1;
            case ThreePixel: return 3;
            case FivePixel: return 5;
            case EightPixel: return 8;
            default: return 1;
        }
    }
    public String toString(){
        switch (this){
            case OnePixel : return "1px";
            case ThreePixel: return "3px";
            case FivePixel: return "5px";
            case EightPixel: return "8px";
            default: return "";
        }
    }
}
