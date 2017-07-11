package com.jonbott.knownspies.Helpers;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by j on 4/26/17.
 */

public class Helper {
    public static int resourceIdWith(Context context, String imageName) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(imageName, "drawable", context.getPackageName());
        return resourceId;
    }
}
