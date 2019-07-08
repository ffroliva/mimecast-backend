package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import br.com.ffroliva.mimecast.validation.Validation;
import br.com.ffroliva.mimecast.validation.rule.ServerValidationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.ffroliva.mimecast.config.properties.MessageProperty.ACCESS_DENIED;
import static br.com.ffroliva.mimecast.config.properties.MessageProperty.INVALID_PATH;

@Slf4j
@Service
public class FileSearchService implements SearchService {

    @Override
    public List<SearchResponse> search(SearchRequest searchRequest) {
        Validation.execute(ServerValidationRule.of(searchRequest.getServer()));
        try (Stream<Path> paths = Files.walk(Paths.get(searchRequest.getRootPath()))) {
            return paths
                    .parallel()
                    .filter(Files::isRegularFile)
                    .map(path -> this.searchFileContent(path, searchRequest.getSearchTerm()))
                    .sorted(Comparator.comparing(SearchResponse::getFilePath))
                    .collect(Collectors.toList());
        } catch (AccessDeniedException e) {
            throw new BusinessException(INVALID_PATH.bind(e.getMessage()));
        } catch (IOException e) {
            throw new BusinessException(ACCESS_DENIED.bind(e.getMessage()));
        }
    }

    private SearchResponse searchFileContent(Path path, String searchTerm) {
        SearchResponse response;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            response = SearchResponse.of(
                    path.toString(),
                    countWordsInFile(searchTerm, br.lines()));
        } catch (Exception e) {
            response = SearchResponse.of(
                    path.toString(),
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
