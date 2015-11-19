package c.mars.retrofit2;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text)
    TextView textView;
    //    display action requires to be called on ui thread
    Action1<ApiHelper.Repo> displayAction = repo -> textView.append(repo.full_name + "\n");
    //    save action supposed to be called in worker thread
    Action1<ApiHelper.Repo> saveAction = repo -> PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("repo", repo.full_name).commit();
    //    error action also requires ui thread just because user should receive some message
    Action1<Throwable> errorAction = throwable -> textView.setText(throwable.getMessage());
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);

//      remembering subsription is necessary to unsubscribe when activity is destroyed
        subscription = ApiHelper.getInstance().listRepos("c-mars", displayAction, saveAction, errorAction);
    }

    @Override
    protected void onDestroy() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
