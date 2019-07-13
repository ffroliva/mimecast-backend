package br.com.ffroliva.mimecast.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ToString
@Getter
@RequiredArgsConstructor(staticName = "of")
public class SearchRequest implements Serializable {
    @NotNull
    private final String server;
    @NotNull
    private final String rootPath;
    @NotNull
    private final String searchTerm;
}
