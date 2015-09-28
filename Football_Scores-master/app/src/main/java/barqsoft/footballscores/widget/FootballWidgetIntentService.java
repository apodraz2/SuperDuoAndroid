package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by adampodraza on 9/27/15.
 */
public class FootballWidgetIntentService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public FootballWidgetIntentService() {
        super("FootballIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FootballScoresWidgetProvider.class));

        for(int appWidgetId : appWidgetIds) {

            CharSequence widgetText = "Manchester City FC";
            // Construct the RemoteViews object
            Log.d(LOG_TAG, getPackageName());
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.football_scores_widget_provider);
            views.setImageViewResource(R.id.widget_home_crest, R.drawable.manchester_city);
            views.setTextViewText(R.id.widget_home_name, widgetText);
            views.setTextViewText(R.id.widget_score_textview, "1 - 3");
            views.setImageViewResource(R.id.widget_away_crest, R.drawable.manchester_united);
            views.setTextViewText(R.id.widget_away_name, "Manchester United");

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}
