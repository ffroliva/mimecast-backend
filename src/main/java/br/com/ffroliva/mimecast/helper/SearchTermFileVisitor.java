package br.com.ffroliva.mimecast.helper;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Getter
@RequiredArgsConstructor
public class SearchTermFileVisitor extends SimpleFileVisitor<Path> {
    private final SearchRequest searchRequest;
    private final List<SearchResponse> searchResponseList;

    private SearchResponse searchFileContent(Path path, SearchRequest searchRequest) {
        SearchResponse response;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            response = SearchResponse.of(
                    searchRequest.getServer(),
                    Paths.get(path.toUri()).toString(),
                    countWordsInFile(searchRequest.getSearchTerm(), br.lines()),
                    false);
        } catch (Exception e) {
            response = SearchResponse.of(
                    searchRequest.getServer(),
                    path.toString(),
                    0,
                    true);
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

    private boolean isTextFile(Path path) throws IOException {
        String type = Files.probeContentType(path);
        if (type == null) {
            //type couldn't be determined, assume binary
            return false;
        } else //type isn't text
            return type.startsWith("text");
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        log.debug("Visited: " + (Path) dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()
                && !attrs.isDirectory()
                && !attrs.isSymbolicLink()
                && isTextFile(file)) {
            searchResponseList.add(searchFileContent(file, searchRequest));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
