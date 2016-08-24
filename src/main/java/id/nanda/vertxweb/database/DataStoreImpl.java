package id.nanda.vertxweb.database;

import com.github.davidmoten.rx.jdbc.Database;
import id.nanda.vertxweb.model.User;
import id.nanda.vertxweb.model.UserSearchResult;
import rx.Observable;

import java.util.List;

/**
 * Created by nanda on 8/24/16.
 */
public class DataStoreImpl implements DataStore {

    private Database database;

    public DataStoreImpl(Database database) {
        this.database = database;
    }

    @Override
    public Observable<List<User>> getAllUsersAsStream() {
        return database
                .select("SELECT * FROM USERS")
                .get(new UsersMapper())
                .toList();
    }

    @Override
    public Observable<Integer> insertUser(String id, String name) {
        return database
                .update("INSERT INTO USERS(id, name) VALUES (?,?)")
                .parameters(id, name)
                .count();
    }

    @Override
    public Observable<List<UserSearchResult>> searchUserByName(String searchTerm, int limit, int offset) {
        return database
                .select(createSearchUserByNameQuery(searchTerm))
                .parameters(limit, offset)
                .get(new UserSearchResultMapper())
                .toList();
    }

    private String createSearchUserByNameQuery(String searchTerm) {
        String searchParam = "'%".concat(searchTerm).concat("%'");

        return new StringBuilder()
                .append("SELECT ")
                .append("(SELECT COUNT(id) from USERS WHERE name like ")
                .append(searchParam).append(") as ").append(UserSearchResultMapper.COLUMN_TOTAL)
                .append(", id, name FROM USERS WHERE name like ")
                .append(searchParam)
                .append(" ORDER by id LIMIT ? OFFSET ? ")
                .toString();
    }
}
