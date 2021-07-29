package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Car;
import softuni.exam.models.dto.CarSeedDto;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private static final String CARS_FILE_PATH = "src/main/resources/files/json/cars.json";
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public CarServiceImpl(CarRepository carRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files
                .readString(Path.of(CARS_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException {
        StringBuilder  sb = new StringBuilder();

        CarSeedDto[] carSeedDtos = gson.fromJson(readCarsFileContent(),CarSeedDto[].class);

        Arrays.stream(carSeedDtos)
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto);
                    if (isValid) {
                        sb.append(String.format("Successfully imported car - %s - %s",
                                dto.getMake(),dto.getModel()));

                    } else {
                        sb.append("Invalid car");
                    }

                    sb.append(System.lineSeparator());

                     return isValid;
                })
                .map(dto -> modelMapper.map(dto, Car.class))
                .forEach(carRepository::save);

        return sb.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {

        StringBuilder sb = new StringBuilder();

        this.carRepository.findAllCarsOrderByPicturesThenByMake()
                .forEach(c -> {
                    sb.append(String.format("Car make - %s, model - %s\n" +
                            "\tKilometers - %d\n" +
                            "\tRegistered on - %s\n" +
                            "\tNumber of pictures - %d",
                            c.getMake()
                            ,c.getModel()
                    ,c.getKilometers(), c.getRegisteredOn() ,c.getPictures().size()))
                    .append(System.lineSeparator());
                });

        return sb.toString();
    }

    @Override
    public Car getCarById(Long carId) {
        return this.carRepository.findById(carId).orElse(null);
    }


}
