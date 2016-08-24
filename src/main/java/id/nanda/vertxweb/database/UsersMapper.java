package id.nanda.vertxweb.database;

import com.github.davidmoten.rx.jdbc.ResultSetMapper;
import id.nanda.vertxweb.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by nanda on 8/24/16.
 */
public class UsersMapper implements ResultSetMapper<User> {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    @Override
    public User call(ResultSet rs) throws SQLException {
        return new User(rs.getString(COLUMN_ID), rs.getString(COLUMN_NAME));
    }
}
