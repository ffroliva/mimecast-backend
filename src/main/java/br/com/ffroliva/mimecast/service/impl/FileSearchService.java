package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import br.com.ffroliva.mimecast.validation.Validation;
import br.com.ffroliva.mimecast.validation.rule.IsValidPath;
import br.com.ffroliva.mimecast.validation.rule.ServerValidationRule;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSearchService implements SearchService {

    private final ApplicationProperties applicationProperties;

    @Override
    public Flux<SearchResponse> search(SearchRequest searchRequest) {
        try {
            File file = Paths.get(searchRequest.getRootPath()).toFile();
            Validation.execute(ServerValidationRule
                    .of(searchRequest.getServer(), applicationProperties.getServersAsSet()));
            Validation.execute(IsValidPath.of(file, searchRequest.getServer()));
            return Flux.fromIterable(Files.fileTraverser()
                            .breadthFirst(file))
                    .filter(f -> f.isFile() && f.canRead())
                    .map(f -> this.searchFileContent(f, searchRequest))
                    .delayElements(Duration.ofMillis(300));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(MessageProperty.INTERNAL_SERVER_ERROR
                    .bind(searchRequest.getRootPath()));
        }
    }

    private SearchResponse searchFileContent(File file, SearchRequest searchRequest) {
        SearchResponse response;
        try (BufferedReader br = Files.newReader(file, Charset.defaultCharset())) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    countWordsInFile(searchRequest.getSearchTerm(), br.lines()),
                    searchRequest.getServer());
        } catch (Exception e) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    0,
                    searchRequest.getServer());
        }
        log.debug(response.toString());
        return response;
    }

    private int countWordsInFile(String searchTerm, Stream<String> linesStream) {
        return linesStream
                .parallel()
                .map(line -> countWordsInLine(line, searchTerm))
                .reduce(0, Integer::sum);
    }

    private int countWordsInLine(String line, String searchTerm) {
        Pattern pattern = Pattern.compile(searchTerm.toLowerCase());
        Matcher matcher = pattern.matcher(line.toLowerCase());

        int count = 0;
        int i = 0;
        while (matcher.find(i)) {
            count++;
            i = matcher.start() + 1;
        }
        return count;
    }


}
