package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Seller;
import softuni.exam.models.dto.SellerRootDto;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SellerServiceImpl implements SellerService {

    private static final String SELLERS_FILE_PATH = "src/main/resources/files/xml/sellers.xml";
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public SellerServiceImpl(SellerRepository sellerRepository, ModelMapper modelMapper
            , ValidationUtil validationUtil, XmlParser xmlParser) {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }


    @Override
    public boolean areImported() {
        return this.sellerRepository.count() > 0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(Path.of(SELLERS_FILE_PATH));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        SellerRootDto sellerRootDto = xmlParser.fromFile(SELLERS_FILE_PATH,SellerRootDto.class);

        sellerRootDto.getSellers().stream()
                .filter(dto-> {
                    boolean isValid = validationUtil.isValid(dto);
                    if (isValid) {
                        sb.append(String.format("Successfully import seller %s - @s",
                                dto.getFirstName(),
                                dto.getEmail()));
                    } else {
                        sb.append("Invalid seller");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> modelMapper.map(dto, Seller.class))
                .forEach(sellerRepository::save);


        return sb.toString();
    }

    @Override
    public Seller getSellerById(Long id) {
        return this.sellerRepository.findById(id).orElse(null);
    }
}
