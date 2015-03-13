package pl.javaparty.enums;

/**
 * Created by Jakub on 2015-03-13.
 */
public enum PHPurls {
    main("http://musicart.nazwa.pl/android/"),
    getConcerts(main + "getconcerts.php"),
    insertComment(main + "insert_comment.php"),
    getComments(main + "getcomments.php"),
    login(main + "login.php");

    final String url;

    PHPurls(String url) {
        this.url = url;
    }

    public String toString() {
        return url;
    }


}
