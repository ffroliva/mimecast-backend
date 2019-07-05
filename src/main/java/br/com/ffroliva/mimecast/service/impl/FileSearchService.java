package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSearchService implements SearchService {

    @Override
    public void search(String rootPath, String searchWord) {
        try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(p -> log.debug(p.toString()));
        } catch (IOException e) {
            new BusinessException("Erro while reading files: " + e.getMessage());
        }
    }

}
