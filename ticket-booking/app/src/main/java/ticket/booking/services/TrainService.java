package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {
    private List<Train> trainList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAINS_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    public TrainService() throws IOException {
        loadTrains();
    }

    public List<Train> searchTrains(String src, String dest) {
        List<Train> trains = this.trainList;

        return trains.stream()
                .filter(train -> validTrainRoute(train, src, dest))
                .collect(Collectors.toList());
    }

    public static void printTrainDetails(List<Train> trains) {
        if (trains.isEmpty()) {
            System.out.println("\nNo trains found for the given route 😟!\n");
            return;
        }

        for (Train train: trains) {
            System.out.println("\n********** Train Details **********\n");
            System.out.println(train.getTrainInfo());

            for (Map.Entry<String, Time> entrySet: train.getStationTimes().entrySet()) {
                System.out.println(entrySet.getKey() + " - " + entrySet.getValue());
            }
            System.out.println("************************************");
        }
    }

    public void updateTrain(Train updatedTrain) throws IOException {
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveToTrainFile();
        }

        // add train (not going to execute now)
        trainList.add(updatedTrain);
    }

    private boolean validTrainRoute(Train train, String src, String dest) {
        List<String> stations = train.getStations();
        int srcIndex = stations.indexOf(src.toLowerCase());
        int destIndex = stations.indexOf(dest.toLowerCase());
        return srcIndex != -1 && destIndex != -1 && srcIndex < destIndex;
    }

    private void loadTrains() throws IOException {
        File file = new File(TRAINS_PATH);
        if (!file.exists()) {
            System.out.println("Not a single train is found in the system 😟");
            trainList = new ArrayList<>();
            return;
        }

        try {
            trainList = objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error occurred while fetching trains 😟 ==> " + e.getMessage());
            throw e;
        }
    }

    private void saveToTrainFile() throws IOException {
        File trainsFile = new File(TRAINS_PATH);
        objectMapper.writeValue(trainsFile, trainList);
    }
}
