package hu.unideb.inf.ticketservice.command.movie;

import hu.unideb.inf.ticketservice.command.impl.movie.DeleteMovieCommand;
import hu.unideb.inf.ticketservice.model.user.Administrator;
import hu.unideb.inf.ticketservice.model.user.DefaultUser;
import hu.unideb.inf.ticketservice.repository.MovieRepository;
import hu.unideb.inf.ticketservice.repository.ScreeningRepository;
import hu.unideb.inf.ticketservice.service.connection.impl.MovieRepositoryConnection;
import hu.unideb.inf.ticketservice.service.connection.impl.ScreeningRepositoryConnection;
import hu.unideb.inf.ticketservice.service.impl.AdminCredentialsProvider;
import hu.unideb.inf.ticketservice.service.connection.ConnectToMovieRepository;
import hu.unideb.inf.ticketservice.service.impl.LoggedInUserTrackImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class TestDeleteMovieCommand {

    private final List<String> PARAMETER_LIST = List.of("Movie");

    private DeleteMovieCommand underTest;
    private LoggedInUserTrackImpl userService;
    private AdminCredentialsProvider credentialsProvider;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ScreeningRepository screeningRepository;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        credentialsProvider = new AdminCredentialsProvider();
        userService = new LoggedInUserTrackImpl(new DefaultUser());
        underTest = new DeleteMovieCommand(userService, new MovieRepositoryConnection(movieRepository, screeningRepository));
    }

    @Test
    public void testExecuteShouldDeleteMovieWhenSignedIntoAPrivilegedAccountGivenTheRightMovieName()
    {
        //Given
        userService.updateCurrentUser(new Administrator(credentialsProvider));
        final String expected = "Alright";

        //When
        final String result = underTest.execute(PARAMETER_LIST);

        //Then
        Assertions.assertEquals(expected,result);
        Mockito.verify(movieRepository).deleteByName("Movie");
    }

    @Test
    public void testExecuteShouldNotDeleteMovieWhenSignedIntoANonPrivilegedAccount()
    {
        //Given
        final String expected = "Unauthorized action!";

        //When
        final String result = underTest.execute(PARAMETER_LIST);

        //Then
        Assertions.assertEquals(expected,result);
    }

}
