package barqsoft.footballscores;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by adampodraza on 9/26/15.
 */
public class FootballWidgetIntentService extends IntentService {

    private static final String[] FOOTBALL_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL

    };

    private static final int HOME_COL = 3;


    public FootballWidgetIntentService() {
        super("FootballWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FootballScoresWidget.class));

        String favoriteTeam = Utilies.getPreferredTeam();

        Uri gameInfo = DatabaseContract.scores_table.buildScoreWithDate();

        Cursor data = getContentResolver().query(gameInfo,
                FOOTBALL_COLUMNS,
                null,
                null,
                DatabaseContract.scores_table.DATE_COL + " ASC");

        if(data == null) {
            return;
        }
         if(!data.moveToFirst()) {
             data.close();
             return;
         }

        String homeTeam;

    }
}
