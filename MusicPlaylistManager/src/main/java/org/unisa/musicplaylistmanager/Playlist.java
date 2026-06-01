package org.unisa.musicplaylistmanager;

/**
 * @author gruppo10
 */

import java.util.ArrayList;

public class Playlist {

    private String name;
    private ArrayList<Track> tracks;


    public Playlist(String name) {
        this.name = name;
        this.tracks = new ArrayList<Track>();
    }


        // metodi getter
    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    public String getName() {
        return name;
    }

    public Track getTrack() {
        throw new UnsupportedOperationException();
    }


    // metodi setter
    public void setName(String name) {
        this.name = name;
    }


    // aggiunge una traccia alla lista
    public void addTrack(Track track) {
        if(track!= null && !tracks.contains(track)) {
            tracks.add(track);
            return;
        }
        throw new IllegalArgumentException();
    }


    // rimuove una traccia dalla lista
    public void removeTrack(Track track) {
        tracks.remove(track);

    }
// Sostituisce i DATI di una traccia esistente, mantenendo intatto il riferimento in memoria
    public void updateTrack(Track existingTrack, Track newDataTrack) {
        
        // Controlliamo se i nuovi dati creerebbero un duplicato con un'altra traccia
        // (Nota: newDataTrack.equals() controlla titolo, autore e anno)
        for (Track t : tracks) {
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
        
        // Non serve fare tracks.set(...) perché la lista contiene già il riferimento a existingTrack, con i dati aggiornati
    }
    // restituisce l'indice di una traccia nella lista
    public int getIndex(Track track){
        throw new UnsupportedOperationException();
    }

    // restituisce la dimensione della lista
    public int getSize(){
        return tracks.size();
    }

    public void deleteAll(){
        tracks.clear();
    }

    public void undo(){
        throw new UnsupportedOperationException();
    }

    //metodi per uguaglianza playlist
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if ((o == null) ||  (o.getClass() != this.getClass())) return false;
        Playlist p = (Playlist) o;
        return p.getName().equals(this.getName());
    }

    @Override
    public int hashCode(){
        return this.getName().hashCode();
    }

    //metodo per stampa playlist
    @Override
    public String toString(){
        return this.getName();
    }
}
