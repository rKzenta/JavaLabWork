package org.nmu.labwork.services;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.hibernate.collection.spi.PersistentBag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nmu.labwork.models.Phone;
import org.nmu.labwork.models.PhoneSearch;
import org.nmu.labwork.repositories.PhoneSearchRepository;
import org.nmu.labwork.repositories.PhonesRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class SiteParsingService {
    final String SearchUrl = "https://www.foxtrot.com.ua/uk/search?query=%s+.&filter=_60&pFilter=%%2C60&page=%d";
    final PhoneSearchRepository phoneSearchRepository;
    final PhonesRepository phonesRepository;

    public SiteParsingService(PhoneSearchRepository phoneSearchRepository, PhonesRepository phonesRepository) {
        this.phoneSearchRepository = phoneSearchRepository;
        this.phonesRepository = phonesRepository;
    }

    @Transactional
    public ArrayList<Phone> getPhonesBySearch(String search) {
        Optional<PhoneSearch> cachedSearch = phoneSearchRepository.getFirstBySearchOrderByCreatedAtDesc(search);
        if (cachedSearch.isEmpty() || Duration.between(cachedSearch.get().getCreatedAt(), OffsetDateTime.now()).toMinutes() > 10) {
            cachedSearch.ifPresent(phoneSearchRepository::delete);
            PhoneSearch phoneSearch = new PhoneSearch();
            phoneSearch.setSearch(search);

            phoneSearch.getPhones().addAll(phonesRepository.saveAll(getCurrentPhonesBySearch(search)));
            cachedSearch = Optional.of(phoneSearchRepository.save(phoneSearch));
        }
        return new ArrayList<>(cachedSearch.get().getPhones());
    }

    @SneakyThrows
    private ArrayList<Phone> getCurrentPhonesBySearch(String search) {
        ArrayList<Phone> phones = new ArrayList<>();
        int n = 10;
        for (int i = 1; i <= n; i++) {
            Document document = Jsoup.connect(SearchUrl.formatted(search, i)).get();
            if (i == 1) {
                n = Integer.min(n, Integer.parseInt(document.getElementsByClass("listing__pagination").attr("data-pages-count")));
            }

            for (Element phoneElement : document.getElementsByClass("sc-product")) {
                Phone phone = new Phone();

                Element itemNameWithUrl = phoneElement.getElementsByClass("card__title").getFirst();
                if (!itemNameWithUrl.text().toLowerCase().contains(search.toLowerCase())) continue;
                phone.setName(itemNameWithUrl.text());
                phone.setUrl("https://www.foxtrot.com.ua" + itemNameWithUrl.attr("href"));
                Elements priceElements = phoneElement.getElementsByClass("card-price");
                if (priceElements.isEmpty()) continue;
                phone.setPrice(Integer.parseInt(priceElements.getFirst().text().replaceAll("\\D", "")));

                phones.add(phone);
            }
        }
        return phones;
    }
}