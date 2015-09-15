package database;

/**
 * Created by chan on 15-9-6.
 */
public class UserContentResolver extends ContentResolver{
    @Override
    protected String getTables() {
        return "USER";
    }
}
