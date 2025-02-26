package hu.unideb.inf.ticketservice.service.connection;

import hu.unideb.inf.ticketservice.model.Movie;
import hu.unideb.inf.ticketservice.model.component.PriceComponent;

import java.util.List;

public interface ConnectToMovieRepository {
    List<Movie> listMovies();

    void createMovie(Movie movie);

    void updateMovie(String name, Movie movie);

    void deleteMovie(String name);

    void updateComponent(String name, PriceComponent component);

}
