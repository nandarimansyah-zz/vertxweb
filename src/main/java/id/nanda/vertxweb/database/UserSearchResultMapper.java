package id.nanda.vertxweb.database;

import com.github.davidmoten.rx.jdbc.ResultSetMapper;
import id.nanda.vertxweb.model.UserSearchResult;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nanda on 8/24/16.
 */
public class UserSearchResultMapper implements ResultSetMapper<UserSearchResult> {

    public static final String COLUMN_TOTAL = "total";

    @Override
    public UserSearchResult call(ResultSet rs) throws SQLException {
        UserSearchResult userSearchResult = new UserSearchResult();
        userSearchResult.setId(rs.getString(UsersMapper.COLUMN_ID));
        userSearchResult.setName(rs.getString(UsersMapper.COLUMN_NAME));
        userSearchResult.setTotalSearchResult(rs.getInt(COLUMN_TOTAL));
        return userSearchResult;
    }
}
