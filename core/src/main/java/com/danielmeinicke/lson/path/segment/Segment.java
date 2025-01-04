package com.danielmeinicke.lson.path.segment;

import com.danielmeinicke.lson.path.Selector;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface Segment extends Iterable<Selector> {

    @NotNull Stream<Selector> stream();

}
