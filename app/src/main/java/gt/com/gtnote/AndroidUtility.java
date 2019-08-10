package gt.com.gtnote;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

class AndroidUtility {
    
    public void makeTransparentStatusBar(Activity activity) {
        
        if (Build.VERSION.SDK_INT >= 21) {
            
            Window window = activity.getWindow();
            
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            
            fillStatusBarHeight(
                    activity.getResources(),
                    activity.getLayoutInflater(),
                    activity.findViewById(R.id.status_bar_space_view_group_view_mode),
                    activity.findViewById(R.id.status_bar_space_view_group_edit_mode)
            );
        }
    }
    
    private int getStatusBarHeight(Resources res) {
        int result = 0;
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    private void fillStatusBarHeight(Resources res, LayoutInflater layoutInflater, ViewGroup... viewGroups) {
        
        int statusBarHeight = getStatusBarHeight(res);
    
        for (ViewGroup viewGroup : viewGroups) {
            View view = layoutInflater.inflate(R.layout.status_bar_background_space, viewGroup, false);
            view.setMinimumHeight(statusBarHeight);
            viewGroup.addView(view, 0);
        }
    }
}
