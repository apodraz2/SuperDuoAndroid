package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by adampodraza on 9/26/15.
 */
public class FootballWidgetIntentService extends IntentService {
    private final String LOG_TAG = getClass().getSimpleName();

    private static final String[] FOOTBALL_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL

    };

    private static final int HOME_COL = 3;
    private static final int AWAY_COL = 4;
    private static final int HOME_GOALS_COL = 6;
    private static final int AWAY_GOALS_COL = 7;



    public FootballWidgetIntentService() {
        super("FootballWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");

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

        String homeTeam = data.getString(HOME_COL);
        String awayTeam = data.getString(AWAY_COL);
        int homeGoals = data.getInt(HOME_GOALS_COL);
        int awayGoals = data.getInt(AWAY_GOALS_COL);

        data.close();

        for (int appId : appWidgetIds) {
            int layoutId = R.layout.football_scores_widget;

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setImageViewResource(R.id.widget_icon, Utilies.getTeamCrestByTeamName(homeTeam));
            views.setTextViewText(R.id.widget_home_team_name, homeTeam);
            views.setTextViewText(R.id.widget_home_team_score, Integer.toString(homeGoals));

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.simple_widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appId, views);
        }

    }
}
