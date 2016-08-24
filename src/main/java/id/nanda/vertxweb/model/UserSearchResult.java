package id.nanda.vertxweb.model;

/**
 * Created by nanda on 8/24/16.
 */
public class UserSearchResult extends User {

    private int totalSearchResult;

    public int getTotalSearchResult() {
        return totalSearchResult;
    }

    public void setTotalSearchResult(int totalSearchResult) {
        this.totalSearchResult = totalSearchResult;
    }
}
