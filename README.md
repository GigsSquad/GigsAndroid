ConcertFinder
=============

### O co chodzi
To co wiem na pewno to to że będziemy parsować stronę, [Przykład](http://www.go-ahead.pl/pl/koncerty.html), przez JSOUP i wyciągamy z niej na początek daty, nazwy i miejsca, wyświetlamy to na ekranie. Do tego wyszukiwarka z 3 opcjami: 
* po wykonawcy i dacie 
* miejscu (+promień) i dacie
* filtr dla konkretnej agencji np tylko z [GoAhead](http://www.go-ahead.pl/pl/)

### Supportowe biblioteki etc
###### Jako projekty 
Te trzeba zaimportować jako osobne projekty Project -> import -> Projekt androidowy 
i pamiętajcie o zaznaczaniu "Copy projects into workspace" 
* ~android-sdk\extras\android\support\v7\appcompat 
* ~android-sdk\extras\android\support\v7\cardview 
później właściwości projektu -> Android -> Add.. 
Jeśli niewidać projektu na liście wyboru to trzeba zaznaczyć na projekcie którego nie wiadć "Is library" we właściwościach

###### JAR
Właściwości projektu -> Java Build Path -> add external Jars... 
* ~android-sdk\extras\android\support\v7\appcompat\libs

### 3 podzialy
* Zabawa z parsowaniem stron przez JSOUP + ewentualnie SQLite 
* Zabawa z samym Androidem, okienka, przyciski, listy i inne
* Google Maps API

### Linki do parsowania 
* [Go Ahead](http://www.go-ahead.pl/pl/)
* [Presige MJM](http://www.imprezyprestige.com/)
* [Ebilet](http://www.ebilet.pl/)
* [Songkick](http://www.songkick.com/)
* [TicketPro](http://www.ticketpro.pl/jnp/home/index.html)
* [Eventim](http://www.eventim.pl/)
* [Live Nation](http://www.livenation.pl/)

### Android 
![Android lifecycle](http://developer.android.com/images/activity_lifecycle.png)

[Więcej o lifecycle activity](http://developer.android.com/reference/android/app/Activity.html)

### Fajne linki

[Instalacja pluginu do Eclipse](http://developer.android.com/…/installi…/installing-adt.html)

[Git w konsoli](http://git-scm.com/)

[Podstawy Gita](http://rogerdudler.github.io/git-guide/)

[Branche w GitHubie](http://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)

[Konwencje przy branchach](https://gist.github.com/digitaljhelms/4287848)

[Parsowanie stron JSOUP](http://jsoup.org/)

[SQLBrowser - do przeglądania baz danych](http://sqlitebrowser.org/)

[Google Maps API Android - krok po kroku](https://developers.google.com/maps/documentation/android/start#getting_the_google_maps_android_api_v2)

[Jak skonczymy](http://i.imgur.com/xgYL5Zc.gifv)
