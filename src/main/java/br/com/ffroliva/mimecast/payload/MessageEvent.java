package br.com.ffroliva.mimecast.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageEvent<T extends Data> {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private final String type;
    private final T data;

    public static final MessageEvent success(Data data) {
        return new MessageEvent<>(SUCCESS, data);
    }

    public static final MessageEvent error(Data data){
        return new MessageEvent<>(ERROR, data);
    }
}
