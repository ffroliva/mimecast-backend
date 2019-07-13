package br.com.ffroliva.mimecast.payload;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SearchRequest implements Serializable {
    @NotNull
    private String server;
    @NotNull
    private String rootPath;
    @NotNull
    private String searchTerm;
}
