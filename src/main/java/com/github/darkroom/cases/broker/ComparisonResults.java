package com.github.darkroom.cases.broker;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComparisonResults(
        @JsonProperty(value = "id", required = true) long id,
        @JsonProperty(value = "image", required = true) byte[] image,
        @JsonProperty(value = "delta", required = true) int delta) {
}
