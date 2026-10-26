package me.vlad.lastbreath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;

/** Shared wood and brass controls, retaining vanilla focus, narration and input handling. */
public final class SoulButton extends Button {
    public SoulButton(Button.Builder builder){super(builder);}
    @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
        int x=getX(),y=getY(),w=getWidth(),h=getHeight();
        boolean focus=active&&isHoveredOrFocused();
        g.fill(x,y,x+w,y+h,active?(focus?0xffefd08a:0xffa88748):0xff66543b);
        g.fill(x+1,y+1,x+w-1,y+h-1,focus?0xff513921:0xff30231b);
        g.fill(x+2,y+2,x+w-2,y+3,0xff735332);
        g.fill(x+2,y+h-3,x+w-2,y+h-2,0xff1c1612);
        g.fill(x+3,y+h/2,x+4,y+h/2+1,0xffcaa25b);
        g.fill(x+w-4,y+h/2,x+w-3,y+h/2+1,0xffcaa25b);
        g.centeredText(Minecraft.getInstance().font,getMessage(),x+w/2,y+(h-8)/2,active?0xffefdcab:0xff95856f);
    }
}
