package techheromanish.example.com.silenceapp.helper;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Manish on 1/31/2017.
 */

public class FontManager {

   public   Context context;
    private static FontManager mInstance;
    public Typeface typefaceBold, typeFaceRegular;


    public FontManager(Context context){
        this.context = context;

        setUpfonts();
    }

    public static synchronized FontManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FontManager(context);
        }

        return mInstance;
    }

    public void setUpfonts(){
        typefaceBold = Typeface.createFromAsset(context.getAssets(),"fonts/Quicksand Bold.otf");
        typeFaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand Light.otf");
    }




}
