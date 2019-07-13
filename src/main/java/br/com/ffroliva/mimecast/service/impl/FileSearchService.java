package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.helper.SearchTermFileVisitor;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import br.com.ffroliva.mimecast.validation.Validation;
import br.com.ffroliva.mimecast.validation.rule.ServerValidationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static br.com.ffroliva.mimecast.config.properties.MessageProperty.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class FileSearchService implements SearchService {

    @Override
    public List<SearchResponse> search(SearchRequest searchRequest) {
        Validation.execute(ServerValidationRule.of(searchRequest.getServer()));
        Path start = Paths.get(searchRequest.getRootPath());
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        int maxDepth = Integer.MAX_VALUE;
        SearchTermFileVisitor visitor = new SearchTermFileVisitor(searchRequest, new ArrayList<>());
        try {
            Files.walkFileTree(start,opts,maxDepth, visitor);
            return visitor.getSearchResponseList();
        } catch (IOException e) {
            throw new BusinessException(INTERNAL_SERVER_ERROR.bind(e.getMessage()));
        }
    }

}
