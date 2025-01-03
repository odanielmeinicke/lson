package com.danielmeinicke.lson.path;

import com.danielmeinicke.lson.JsonPrimitive;
import com.danielmeinicke.lson.path.JsonPath.Comparator;
import com.danielmeinicke.lson.path.JsonPath.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        @NotNull List<Node> nodes = new LinkedList<>();

        for (@NotNull Object object : this.nodes) {
            if (object instanceof NodeBuilder) {
                @NotNull NodeBuilder builder = (NodeBuilder) object;

                nodes.add(new NodeImpl(
                        nodes,
                        nodes.size(),
                        builder.name,
                        builder.operators.toArray(new Operator[0])
                ));
            } else {
                nodes.add((Node) object);
            }
        }

        return new JsonPathImpl(nodes.toArray(new Node[0]));
    }

    // Classes

    public static final class NodeBuilder {

        // Object

        private final @NotNull Builder builder;

        private final @NotNull String name;
        private final @NotNull List<Operator> operators = new LinkedList<>();

        public NodeBuilder(@NotNull Builder builder, @NotNull String name) {
            this.builder = builder;
            this.name = name;
        }

        // Operators

        @Contract(value = "_,_,_->this")
        public @NotNull NodeBuilder logical(@NotNull LogicalComparator.Type type, @NotNull Comparator first, @NotNull Comparator second) {
            operators.add(new LogicalComparatorImpl(type, first, second));
            return this;
        }
        @Contract(value = "_,_,_->this")
        public @NotNull NodeBuilder primitive(@NotNull PrimitiveComparator.Type type, @NotNull Node node, @Nullable JsonPrimitive primitive) {
            operators.add(new PrimitiveComparatorImpl(type, node, primitive));
            return this;
        }

        @Contract(value = "_,_,_->this")
        public @NotNull NodeBuilder array(int start, @Nullable Integer end, int step) {
            operators.add(new ArraySliceImpl(start, end, step));
            return this;
        }
        @Contract(value = "->this")
        public @NotNull NodeBuilder array() {
            operators.add(new ArraySliceImpl(0, null, 1));
            return this;
        }

        @Contract(value = "_->this")
        public @NotNull NodeBuilder index(int index) {
            operators.add(new ArrayPositionImpl(index));
            return this;
        }

        @Contract(value = "->this")
        public @NotNull NodeBuilder wilcard() {
            operators.add(new WildcardImpl());
            return this;
        }

        public @NotNull Builder build() {
            builder.nodes.add(this);
            return builder;
        }

    }

    private static final class NodeImpl implements Node {

        private final @NotNull List<Node> nodes;
        private final int index;

        private final @NotNull String name;

        private final @NotNull Operator @NotNull [] operators;

        public NodeImpl(@NotNull List<Node> nodes, int index, @NotNull String name, @NotNull Operator @NotNull [] operators) {
            this.nodes = nodes;
            this.index = index;
            this.name = name;
            this.operators = operators;
        }

        // Getters

        public @NotNull String getName() {
            return name;
        }

        @Override
        public @Nullable Node getParent() {
            return (index > 0 && index <= nodes.size()) ? nodes.get(index - 1) : null;
        }
        @Override
        public @Nullable Node getChildren() {
            return (index >= 0 && index + 1 < nodes.size()) ? nodes.get(index + 1) : null;
        }

        // Operators

        @Override
        public @NotNull Operator @NotNull [] getOperators() {
            return operators;
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
            if (!(object instanceof NodeImpl)) return false;
            @NotNull NodeImpl node = (NodeImpl) object;
            return Objects.equals(getName(), node.getName()) && Objects.deepEquals(getOperators(), node.getOperators()) && Objects.equals(getParent(), node.getParent()) && Objects.equals(getChildren(), node.getChildren());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName(), getChildren(), getParent(), Arrays.hashCode(getOperators()));
        }

        @Override
        public @NotNull String toString() {
            @NotNull StringBuilder builder = new StringBuilder();

            // Add parent's names recursively from first
            @Nullable Node parent = getParent();

            while (parent != null) {
                builder.append(parent.getName());
                builder.append(".");

                parent = parent.getParent();
            }

            // Flip parents
            // todo: will split dots and brackets inside quotes also, this should not happen
            @NotNull List<String> list = Arrays.asList(builder.toString().split("\\.|\\[]"));
            Collections.reverse(list);

            builder = new StringBuilder(list.stream().map(s -> s + ".").collect(Collectors.joining()));

            // Add node name
            builder.append(getName());

            // Finish
            return builder.toString();
        }

    }
    private static final class LogicalComparatorImpl implements LogicalComparator {

        // Object

        private final @NotNull Type type;

        private final @NotNull Comparator first;
        private final @NotNull Comparator second;

        public LogicalComparatorImpl(@NotNull Type type, @NotNull Comparator first, @NotNull Comparator second) {
            this.type = type;

            this.first = first;
            this.second = second;
        }

        // Getters

        @Override
        public @NotNull Type getType() {
            return type;
        }

        @Override
        public @NotNull Comparator getFirst() {
            return first;
        }
        @Override
        public @NotNull Comparator getSecond() {
            return second;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof LogicalComparatorImpl)) return false;
            @NotNull LogicalComparatorImpl that = (LogicalComparatorImpl) object;
            return getType() == that.getType() && Objects.equals(getFirst(), that.getFirst()) && Objects.equals(getSecond(), that.getSecond());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getType(), getFirst(), getSecond());
        }

        @Override
        public @NotNull String toString() {
            @NotNull Function<Comparator, String> replacer = new Function<Comparator, String>() {
                @Override
                public @NotNull String apply(@NotNull Comparator comparator) {
                    // todo: will split dots and brackets inside quotes also, this should not happen
                    @NotNull String string = comparator.toString().replaceFirst("\\?\\(|\\)", "");

                    if (comparator instanceof LogicalComparator) {
                        string = "(" + string + ")";
                    }

                    return string;
                }
            };

            return "?(" + replacer.apply(getFirst()) + " " + getType() + " " + replacer.apply(getSecond()) + ")";
        }

    }
    private static final class PrimitiveComparatorImpl implements PrimitiveComparator {

        // Object

        private final @NotNull Type type;

        private final @NotNull Node node;
        private final @Nullable JsonPrimitive primitive;

        public PrimitiveComparatorImpl(@NotNull Type type, @NotNull Node node, @Nullable JsonPrimitive primitive) {
            this.type = type;
            this.node = node;
            this.primitive = primitive;
        }

        // Getters

        @Override
        public @NotNull Type getType() {
            return type;
        }

        @Override
        public @NotNull Node getNode() {
            return node;
        }
        @Override
        public @Nullable JsonPrimitive getPrimitive() {
            return primitive;
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) return true;
            if (!(object instanceof PrimitiveComparatorImpl)) return false;
            @NotNull PrimitiveComparatorImpl that = (PrimitiveComparatorImpl) object;
            return getType() == that.getType() && Objects.equals(getNode(), that.getNode()) && Objects.equals(getPrimitive(), that.getPrimitive());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getType(), getNode(), getPrimitive());
        }

        @Override
        public @NotNull String toString() {
            return "?(" + getNode() + " " + getType().getString() + " " + getPrimitive() + ")";
        }

    }
    private static final class ArraySliceImpl implements ArraySlice {

        private final int start;
        private final @Nullable Integer end;
        private final int step;

        public ArraySliceImpl(int start, @Nullable Integer end, int step) {
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
            if (!(object instanceof ArraySlice)) return false;
            @NotNull ArraySlice that = (ArraySlice) object;
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
    private static final class ArrayPositionImpl implements ArrayPosition {

        private final int index;

        public ArrayPositionImpl(int index) {
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
            if (!(object instanceof ArrayPositionImpl)) return false;
            @NotNull ArrayPositionImpl that = (ArrayPositionImpl) object;
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
    private static final class WildcardImpl implements Wildcard {

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

    private static final class JsonPathImpl implements JsonPath {

        // Object

        private final @NotNull Node @NotNull [] nodes;

        public JsonPathImpl(@NotNull Node @NotNull [] nodes) {
            this.nodes = nodes;
        }

        // Getters

        @Override
        public @NotNull Node @NotNull [] getNodes() {
            return nodes;
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
            @NotNull StringBuilder builder = new StringBuilder();
            @NotNull Node[] nodes = getNodes();

            int row = 0;
            for (@NotNull Node node : nodes) {
                // Check last
                boolean last = row + 1 == nodes.length;
                row++;

                // Start serializing
                builder.append(node.getName());

                for (@NotNull Operator operator : node.getOperators()) {
                    builder.append("[").append(operator).append("]");
                }

                if (!last) builder.append(".");
            }

            return builder.toString();
        }

    }

}
