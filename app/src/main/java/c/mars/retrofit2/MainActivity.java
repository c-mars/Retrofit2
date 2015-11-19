package c.mars.retrofit2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.squareup.okhttp.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("https://api.github.com")
                .build();

        GitHubService service = retrofit.create(GitHubService.class);

////        base example
//        Call<ArrayList<Repo>> repos = service.listRepos("octocat");
//
//        repos.enqueue(new retrofit.Callback<ArrayList<Repo>>() {
//            @Override
//            public void onResponse(Response<ArrayList<Repo>> response, Retrofit retrofit) {
//                Timber.d("success:\n");
//                for (Repo repo : response.body()) {
//                    Timber.d(repo.full_name + ": " + repo.pushed_at);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Timber.e(t.getMessage());
//            }
//        });

//        rx example
        service.listReposRx("c-mars")
                .flatMap(list -> Observable.from(list))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repo -> textView.append(repo.full_name + "\n"), throwable -> textView.setText(throwable.getMessage()));
    }

    public static class Repo {
        public String full_name, pushed_at;
    }

    public interface GitHubService {
        @GET("/users/{user}/repos")
        Call<ArrayList<Repo>> listRepos(@Path("user") String user);

//        the same with rx
        @GET("/users/{user}/repos")
        rx.Observable<List<Repo>> listReposRx(@Path("user") String user);
    }
}
