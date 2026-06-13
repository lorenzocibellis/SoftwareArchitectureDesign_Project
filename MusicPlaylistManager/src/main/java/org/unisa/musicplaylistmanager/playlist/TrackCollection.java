package org.unisa.musicplaylistmanager.playlist;

import org.unisa.musicplaylistmanager.iterator.AbstractIterator;
import org.unisa.musicplaylistmanager.iterator.IterableCollection;
import org.unisa.musicplaylistmanager.iterator.Iterator;
import org.unisa.musicplaylistmanager.track.Track;
import org.unisa.musicplaylistmanager.track.TrackList;

import java.util.ArrayList;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public abstract class TrackCollection implements IterableCollection {
    private final StringProperty name = new SimpleStringProperty();
    private ArrayList<Track> tracks;

    /**
     * Costruisce una nuova playlist con il nome specificato.
     *
     * @param name il nome della playlist
     */
    public TrackCollection(String name) {
        this.name.set(name);
        this.tracks = new ArrayList<Track>();
    }


    /**
     * Restituisce la lista di tracce contenute nella playlist.
     *
     * @return un {@link ArrayList} di {@link Track}
     */
    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    /**
     * Restituisce il nome della playlist.
     *
     * @return il nome della playlist
     */
    public String getName() {
        return name.get();
    }

    /**
     * Imposta o modifica il nome della playlist.
     *
     * @param name il nuovo nome
     */
    protected void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Aggiunge una traccia alla playlist, se non è già presente.
     *
     * @param track la traccia da aggiungere
     * @throws IllegalArgumentException se la traccia è nulla o se è già presente
     */
    public void addTrack(Track track) {
        if(track!= null && !tracks.contains(track)) {
            tracks.add(track);
            return;
        }
        throw new IllegalArgumentException();
    }


    /**
     * Rimuove una traccia specifica dalla playlist.
     *
     * @param track la traccia da rimuovere
     */
    public void removeTrack(Track track) {
        tracks.remove(track);
    }


    public void removeAllTracks(ArrayList<Track> tracks){
        getTracks().removeAll(tracks);
    }
    /**
     * Restituisce l'indice di una traccia all'interno della playlist (non supportato al momento).
     *
     * @param track la traccia da cercare
     * @return l'indice della traccia
     * @throws UnsupportedOperationException sempre
     */
    public int getIndex(Track track){
        throw new UnsupportedOperationException("L'operazione getIndex non è attualmente supportata.");
    }

    /**
     * Restituisce il numero di tracce contenute nella playlist.
     *
     * @return la dimensione della playlist
     */
    public int getSize(){
        return tracks.size();
    }

    /**
     * Rimuove tutte le tracce dalla playlist.
     */
    public void deleteAll(){
        tracks.clear();
    }

    /**
     * Verifica l'uguaglianza tra questa playlist e un altro oggetto.
     * Due playlist sono considerate uguali se hanno lo stesso nome.
     *
     * @param o l'oggetto da confrontare
     * @return {@code true} se sono uguali, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if ((o == null) ||  (o.getClass() != this.getClass())) return false;
        Playlist p = (Playlist) o;
        return p.getName().equals(this.getName());
    }

    /**
     * Restituisce il codice hash della playlist basato sul nome.
     *
     * @return il codice hash
     */
    @Override
    public int hashCode(){
        return this.getName().hashCode();
    }

    /**
     * Restituisce la rappresentazione in stringa della playlist (il suo nome).
     *
     * @return il nome della playlist
     */
    @Override
    public String toString(){
        return this.getName();
    }


    /**
     * Sostituisce i dati di una traccia esistente con i dati di una nuova traccia,
     * mantenendo intatto il riferimento in memoria.
     * Se la modifica crea un duplicato nella playlist, l'operazione viene annullata.
     *
     * @param existingTrack la traccia esistente da modificare
     * @param newDataTrack un oggetto contenente i nuovi dati
     * @throws IllegalArgumentException se esiste già una traccia identica
     */
    public void updateTrack(Track existingTrack, Track newDataTrack) {

        // Controlliamo se i nuovi dati creerebbero un duplicato con un'altra traccia
        // (Nota: newDataTrack.equals() controlla titolo, autore e anno)
        for (Track t : getTracks()) {
            if (t != existingTrack && t.equals(newDataTrack)) {
                throw new IllegalArgumentException("Modifica annullata: esiste già una traccia identica.");
            }
        }

        // Se è tutto ok, NON sostituiamo l'oggetto, ma aggiorniamo i suoi campi
        // In questo modo, l'oggetto in memoria rimane lo stesso e gli Observer non si rompono.
        existingTrack.setTitle(newDataTrack.getTitle());
        existingTrack.setAuthor(newDataTrack.getAuthor());
        existingTrack.setYear(newDataTrack.getYear());
        existingTrack.setGenre(newDataTrack.getGenre());
        existingTrack.setDuration(newDataTrack.getDuration());
        existingTrack.setFavourite(newDataTrack.isFavourite());
        existingTrack.setExplicit(newDataTrack.isExplicitContent());
        existingTrack.setNewRelease(newDataTrack.isNewRelease());

        // permette di modificare la tracklist e la lista osservabile della tracklist quando effettuo una modifica ad
        // una traccia nella playlist
        if (TrackList.exists()) {
            TrackList trackList = TrackList.getTrackListPointer();
            if (trackList != this) { // Evita la chiamata ricorsiva se stiamo già aggiornando la TrackList
                trackList.updateTrack(existingTrack, newDataTrack);
            }
        }
    }

    public AbstractIterator createIterator(){
        return new Iterator(this);
    }


}
