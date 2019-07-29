package br.com.ffroliva.mimecast.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
