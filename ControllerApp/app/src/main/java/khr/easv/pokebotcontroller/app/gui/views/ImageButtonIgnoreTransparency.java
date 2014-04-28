package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Simple extension of ImageButton, that ignores touch events triggered on transparent parts
 * of the button. Probably requires the button uses a selector for it's images.
 */
public class ImageButtonIgnoreTransparency extends ImageButton {
    public static final int ALPHA_THRESHOLD = 5;

    public ImageButtonIgnoreTransparency(Context context) { super(context); }
    public ImageButtonIgnoreTransparency(Context context, AttributeSet attrs) { super(context, attrs); }
    public ImageButtonIgnoreTransparency(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Bitmap bitmap = getBitmapFromStateListDrawable();
        if( bitmap == null ){ setPressed(false); return false; }
        int x = (int) event.getX(), y = (int) event.getY();
        int alpha = 0;
        try{
            alpha = getBitmapAlphaAtPoint(bitmap, x,y);
        }catch (IllegalArgumentException e){
            // This is thrown if the user clicks, holds and drags outside of the image bounds
            /* Do nothing - It's handled after the next if-statement */
        }
        if( alpha > ALPHA_THRESHOLD) return super.onTouchEvent(event);
        setPressed(false);
        return false;
    }

    private int getBitmapAlphaAtPoint(Bitmap bitmap, int x, int y){
        int color = bitmap.getPixel(x,y);
        return Color.alpha(color);
    }

    private Bitmap getBitmapFromStateListDrawable(){
        DrawableContainerState state = (DrawableContainerState) getDrawable().getConstantState();
        for(Drawable drawable : state.getChildren() ){
            if( drawable instanceof BitmapDrawable ){
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return null;
    }
}
