package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Implementation of App Widget functionality.
 */
public class FootballScoresWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "FootballScoresWidgetProvider";

    private static final String [] SOCCER_COLUMNS = {
            DatabaseContract.ScoresTable.HOME_COL,
            DatabaseContract.ScoresTable.AWAY_COL,
            DatabaseContract.ScoresTable.HOME_GOALS_COL,
            DatabaseContract.ScoresTable.AWAY_GOALS_COL,
            DatabaseContract.ScoresTable.LEAGUE_COL
    };

    private static final int INDEX_HOME_NAME = 0;
    private static final int INDEX_AWAY_NAME = 1;
    private static final int INDEX_HOME_GOALS = 2;
    private static final int INDEX_AWAY_GOALS = 3;
    private static final int INDEX_LEAGUE = 4;

    private static String homeTeam;
    private static String awayTeam;
    private static int homeGoals;
    private static int awayGoals;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        //context.startService(new Intent(context, FootballWidgetIntentService.class));
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);


        //updateAppWidget(context, appWidgetManager, 0);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Log.d(LOG_TAG, "updateAppWidget");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Get the user's favorite league from shared preferences
        String favoriteLeague = sharedPreferences.getString(context.getString(R.string.favorite_league_key), context.getString(R.string.league_premier));


        Uri gamesUri = DatabaseContract.BASE_CONTENT_URI;

        Cursor data = context.getContentResolver().query(
                gamesUri,
                SOCCER_COLUMNS,
                null,
                null,
                null
        );

        if(data == null) {
            Log.d("FootballScoresWidget", "cursor was null");
        }

        if(!data.moveToFirst()) {
            Log.d("FootballScoresWidget", "cursor couldn't move to first");
        }

        //Give preference to the user's favorite league when displaying the match in the widget
        while(data.moveToNext()) {
            if(data.getString(INDEX_LEAGUE).equals(favoriteLeague)) {

                homeTeam = data.getString(INDEX_HOME_NAME);
                awayTeam = data.getString(INDEX_AWAY_NAME);

                homeGoals = data.getInt(INDEX_HOME_GOALS);
                awayGoals = data.getInt(INDEX_AWAY_GOALS);
            }
        }

        //if the cursor didn't contain data from the user's favorite league show data from the first match
        if(homeTeam == null && awayTeam == null) {
            data.moveToFirst();

            homeTeam = data.getString(INDEX_HOME_NAME);
            awayTeam = data.getString(INDEX_AWAY_NAME);

            homeGoals = data.getInt(INDEX_HOME_GOALS);
            awayGoals = data.getInt(INDEX_AWAY_GOALS);
        }



        data.close();


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.football_scores_widget_provider);
        views.setImageViewResource(R.id.widget_home_crest, Utilities.getTeamCrestByTeamName(homeTeam));
        views.setTextViewText(R.id.widget_home_name, homeTeam);
        views.setTextViewText(R.id.widget_score_textview, Utilities.getScores(homeGoals, awayGoals));
        views.setImageViewResource(R.id.widget_away_crest, Utilities.getTeamCrestByTeamName(awayTeam));
        views.setTextViewText(R.id.widget_away_name, awayTeam);

        // Create an Intent to launch MainActivity
        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

