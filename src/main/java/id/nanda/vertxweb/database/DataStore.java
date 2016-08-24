package id.nanda.vertxweb.database;

import id.nanda.vertxweb.model.User;
import id.nanda.vertxweb.model.UserSearchResult;
import rx.Observable;

import java.util.List;

/**
 * Created by nanda on 8/24/16.
 */
public interface DataStore {

    Observable<List<User>> getAllUsersAsStream();

    Observable<Integer> insertUser(String id, String name);

    Observable<List<UserSearchResult>> searchUserByName(String searchTerm, int limit, int offset);
}
