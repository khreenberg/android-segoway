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

import java.util.HashSet;

/**
 * Simple extension of ImageButton, that ignores touch events triggered on transparent parts
 * of the button. Probably requires the button uses a selector for it's images.
 */
public class ImageButtonIgnoreTransparency extends ImageButton {
    public static final int ALPHA_THRESHOLD = 5;

    public ImageButtonIgnoreTransparency(Context context) { super(context); }
    public ImageButtonIgnoreTransparency(Context context, AttributeSet attrs) { super(context, attrs); }
    public ImageButtonIgnoreTransparency(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    private HashSet<IButtonControlListener> _listeners;

    private boolean _isPressed = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_UP ) return notClicked();
        Bitmap bitmap = getBitmapFromStateListDrawable();
        if( bitmap == null ) return notClicked();
        int x = (int) event.getX(), y = (int) event.getY();
        int alpha = 0;
        try{
            alpha = getBitmapAlphaAtPoint(bitmap, x,y);
        }catch (IllegalArgumentException e){
            // This is thrown if the user clicks, holds and drags outside of the image bounds
            /* Do nothing - It's handled in the following return statement */
        }
        return alpha > ALPHA_THRESHOLD ? handleEvent(event) : notClicked();
    }

    private boolean handleEvent(MotionEvent event) {
        handleState(true);
        event.setAction(MotionEvent.ACTION_DOWN);
        return super.onTouchEvent(event);
    }

    private boolean notClicked() {
        handleState(false);
        setPressed(false);
        return false;
    }

    private void handleState(boolean shouldBePressed) {
        if( shouldBePressed == _isPressed ) return;
        _isPressed = !_isPressed;
        notifyListeners(_isPressed);
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


    public interface IButtonControlListener{
        void onButtonEvent(ImageButtonIgnoreTransparency button, boolean isPressed);
    }

    public void addListener(IButtonControlListener listener) {
        if( _listeners == null ) _listeners = new HashSet<IButtonControlListener>();
        _listeners.add(listener);
    }

    public void removeListener(IButtonControlListener listener) {
        if( _listeners == null ) return;
        _listeners.remove(listener);
        if (_listeners.isEmpty()) _listeners = null;
    }

    private void notifyListeners(boolean isPressed){
        if( _listeners == null ) return;
        for (IButtonControlListener listener : _listeners) listener.onButtonEvent(this, isPressed);
    }
}
