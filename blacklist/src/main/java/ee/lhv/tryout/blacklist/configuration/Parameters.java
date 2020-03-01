package ee.lhv.tryout.blacklist.configuration;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class Parameters {
    @Getter
    private List<String> blackList;
    @Getter
    private List<String> noiseWords;

    @PostConstruct
    public void init() throws IOException {
        initBlackList();
        initNoiseWords();
    }

    private void initBlackList() throws IOException {
        Resource resource = new ClassPathResource("names.txt");
        blackList = Files.readAllLines(Paths.get(resource.getURI()));
    }

    private void initNoiseWords() throws IOException {
        Resource resource = new ClassPathResource("noise_words.txt");
        noiseWords = Files.readAllLines(Paths.get(resource.getURI()));
    }
}
