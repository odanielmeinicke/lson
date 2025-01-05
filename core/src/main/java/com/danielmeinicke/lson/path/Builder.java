package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.Json;
import com.danielmeinicke.lson.path.filter.Filter;
import com.danielmeinicke.lson.path.segment.Segment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static com.danielmeinicke.lson.path.filter.Filter.*;

public final class Builder {

    // Object

    private final @NotNull List<Object> nodes = new LinkedList<>();

    public Builder() {
    }

    // Nodes

    public @NotNull NodeBuilder node(@NotNull String name) {
        return new NodeBuilder(this, name);
    }
    @Contract(value = "_->this")
    public @NotNull Builder node(@NotNull Node node) {
        nodes.add(node);
        return this;
    }

    // Builder

    public @NotNull JsonPath build() {
        return null;
//        @NotNull List<Node> nodes = new LinkedList<>();
//
//        for (@NotNull Object object : this.nodes) {
//            if (object instanceof NodeBuilder) {
//                @NotNull NodeBuilder builder = (NodeBuilder) object;
//
//                nodes.add(new NodeImpl(builder.name, builder.selectors.toArray(new Selector[0])));
//            } else {
//                nodes.add((Node) object);
//            }
//        }
//
//        return new JsonPathImpl(nodes.toArray(new Node[0]));
    }

    // Classes

    public static final class NodeBuilder {

        // Object

        private final @NotNull Builder builder;

        private final @NotNull String name;
        private final @NotNull List<Selector> selectors = new LinkedList<>();

        public NodeBuilder(@NotNull Builder builder, @NotNull String name) {
            this.builder = builder;
            this.name = name;
        }

        // Operators

        @Contract(value = "_->this")
        public @NotNull NodeBuilder filter(@NotNull Filter filter) {
            selectors.add(filter);
            return this;
        }

        @Contract(value = "_,_,_->this")
        public @NotNull NodeBuilder array(int start, @Nullable Integer end, int step) {
            selectors.add(new SlicingImpl(start, end, step));
            return this;
        }
        @Contract(value = "->this")
        public @NotNull NodeBuilder array() {
            selectors.add(new SlicingImpl(0, null, 1));
            return this;
        }

        @Contract(value = "_->this")
        public @NotNull NodeBuilder index(int index) {
            selectors.add(new IndexImpl(index));
            return this;
        }

        @Contract(value = "->this")
        public @NotNull NodeBuilder wilcard() {
            selectors.add(new WildcardImpl());
            return this;
        }

        public @NotNull Builder build() {
            builder.nodes.add(this);
            return builder;
        }

    }

    static final class NodeImpl implements Node {

        private final @Nullable String name;
        private final @NotNull String original;

        private final @NotNull Segment @NotNull [] segments;
        private final @Nullable Type type;

        public NodeImpl(@Nullable String name, @NotNull String original, @NotNull Segment @NotNull [] segments, @Nullable Type type) {
            this.name = name;
            this.original = original;
            this.segments = segments;
            this.type = type;
        }

        // Getters

        @Override
        public @Nullable String getName() {
            return name;
        }

        @Override
        public @Nullable Type getType() {
            return type;
        }

        @Override
        public @NotNull Segment @NotNull [] getSegments() {
            return segments;
        }

        // Modules

        @Override
        public boolean contains(@NotNull Json json) {
            return false;
        }

        @Override
        public @Nullable Json get(@NotNull Json json) {
            return null;
        }

        // CharSequence

