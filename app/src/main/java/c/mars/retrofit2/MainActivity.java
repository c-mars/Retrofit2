package c.mars.retrofit2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.okhttp.Callback;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com")
                .build();

        GitHubService service = retrofit.create(GitHubService.class);

        Call<ArrayList<Repo>> repos = service.listRepos("octocat");

//        base example
        repos.enqueue(new retrofit.Callback<ArrayList<Repo>>() {
            @Override
            public void onResponse(Response<ArrayList<Repo>> response, Retrofit retrofit) {
                Timber.d("success:\n");
                for(Repo repo:response.body()){
                    Timber.d(repo.full_name+": "+repo.pushed_at);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t.getMessage());
            }
        });

//        rx example
        service.listReposRx("c-mars")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .forEach(list -> Timber.d(list.toString()));
    }

    public static class Repo {
        public String full_name, pushed_at;
    }

    public interface GitHubService {
        @GET("/users/{user}/repos")
        Call<ArrayList<Repo>> listRepos(@Path("user") String user);

//        the same with rx
        @GET("/users/{user}/repos")
        rx.Observable<ArrayList<Repo>> listReposRx(@Path("user") String user);
    }
}
