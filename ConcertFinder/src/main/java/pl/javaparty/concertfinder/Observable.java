package pl.javaparty.concertfinder;

/**
 * Created by jakub on 8/16/15.
 */
public interface Observable {

    void register(Observer o);
    void remove(Observer o);
    void notifyObservers();

}
