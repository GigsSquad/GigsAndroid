Zarys dzia�ania:

1. Klasy z JSoupem pobieraj� dane ze stron, wykonuj� ca�y parsing
(g��wnie chodzi tu o dat� (string->int[3]), co by�o wcze�niej domen�
Concertu). Po uporz�dkowaniu danych ze strony wrzucaj� wszystko
do bazy*. 

2. Zadanie ConcertManagera polega tylko na wyci�gni�ciu danych z bazy*,
przetworzenie ich na obiekty Concertu i wrzucenie do siebie do listy.
Jest to tymczasowe i IMO nieefektywne (lepiej pomijac kontenery),
ale nie musia�em grzeba� w MainActivity (penwie bym zjeba�).

3. MainActivity leci po staremu, ze wzgl�du na zachowanie modelu 
funkcji ConMgr'a (gettery).

*Baza obs�ugiwana jest przez dbManagera, kt�ry jest wykorzystywany 
przez ConcertManagera (konstruktor, przekazanie dma z poziomu MainAct)
i Jsoupy (one same generuj� nowego dbma). 
---------------------------------------------------------
ZMIANY
- Wywalony parser z Concertu
- Tymczasowo nieu�ywany AlterArt
- ConcertManager:
	* konstruktor (dbManager)
	* gettery u�ywaj� Cursora ("iterator" po bazie)
	* collect() - pobiera dane z bazy, tworzy Concerty
	  i wrzuca je do swojej listy
- JSoupy:
	* JSoupDownloader uruchamia wszystkie strony
	* Wszystko leci na dbMgr utworzonym w MainAcitvity
	* wywalone gettery, s�u�y tylko do komunikacji strona<->baza
	* dodany boolean() i obs�uga kontroli wersji strony
- MainActivity:
	* tworzymy dbManagera, kt�ry obs�u�y ConcertManager
------------------------------------------------------------------
Struktura bazy i dbManager:
BAZA:
2 tabele: Concerts [artist|city|spot|day|month|year|agency|url]
	  Hashcodes[agency|hash]

dbManager:
	* dodawanie koncertu
	* metody ogarniaj�ce hashcode
-------------------------------------------------------------------
Nie wiem czy patrzyli�cie wcze�niej na mojego brancha, ale
JSoupy wygl�daj� tak, �e ka�da strona ma w�asn� klas�, kt�rych getData()
[g��wna metoda] jest wywo�ywana w JSoupDownloader (g��wna klasa do pobierania)
-------------------------------------------------------------------
KONTROLA WERSJI!!!
W bazie mamy tabel� Hashcodes [AGENCY | HASH ]
Agency jest polem unikatowym (key value),
odpowiada za agencj� (stron�), z kt�ej pobieramy
Hash to hashcode pierwszego (najnowszego) eventu. Za ka�dym razem
jak chcemy robi� update najpierw pobieramy tylko jeden koncert, 
ustalamy jego hashcode, por�wnujemy z baz�, je�li dodali jakie� nowe
ecenty to hase si� r�ni� i lecimy z ca�ym dodawaniem do bazy oraz oczywi�cie
podmieniamy warto�� pola HASH w Hashcodes. 
--------------------------------------------------------------------
TODO:
W razie update'u poza nowymi koncertami dodadza si� r�wnie� te co by�y
Mo�na rozwi�za� sprawdzaniem hashu ka�dego koncertu a� do momentu
trafienia na ten w bazie.
	