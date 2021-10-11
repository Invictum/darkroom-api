package com.github.darkroom.cases.controller;

import java.time.LocalDateTime;

public record CaseResponse(Long id, String classifier, String file, String base, String delta, Integer diffPercent,
                           LocalDateTime timestamp) {
}
