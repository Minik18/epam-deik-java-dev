package hu.unideb.inf.ticketservice.command.impl.screening;

import hu.unideb.inf.ticketservice.command.Command;
import hu.unideb.inf.ticketservice.command.PrivilegedCommand;
import hu.unideb.inf.ticketservice.model.Movie;
import hu.unideb.inf.ticketservice.model.Room;
import hu.unideb.inf.ticketservice.model.Screening;
import hu.unideb.inf.ticketservice.service.DateValidationService;
import hu.unideb.inf.ticketservice.service.connection.ConnectToMovieRepository;
import hu.unideb.inf.ticketservice.service.connection.ConnectToRoomRepository;
import hu.unideb.inf.ticketservice.service.connection.ConnectToScreeningRepository;
import hu.unideb.inf.ticketservice.service.impl.LoggedInUserTrackImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateScreeningCommand implements Command, PrivilegedCommand {

    private final LoggedInUserTrackImpl userService;
    private final ConnectToScreeningRepository screeningRepository;
    private final ConnectToRoomRepository roomRepository;
    private final ConnectToMovieRepository movieRepository;
    private final DateValidationService dateValidationService;

    @Autowired
    public CreateScreeningCommand(LoggedInUserTrackImpl userService,
                                  ConnectToScreeningRepository screeningRepository,
                                  ConnectToRoomRepository roomRepository, ConnectToMovieRepository movieRepository,
                                  DateValidationService dateValidationService) {
        this.userService = userService;
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
        this.dateValidationService = dateValidationService;
    }

    @Override
    public String execute(List<String> parameters) {
        if (isAuthorized(userService)) {
            List<Movie> movies = movieRepository.listMovies();
            List<Room> rooms = roomRepository.listRooms();
            Movie actualMovie = getMovieByName(parameters.get(0),movies);
            Room actualRoom = getRoomByName(parameters.get(1),rooms);

            if (actualMovie != null) {
                if (actualRoom != null) {
                    String date = parameters.get(2);
                    if (dateValidationService.isDateValid(date)) {
                        Screening actualScreening = new Screening(actualMovie, actualRoom,date);
                        List<Screening> screenings = screeningRepository.listScreenings();
                        if (!dateValidationService.isDateOverlapping(date,screenings,actualRoom,
                                actualMovie.getNumberOfMinutes())) {
                            if (!dateValidationService.isDateInsideBreakTime(date,screenings,actualRoom,
                                    actualMovie.getNumberOfMinutes())) {
                                screeningRepository.createScreening(actualScreening);
                                return "Alright";
                            } else {
                                return "This would start in the break period after another screening in this room";
                            }
                        } else {
                            return "There is an overlapping screening";
                        }
                    } else {
                        return "That is not a valid date!";
                    }
                } else {
                    return "There is no room like " + parameters.get(1);
                }
            } else {
                return "There is no movie like " + parameters.get(0);
            }
        } else {
            return "Unauthorized action!";
        }
    }

    private Movie getMovieByName(String name, List<Movie> movies) {
        return movies.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private Room getRoomByName(String name, List<Room> rooms) {
        return rooms.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
