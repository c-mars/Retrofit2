package c.mars.retrofit2;

import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Constantine Mars on 11/19/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ApiHelper {

    //    retrofit instance can be shared between different services/requests
    private static Retrofit retrofit;
    private static GitHubService gitHubService;

    //    singleton
    private ApiHelper() {
    }

    public static ApiHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void init() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("https://api.github.com")
                .build();
    }

    //    init retrofit service for future usage
    private void initGitHubService() {
        if (retrofit == null) {
            init();
        }

        gitHubService = retrofit.create(GitHubService.class);
    }

    //    let it be public, so we can call service methods from anywhere
    public GitHubService getGitHubService() {
        if (gitHubService == null) {
            initGitHubService();
        }
        return gitHubService;
    }

    //  all-in-one call example - we pass in actions, but all logic is incapsulated here
    public Subscription listRepos(String userName, Action1<Repo> displayAction, Action1<Repo> saveAction, Action1<Throwable> errorAction) {
        return getGitHubService().listReposRx(userName)
                .flatMap(Observable::from)
                .doOnNext(saveAction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        displayAction,
                        errorAction);
    }

    public interface GitHubService {
        @GET("/users/{user}/repos")
        rx.Observable<List<Repo>> listReposRx(@Path("user") String user);
    }

    private static class InstanceHolder {
        private static final ApiHelper INSTANCE = new ApiHelper();
    }

    public static class Repo {
        public String full_name;
    }
}
