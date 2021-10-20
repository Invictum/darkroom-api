package com.github.darkroom.cases.broker;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComparisonTask(
        @JsonProperty(value = "id", required = true) long id,
        @JsonProperty(value = "base", required = true) byte[] base,
        @JsonProperty(value = "sample", required = true) byte[] sample) {
}
