package softuni.exam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javassist.expr.NewArray;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import softuni.exam.models.Car;
import softuni.exam.models.dto.CarSeedDto;
import softuni.exam.util.ValidationUtil;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Configuration
public class ApplicationBeanConfiguration {



    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }


    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();


        modelMapper.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> mappingContext) {
                return LocalDate
                        .parse(mappingContext.getSource(),
                                DateTimeFormatter.ofPattern(("dd/MM/yyyy")));
            }
        });

        modelMapper.addConverter(new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(MappingContext<String, LocalDateTime> mappingContext) {
                return LocalDateTime
                        .parse(mappingContext.getSource(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        });

//        modelMapper.typeMap(CarSeedDto.class,
//                Car.class)
//                .addMappings(mapper -> mapper.map(CarSeedDto::getRegisteredOn,
//                        Car::setRegisteredOn));
        
        return modelMapper;
    }

}
