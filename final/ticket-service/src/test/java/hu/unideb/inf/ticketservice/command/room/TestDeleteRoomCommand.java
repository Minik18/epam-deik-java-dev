package hu.unideb.inf.ticketservice.command.room;

import hu.unideb.inf.ticketservice.command.impl.room.DeleteRoomCommand;
import hu.unideb.inf.ticketservice.model.user.Administrator;
import hu.unideb.inf.ticketservice.model.user.DefaultUser;
import hu.unideb.inf.ticketservice.repository.RoomRepository;
import hu.unideb.inf.ticketservice.repository.ScreeningRepository;
import hu.unideb.inf.ticketservice.service.connection.impl.RoomRepositoryConnection;
import hu.unideb.inf.ticketservice.service.impl.AdminCredentialsProvider;
import hu.unideb.inf.ticketservice.service.connection.ConnectToRoomRepository;
import hu.unideb.inf.ticketservice.service.impl.LoggedInUserTrackImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class TestDeleteRoomCommand {

    private final List<String> PARAMETER_LIST = List.of("Room");

    private AdminCredentialsProvider credentialsProvider;
    private DeleteRoomCommand underTest;
    private LoggedInUserTrackImpl userService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ScreeningRepository screeningRepository;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        credentialsProvider = new AdminCredentialsProvider();
        userService = new LoggedInUserTrackImpl(new DefaultUser());
        underTest = new DeleteRoomCommand(userService, new RoomRepositoryConnection(roomRepository,screeningRepository));
    }

    @Test
    public void testExecuteShouldDeleteRoomWhenSignedIntoAPrivilegedAccountGivenTheRightRoomName()
    {
        //Given
        userService.updateCurrentUser(new Administrator(credentialsProvider));
        final String expected = "Alright";

        //When
        final String result = underTest.execute(PARAMETER_LIST);

        //Then
        Assertions.assertEquals(expected,result);
        Mockito.verify(roomRepository).deleteByName("Room");
    }

    @Test
    public void testExecuteShouldNotDeleteRoomWhenSignedIntoANonPrivilegedAccount()
    {
        //Given
        final String expected = "Unauthorized action!";

        //When
        final String result = underTest.execute(PARAMETER_LIST);

        //Then
        Assertions.assertEquals(expected,result);
    }

}
