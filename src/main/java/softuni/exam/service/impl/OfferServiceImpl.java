package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Offer;
import softuni.exam.models.dto.OfferRootDto;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.OfferService;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OfferServiceImpl implements OfferService {

    private static final String OFFERS_FILE_PATH = "src/main/resources/files/xml/offers.xml";

    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtill;
    private final XmlParser xmlParser;
    private final CarService carService;
    private final SellerService sellerService;

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, ValidationUtil validationUtill
            , XmlParser xmlParser, CarService carService, SellerService sellerService) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
        this.validationUtill = validationUtill;
        this.xmlParser = xmlParser;
        this.carService = carService;
        this.sellerService = sellerService;
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        OfferRootDto offerRootDto = xmlParser.fromFile(OFFERS_FILE_PATH,OfferRootDto.class);

        offerRootDto.getOffers().stream()
                .filter(dto -> {
                    boolean isValid = validationUtill.isValid(dto);

                    if (isValid) {
                        sb.append(String.format("Successfully import offer %s- %s",
                                dto.getAddedOn(),
                                dto.getHasGoldStatus()));
                    }

                    else {
                        sb.append("Invalid offer");
                    }

                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(dto -> {
                    Offer offer = modelMapper.map(dto,Offer.class);
                    offer.setCar(this.carService.getCarById(dto.getCar().getId()));
                    offer.setSeller(this.sellerService.getSellerById(dto.getSeller().getId()));

                    return offer;
                })
                .forEach(offerRepository::save);

        return sb.toString();
    }
}
