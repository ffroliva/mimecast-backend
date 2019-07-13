package br.com.ffroliva.mimecast.payload;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SearchResponse implements Serializable {

    private String server;
    private String filePath;
    private long count;
    private boolean errorReadingFile;
}
