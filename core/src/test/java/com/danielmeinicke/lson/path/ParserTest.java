package com.danielmeinicke.lson.path;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public final class ParserTest {

    @Test
    @Order(0)
    @DisplayName("Complex Parsing (Without Verification)")
    void basic() {
        @NotNull String path = "$['library']..books[?(@.pages > 100 && (@.edition == 'Second' && @.version == '1.0') && @.pages < 400)].authors[?(@.country == 'US')][0:3:1]..[?(@['name'] && @.age >= 30)]..details['bio','contact'].social[?(@.followers > 1000)]..posts[0:][::2]['id','title'][?(@.name)]";
        JsonPath.parse(path);
    }

}
