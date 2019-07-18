package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import br.com.ffroliva.mimecast.validation.Validation;
import br.com.ffroliva.mimecast.validation.rule.IsValidPath;
import br.com.ffroliva.mimecast.validation.rule.ServerValidationRule;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class FileSearchService implements SearchService {

    @Override
    public Stream<SearchResponse> search(SearchRequest searchRequest) {
        try {
            File file = Paths.get(searchRequest.getRootPath()).toFile();
            Validation.execute(ServerValidationRule.of(searchRequest.getHost()));
            Validation.execute(IsValidPath.of(file));
            return StreamSupport
                    .stream(Files.fileTraverser()
                            .breadthFirst(file).spliterator(), true)
                    .filter(f -> f.isFile() && f.canRead())
                    .map(f -> this.searchFileContent(f, searchRequest.getSearchTerm()))
                    .sorted(Comparator.comparing(SearchResponse::getFilePath));
        } catch (Exception e) {
            throw new BusinessException(MessageProperty.INVALID_PATH
                    .bind(searchRequest.getRootPath()));
        }
    }

    private SearchResponse searchFileContent(File file, String searchTerm) {
        SearchResponse response;
        try (BufferedReader br = Files.newReader(file, Charset.defaultCharset())) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    countWordsInFile(searchTerm, br.lines()));
        } catch (Exception e) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    0);
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
