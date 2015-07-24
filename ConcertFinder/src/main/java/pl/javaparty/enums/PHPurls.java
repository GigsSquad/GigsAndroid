package pl.javaparty.enums;

/**
 *
 * Created by Jakub on 2015-03-13.
 */
public enum PHPurls {
    main("http://musicart.nazwa.pl/android/"),
    getConcerts(main + "test/get_concerts.php"),
    insertComment(main + "insert_comment.php"),
    getComments(main + "get_comments.php"),
    login(main + "login.php"),
    getLatLng(main + "get_location.php"),
    updateUser(main + "update_user.php");

    final String url;

    PHPurls(String url) {
        this.url = url;
    }

    public String toString() {
        return url;
    }


}
