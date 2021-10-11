package com.github.darkroom.cases.controller;

import java.util.Map;

public record CaseMetadata(String classifier, Map<String, String> attributes) {
}