        @Override
        public int length() {
            return toString().length();
        }

        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Node)) return false;
            @NotNull Node node = (Node) object;
            return Objects.equals(getName(), node.getName()) && Objects.deepEquals(getSegments(), node.getSegments()) && getType() == node.getType();
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), Arrays.hashCode(getSegments()), getType());
        }

        @Override
        public @NotNull String toString() {
            return original;
        }

    }
    static final class NameImpl implements Name {

        private final @NotNull String string;

        public NameImpl(@NotNull String string) {
            this.string = string;
        }

        // CharSequence

        @Override
        public int length() {
            return toString().length();
        }
        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }
        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Name)) return false;
            @NotNull Name name = (Name) object;
            return Objects.equals(toString(), name.toString());
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(toString());
        }

        @Override
        public @NotNull String toString() {
            return string;
        }

    }
    static final class SlicingImpl implements Slicing {

        private final int start;
        private final @Nullable Integer end;
        private final int step;

        public SlicingImpl(int start, @Nullable Integer end, int step) {
            this.start = start;
            this.end = end;
            this.step = step;
        }

        // Getters

        @Override
        public int getStart() {
            return start;
        }
        @Override
        public @Nullable Integer getEnd() {
            return end;
        }
        @Override
        public int getStep() {
            return step;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Slicing)) return false;
            @NotNull Slicing that = (Slicing) object;
            return getStart() == that.getStart() && getStep() == that.getStep() && Objects.equals(getEnd(), that.getEnd());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getStart(), getEnd(), getStep());
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder(String.valueOf(getStart()));
            builder.append(":");

            if (getEnd() != null) {
                builder.append(getEnd());
            } if (getStep() != 1) {
                builder.append(":").append(getStep());
            }

            return builder.toString();
        }

    }
    static final class IndexImpl implements Index {

        private final int index;

        public IndexImpl(int index) {
            this.index = index;
        }

        // Getters

        public int getIndex() {
            return index;
        }

        // Modules

        @Override
        public int compareTo(@NotNull Integer o) {
            return index - o;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof IndexImpl)) return false;
            @NotNull Builder.IndexImpl that = (IndexImpl) object;
            return getIndex() == that.getIndex();
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(getIndex());
        }

        @Override
        public @NotNull String toString() {
            return String.valueOf(getIndex());
        }

    }
    static final class WildcardImpl implements Wildcard {

        // Object

        public WildcardImpl() {
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            return object instanceof Wildcard;
        }
        @Override
        public int hashCode() {
            return -1;
        }

        @Override
        public @NotNull String toString() {
            return "*";
        }

    }

    static final class JsonPathImpl implements JsonPath {

        // Object

        private final @NotNull Node @NotNull [] nodes;
        private final @NotNull String original;

        public JsonPathImpl(@NotNull Node @NotNull [] nodes, @NotNull String original) {
            this.nodes = nodes;
            this.original = original;
        }

        // Getters

        @Override
        public @NotNull Node @NotNull [] getNodes() {
            return nodes;
        }

        // Modules

        @Override
        public boolean contains(@NotNull Json json) {
            return false;
        }

        @Override
        public @Nullable Json get(@NotNull Json json) {
            return null;
        }

        // CharSequence

        @Override
        public int length() {
            return toString().length();
        }

        @Override
        public char charAt(int index) {
            return toString().charAt(index);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return toString().subSequence(start, end);
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof JsonPath)) return false;
            @NotNull JsonPath that = (JsonPath) object;
            return Objects.deepEquals(getNodes(), that.getNodes());
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(getNodes());
        }

        @Override
        public @NotNull String toString() {
            return original;
        }

    }

    static final class SegmentImpl implements Segment {

        private final @NotNull Selector @NotNull [] selectors;

        public SegmentImpl(@NotNull Selector selector) {
            this.selectors = new Selector[] { selector };
        }
        public SegmentImpl(@NotNull Repeatable @NotNull ... repeatables) {
            this.selectors = repeatables;
        }

        // Iterable and Stream

        @Override
        public @NotNull Stream<Selector> stream() {
            return Arrays.stream(selectors);
        }
        @Override
        public @NotNull Iterator<Selector> iterator() {
            return Arrays.asList(selectors).iterator();
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof Segment)) return false;
            Segment that = (Segment) object;
            return Objects.equals(iterator(), that.iterator());
        }
        @Override
        public int hashCode() {
            return Arrays.hashCode(selectors);
        }

        @Override
        public @NotNull String toString() {
            return Arrays.toString(selectors);
        }

    }

}
