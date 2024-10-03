package goldenage.delfis.app.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.navbar.ConfigActivity;
import goldenage.delfis.app.activity.ErrorActivity;
import goldenage.delfis.app.activity.navbar.HomeActivity;
import goldenage.delfis.app.activity.navbar.LeaderboardActivity;
import goldenage.delfis.app.activity.navbar.StoreActivity;

public class ActivityUtil {
    public static Intent getNextIntent(Context context, MenuItem item) {
        Intent intent;

        if (item.getItemId() == R.id.lojafooter) {
            intent = new Intent(context, StoreActivity.class);
        } else if (item.getItemId() == R.id.homefooter) {
            intent = new Intent(context, HomeActivity.class);
        } else if (item.getItemId() == R.id.configfooter) {
            intent = new Intent(context, ConfigActivity.class);
        } else if (item.getItemId() == R.id.leaderfooter) {
            intent = new Intent(context, LeaderboardActivity.class);
        } else {
            intent = new Intent(context, ErrorActivity.class);
        }

        return intent;
    }
}
