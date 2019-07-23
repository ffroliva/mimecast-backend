package br.com.ffroliva.mimecast.validation;

import br.com.ffroliva.mimecast.validation.rule.Rule;

public class Validation {

    public static void execute(Rule rule) {
        rule.run();
    }
}
