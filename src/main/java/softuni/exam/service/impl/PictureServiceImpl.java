package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Picture;
import softuni.exam.models.dto.PictureSeedDto;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private static final String PICTURES_FILE_PATH = "src/main/resources/files/json/pictures.json";
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final CarService carService;

    public PictureServiceImpl(PictureRepository pictureRepository, Gson gson, ValidationUtil validationUtil
            , ModelMapper modelMapper, CarService carService) {
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.carService = carService;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files
                .readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        StringBuilder sb = new StringBuilder();
        PictureSeedDto[] pictureSeedDtos = gson.fromJson(readPicturesFromFile(),PictureSeedDto[].class);

        Arrays.stream(pictureSeedDtos)
                .filter(dto -> {
                    boolean isValid = validationUtil.isValid(dto);

                    if (isValid) {
                        sb.append(String.format("Successfully import picture - %s",
                                dto.getName()));
                    } else {
                        sb.append("Invalid picture");
                    }
                    sb.append(System.lineSeparator());
                    return isValid;
                })
                .map(dto -> {
                   Picture picture =  modelMapper.map(dto, Picture.class);
                   picture.setCar(this.carService.getCarById(dto.getCar()));
                   return picture;
                })
                .forEach(pictureRepository::save);

        return sb.toString();
    }
}
