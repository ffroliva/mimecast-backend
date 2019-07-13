package br.com.ffroliva.mimecast.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@RequiredArgsConstructor(staticName = "of")
public class SearchResponse implements Serializable {

    private final String server;
    private final String filePath;
    private final long count;
    private final boolean errorReadingFile;
}
